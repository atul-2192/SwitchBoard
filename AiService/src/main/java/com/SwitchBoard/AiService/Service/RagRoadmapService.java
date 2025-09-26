package com.SwitchBoard.AiService.Service;

import com.SwitchBoard.AiService.Dto.RoadmapRequest;
import com.SwitchBoard.AiService.Dto.RoadmapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class RagRoadmapService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final ObjectMapper mapper;

    public RagRoadmapService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
        this.mapper = new ObjectMapper();
        this.mapper.findAndRegisterModules(); // for LocalDateTime
    }

    public RoadmapResponse generateRoadmap(RoadmapRequest req) throws JsonProcessingException {

        // Step 1: Build query
        String userQuery = String.format(
                "Roadmap for subject %s proficiency %s within %d days at %d hours per day",
                req.getSubject(), req.getProficiency(), req.getTotalDays(), req.getDailyHours()
        );

        // Step 2: Fetch candidate chunks from vector store
        SearchRequest searchRequest = SearchRequest.builder()
                .query(userQuery)
                .topK(20)
                .build();

        List<Document> candidates = vectorStore.similaritySearch(searchRequest);
        log.info("Candidate Topics fetched: {}", candidates.size());

        // Step 3: Conservative filtering
        int availableHours = req.getTotalDays() * req.getDailyHours();

        List<Document> filtered = candidates.stream()
                .map(doc -> {
                    Map<String, Object> meta = doc.getMetadata();

                    int hoursRequired = (int) meta.getOrDefault("hours_required", 1);
                    int priority = (int) meta.getOrDefault("priority", 5);
                    List<String> profLevels = (List<String>) meta.getOrDefault("proficiency_level", List.of("Beginner"));

                    // Skip if time doesn’t fit
                    if (hoursRequired > availableHours) return null;

                    // Conservative filter: if user level not in proficiencies → reduce priority
                    if (!profLevels.contains(req.getProficiency().name())) {
                        meta.put("priority", Math.max(1, priority - 2));
                    }

                    return doc;
                })
                .filter(Objects::nonNull)
                .toList();

        log.info("Filtered Topics after conservative pass: {}", filtered.size());

        // Step 4: System prompt for schema binding
        String systemPrompt = """
You are an AI mentor.
Generate a **study roadmap** in JSON format that matches exactly this schema:

RoadmapResponse {
   String title;
   String description;
   LocalDateTime deadline (ISO-8601);
   List<Task> tasks;

   Task {
      String title;
      String description;
      int rewardPoints;
      LocalDateTime deadline (ISO-8601);
      String titleColor (hex or string);
      int order;
   }
}

Rules:
- Respect user proficiency but sprinkle some higher-level topics for stretch learning.
- Use "subtopics" and "important_points" from candidate topics to enrich task descriptions.
- Always prioritize topics with higher `interview_importance`.
- Allocate hours proportionally using `hours_required` vs available time.
- Order tasks logically for learning progression.
- Return only valid JSON strictly matching the schema.
""";

        // Step 5: User prompt with candidate data
        String userPrompt = """
User request:
Subject: %s
Proficiency: %s
Total Days: %d
Hours per Day: %d

Candidate topics (from vector store, JSON):
%s
""".formatted(
                req.getSubject(),
                req.getProficiency(),
                req.getTotalDays(),
                req.getDailyHours(),
                toJsonSafe(filtered)
        );

        log.info("System Prompt: {}", systemPrompt);
        log.info("User Prompt: {}", userPrompt);

        // Step 6: Call LLM
        String json = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        log.info("RAW AI response: {}", json);

        // Step 7: Parse response into RoadmapResponse
        try {
            return mapper.readValue(json, RoadmapResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse AI response into RoadmapResponse. Raw response: {}", json, e);
            throw new JsonProcessingException("Invalid AI response format: " + e.getMessage()) {};
        }
    }

    private String toJsonSafe(List<Document> docs) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(docs);
        } catch (Exception e) {
            return docs.toString();
        }
    }
}


