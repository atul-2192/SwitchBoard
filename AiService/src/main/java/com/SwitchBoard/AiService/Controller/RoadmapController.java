package com.SwitchBoard.AiService.Controller;


import com.SwitchBoard.AiService.Dto.RoadmapRequest;
import com.SwitchBoard.AiService.Dto.RoadmapResponse;
import com.SwitchBoard.AiService.Service.AiRoadmapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
@Slf4j
public class RoadmapController {

    private final AiRoadmapService aiService;

    @PostMapping
    public ResponseEntity<RoadmapResponse> generate(@Valid @RequestBody RoadmapRequest request) {
        log.info("Received roadmap generation request: {}", request);
        var roadmap = aiService.generate(request);
        return ResponseEntity.ok(roadmap);
    }
}
