package com.SwitchBoard.Roadmap.Service;

import com.SwitchBoard.Roadmap.Entity.RoadMapAssignment;
import com.SwitchBoard.Roadmap.Entity.Task;
import com.SwitchBoard.Roadmap.Repository.RoadMapRepository;
import com.SwitchBoard.Roadmap.Repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaskService {


    private final TaskRepository taskRepository;
    private final RoadMapRepository roadMapRepository;

    public TaskService(TaskRepository taskRepository, RoadMapRepository roadMapRepository) {
        this.taskRepository = taskRepository;
        this.roadMapRepository = roadMapRepository;
    }

    public Task addTaskToAssignment(Long assignmentId, Task task) {
        log.info("Adding new task to assignment with id {}", assignmentId);

        RoadMapAssignment assignment = roadMapRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found with id {}", assignmentId);
                    return new RuntimeException("Assignment not found with id: " + assignmentId);
                });

        task.setAssignment(assignment);
        Task saved = taskRepository.save(task);

        log.info("Task with id {} successfully added to assignment {}", saved.getId(), assignmentId);
        return saved;
    }

    public Task updateTask(Long taskId, Task taskDetails) {
        log.info("Updating task with id {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found with id {}", taskId);
                    return new RuntimeException("Task not found with id: " + taskId);
                });

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setRewardPoints(taskDetails.getRewardPoints());
        task.setTitleColor(taskDetails.getTitleColor());
        task.setDaysToComplete(taskDetails.getDaysToComplete());

        Task updated = taskRepository.save(task);

        log.info("Task with id {} successfully updated", updated.getId());
        return updated;
    }

    public void deleteTask(Long taskId) {
        log.info("Deleting task with id {}", taskId);

        if (!taskRepository.existsById(taskId)) {
            log.error("Task not found with id {}", taskId);
            throw new RuntimeException("Task not found with id: " + taskId);
        }

        taskRepository.deleteById(taskId);
        log.info("Task with id {} successfully deleted", taskId);
    }

    public List<Task> getTasksByAssignment(Long assignmentId) {
        log.info("Fetching tasks for assignment with id {}", assignmentId);

        RoadMapAssignment assignment = roadMapRepository.findById(assignmentId)
                .orElseThrow(() -> {
                    log.error("Assignment not found with id {}", assignmentId);
                    return new RuntimeException("Assignment not found with id: " + assignmentId);
                });

        List<Task> tasks = assignment.getTasks();
        log.info("Fetched {} tasks for assignment {}", tasks.size(), assignmentId);
        return tasks;
    }
}
