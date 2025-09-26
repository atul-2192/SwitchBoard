package com.SwitchBoard.AiService.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RagService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Uploads roadmap JSON (by filename in resources/ragdata/) into pgvector.
     * Stores both granular chunks (subtopics, points, etc.) AND the full topic JSON as metadata.
     */
    public int uploadJsonToPgVector(String filename) {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("ragdata/" + filename + ".json");

            if (inputStream == null) {
                throw new RuntimeException("File not found: ragdata/" + filename + ".json");
            }

            JsonNode rootArray = objectMapper.readTree(inputStream);
            List<Document> documents = new ArrayList<>();

            for (JsonNode node : rootArray) {
                String topic = node.path("topic").asText("Unknown Topic");

                // Convert full JSON node to string (so we can always rehydrate the full object)
                String fullJson = objectMapper.writeValueAsString(node);

                // Common parent metadata
                Map<String, Object> baseMeta = new HashMap<>();
                baseMeta.put("topic", topic);
                baseMeta.put("hoursRequired", node.path("hours_required").asInt(0));
                baseMeta.put("priority", node.path("priority").asInt(0));
                baseMeta.put("interviewImportance", node.path("interview_importance").asInt(0));
                baseMeta.put("proficiencyLevel", node.has("proficiency_level")
                        ? objectMapper.convertValue(node.get("proficiency_level"), List.class)
                        : Collections.emptyList());
                baseMeta.put("fullTopicJson", fullJson);

                // Subtopics
                if (node.has("subtopics")) {
                    for (JsonNode sub : node.get("subtopics")) {
                        Map<String, Object> meta = new HashMap<>(baseMeta);
                        meta.put("type", "subtopic");
                        documents.add(new Document("Subtopic: " + sub.asText(), meta));
                    }
                }

                // Important points
                if (node.has("important_points")) {
                    for (JsonNode point : node.get("important_points")) {
                        Map<String, Object> meta = new HashMap<>(baseMeta);
                        meta.put("type", "important_point");
                        documents.add(new Document("Important: " + point.asText(), meta));
                    }
                }

                // Proficiency (still stored as separate chunks, though already in metadata)
                if (node.has("proficiency_level")) {
                    for (JsonNode level : node.get("proficiency_level")) {
                        Map<String, Object> meta = new HashMap<>(baseMeta);
                        meta.put("type", "proficiency");
                        documents.add(new Document("Proficiency Level: " + level.asText(), meta));
                    }
                }

                // Summary (acts as a parent anchor for topic retrieval)
                {
                    Map<String, Object> meta = new HashMap<>(baseMeta);
                    meta.put("type", "summary");
                    documents.add(new Document(
                            "Topic: " + topic +
                                    " | Hours: " + node.path("hours_required").asInt(0) +
                                    " | Priority: " + node.path("priority").asInt(0) +
                                    " | Interview Importance: " + node.path("interview_importance").asInt(0),
                            meta));
                }
            }

            // Push to pgvector
            vectorStore.add(documents);

            return documents.size();

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload JSON into pgvector", e);
        }
    }
}
