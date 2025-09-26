package com.SwitchBoard.AiService.Controller;



import com.SwitchBoard.AiService.Domain.Proficiency;
import com.SwitchBoard.AiService.Dto.RoadmapRequest;
import com.SwitchBoard.AiService.Service.RagRoadmapService;
import com.SwitchBoard.AiService.Service.RagService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@RequiredArgsConstructor
@Slf4j
public class RagController {

    private final RagService ragService;
    private final RagRoadmapService ragRoadmapService;


    @PostMapping("/upload/{filename}")
    public ResponseEntity<String> uploadRagData(@PathVariable String filename) {
        try {
            int count = ragService.uploadJsonToPgVector(filename);
            return ResponseEntity.ok("✅ Uploaded " + count + " documents from " + filename);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to upload: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public   ResponseEntity<String> downloadRagData() throws JsonProcessingException {
        // Implementation for downloading RAG data
        log.info("Download RAG data request received.");
        ragRoadmapService.generateRoadmap(new RoadmapRequest("Spring Boot",30,5, Proficiency.BEGINNER));
        return ResponseEntity.ok("Download endpoint is not yet implemented.");
    }
}
