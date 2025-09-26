package com.SwitchBoard.AiService.Service;



import com.SwitchBoard.AiService.Domain.Assignment;
import com.SwitchBoard.AiService.Domain.PriorityColor;
import com.SwitchBoard.AiService.Domain.Subtopic;
import com.SwitchBoard.AiService.Domain.Task;
import com.SwitchBoard.AiService.Dto.RoadmapRequest;
import com.SwitchBoard.AiService.Dto.RoadmapResponse;
import com.SwitchBoard.AiService.Util.PriorityColorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.ai.openai.api.OpenAiApi;


@Service
@RequiredArgsConstructor
@Slf4j
public class AiRoadmapService {

    private final ChatClient chatClient;



    // Minimal few-shot example to anchor structure – very small for token efficiency.
    private static final String FEW_SHOT = """
        Example Input: T=10, H=2, P=BEGINNER, Subject="Git"
        Example Output:
        {"assignment":{"t":"Git Foundations","desc":"Master core Git CLI & workflows","d":10},
         "tasks":[
          {"t":"Init & Snapshots","desc":"git init/add/commit/status/log","subtopics":["init","add","commit"],"points":40,"color":"RED","d":2,"order":1},
          {"t":"Branching","desc":"create/merge branches, resolve conflicts","subtopics":["branch","merge","diff"],"points":50,"color":"ORANGE","d":4,"order":2},
          {"t":"Remote Basics","desc":"push/pull/fetch & PR flow","subtopics":["remote","push","pull"],"points":60,"color":"YELLOW","d":4,"order":3}
         ]}
        """;

    public RoadmapResponse generate(RoadmapRequest req) {
        String system = loadSystemPrompt() + "\n" +
                "\nRespond with STRICT JSON ONLY. No explanations. No markdown. Follow exactly this schema:\n" +
                "{ \"title\": \"...\", \"description\": \"...\", \"deadline\": \"...\", \"tasks\": [ { \"title\": \"...\", \"description\": \"...\", \"rewardPoints\": 0, \"deadline\": \"...\", \"titleColor\": \"...\", \"order\": 1 } ] }";


        // Compact user message to save tokens
        String user = String.format(Locale.ROOT,
                "Subject=\"%s\"; T=%d days; H=%d h/day; P=%s. ",
                req.getSubject(), req.getTotalDays(), req.getDailyHours(), req.getProficiency());





log.info( "System Prompt: {}", system);
log.info( "User Prompt: {}", user);

        var raw = chatClient.prompt()
                .system(system)
                .user(user)
                .call()
                .content(); // raw text

        log.info("RAW AI response: {}", raw);

//        var response= chatClient.prompt()
//                .system(system)
//                .user(user)
//                .call()
//                .entity(RoadmapResponse.class);
//        log.info("AI response: {}", response);
        return new RoadmapResponse();

//        // Parse compact JSON
//        Map<String, Object> json = parseJson(response);
//
//        Assignment assignment = toAssignment(json);
//        // Ensure total days not exceeding request; if exceed, scale by merging last items lightly
//        assignment = enforceTotalDays(assignment, req.getTotalDays());
//
//        // Compute ordered chain deadlines
//        LocalDate start = (req.getStartDate() != null) ? req.getStartDate() : null;
//        DeadlinePlanner.chainDeadlines(assignment, start);
//
//        return RoadmapResponse.builder().assignment(assignment).build();
    }

    @SuppressWarnings("unchecked")
    private Assignment toAssignment(Map<String, Object> json) {
        Map<String, Object> a = (Map<String, Object>) json.get("assignment");
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) json.get("tasks");

        Assignment assignment = Assignment.builder()
                .title(reqString(a, "t", "Assignment"))
                .description(reqString(a, "desc", ""))
                .deadline(reqInt(a, "d", 7))
                .build();

        List<Task> mapped = tasks.stream().map(m -> Task.builder()
                .title(reqString(m, "t", "Task"))
                .description(reqString(m, "desc", ""))
                .subtopics(toSubtopics((List<Object>) m.getOrDefault("subtopics", List.of())))
                .rewardPoints(reqInt(m, "points", 50))
                .titleColor(PriorityColorUtil.normalize(reqString(m, "color", "YELLOW")))
                .deadline(reqInt(m, "d", 1))
                .order(reqInt(m, "order", 999))
                .build()
        ).sorted(Comparator.comparingInt(Task::getOrder)).collect(Collectors.toList());

        // Normalize ordering if missing or duplicates
        for (int i = 0; i < mapped.size(); i++) {
            if (mapped.get(i).getOrder() <= 0 || mapped.get(i).getOrder() == 999) {
                mapped.get(i).setOrder(i + 1);
            }
        }
        assignment.setTasks(mapped);
        return assignment;
    }

    private Assignment enforceTotalDays(Assignment assignment, int maxDays) {
        int sum = assignment.getTasks().stream().mapToInt(Task::getDeadline).sum();
        if (sum <= maxDays) {
            // keep assignment.deadline as max(sum, assignment.deadline)
            assignment.setDeadline(Math.max(assignment.getDeadline(), sum));
            return assignment;
        }
        // Soft shrink from the tail: reduce low-priority (GREEN/BLUE) first
        List<Task> tasks = new ArrayList<>(assignment.getTasks());
        tasks.sort(Comparator.comparing((Task t) -> priorityWeight(t.getTitleColor())).reversed()); // shrink lowest first
        int over = sum - maxDays;
        for (int i = tasks.size() - 1; i >= 0 && over > 0; i--) {
            Task t = tasks.get(i);
            int reducible = Math.max(0, t.getDeadline() - 1);
            int delta = Math.min(reducible, over);
            t.setDeadline(t.getDeadline() - delta);
            over -= delta;
        }
        // Restore original order
        tasks.sort(Comparator.comparingInt(Task::getOrder));
        assignment.setTasks(tasks);
        assignment.setDeadline(maxDays);
        return assignment;
    }

    private int priorityWeight(PriorityColor color) {
        return switch (color) {
            case RED -> 5;
            case ORANGE -> 4;
            case YELLOW -> 3;
            case BLUE -> 2;
            case GREEN -> 1;
        };
    }

    private List<Subtopic> toSubtopics(List<Object> raw) {
        if (raw == null) return List.of();
        return raw.stream().map(o -> new Subtopic(String.valueOf(o))).toList();
    }

    private String loadSystemPrompt() {
        try {
            var res = new ClassPathResource("prompts/roadmap-system-prompt.txt");
            var topics= new ClassPathResource("prompts/SpringBoot-prompt.txt");
            return new String(res.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    + "\n"
                    + new String(topics.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return "You are an expert curriculum planner.";
        }
    }

    // --- Minimal JSON parsing without extra libs ---
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String json) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            throw new IllegalStateException("AI did not return valid JSON", e);
        }
    }

    private String reqString(Map<String, Object> m, String key, String def) {
        Object v = (m != null) ? m.get(key) : null;
        return v != null ? String.valueOf(v) : def;
    }
    private int reqInt(Map<String, Object> m, String key, int def) {
        Object v = (m != null) ? m.get(key) : null;
        if (v instanceof Number n) return n.intValue();
        try { return v != null ? Integer.parseInt(String.valueOf(v)) : def; } catch (Exception e) { return def; }
    }
}
