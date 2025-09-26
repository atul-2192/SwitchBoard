package com.SwitchBoard.Roadmap.Service;

import com.SwitchBoard.Roadmap.Entity.RoadMapAssignment;
import com.SwitchBoard.Roadmap.Entity.Task;
import com.SwitchBoard.Roadmap.Repository.RoadMapRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoadMapService {



    private final RoadMapRepository roadMapRepository;

    public RoadMapService(RoadMapRepository roadMapRepository) {
        this.roadMapRepository = roadMapRepository;
    }

    public RoadMapAssignment saveAssignment(RoadMapAssignment assignment) {
        log.info("Entering saveAssignment with assignment title='{}' and {} tasks",
                assignment.getTitle(), assignment.getTasks() != null ? assignment.getTasks().size() : 0);

        try {
            if (assignment.getTasks() != null) {
                for (Task task : assignment.getTasks()) {
                    task.setAssignment(assignment);
                    log.debug("Linked task '{}' with assignment '{}'", task.getTitle(), assignment.getTitle());
                }
            }
            RoadMapAssignment saved = roadMapRepository.save(assignment);
            log.info("Successfully saved assignment with id={} and title='{}'", saved.getId(), saved.getTitle());
            return saved;
        } catch (Exception e) {
            log.error("Error occurred while saving assignment '{}'", assignment.getTitle(), e);
            throw e;
        }
    }

    public List<RoadMapAssignment> getAllAssignments() {
        log.info("Fetching all assignments");
        try {
            List<RoadMapAssignment> assignments = roadMapRepository.findAll();
            log.info("Fetched {} assignments", assignments.size());
            return assignments;
        } catch (Exception e) {
            log.error("Error occurred while fetching all assignments", e);
            throw e;
        }
    }

    public Optional<RoadMapAssignment> getAssignmentById(Long id) {
        log.info("Fetching assignment with id={}", id);
        try {
            Optional<RoadMapAssignment> assignment = roadMapRepository.findById(id);
            if (assignment.isPresent()) {
                log.info("Assignment found: id={}, title='{}'", assignment.get().getId(), assignment.get().getTitle());
            } else {
                log.warn("No assignment found with id={}", id);
            }
            return assignment;
        } catch (Exception e) {
            log.error("Error occurred while fetching assignment with id={}", id, e);
            throw e;
        }
    }

    public void deleteAssignment(Long id) {
        log.info("Deleting assignment with id={}", id);
        try {
            roadMapRepository.deleteById(id);
            log.info("Successfully deleted assignment with id={}", id);
        } catch (Exception e) {
            log.error("Error occurred while deleting assignment with id={}", id, e);
            throw e;
        }
    }
}
