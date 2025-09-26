package com.SwitchBoard.Roadmap.Controller;

import com.SwitchBoard.Roadmap.Entity.RoadMapAssignment;
import com.SwitchBoard.Roadmap.Service.RoadMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Roadmap")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RoadmapController {

    private final RoadMapService roadMapService;

    public RoadmapController(RoadMapService roadMapService) {
        this.roadMapService = roadMapService;
    }

    @PostMapping
    public ResponseEntity<RoadMapAssignment> createAssignment(@RequestBody RoadMapAssignment assignment) {
        RoadMapAssignment savedAssignment = roadMapService.saveAssignment(assignment);
        return ResponseEntity.ok(savedAssignment);
    }

    @GetMapping
    public List<RoadMapAssignment> getAllAssignments() {
        return roadMapService.getAllAssignments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoadMapAssignment> getAssignmentById(@PathVariable Long id) {
        return roadMapService.getAssignmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        roadMapService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}
