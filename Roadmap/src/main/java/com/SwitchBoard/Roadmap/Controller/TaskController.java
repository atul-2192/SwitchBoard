package com.SwitchBoard.Roadmap.Controller;

import com.SwitchBoard.Roadmap.Entity.Task;
import com.SwitchBoard.Roadmap.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Add task under specific assignment
    @PostMapping("/assignment/{assignmentId}")
    public ResponseEntity<Task> addTask(@PathVariable Long assignmentId, @RequestBody Task task) {
        Task savedTask = taskService.addTaskToAssignment(assignmentId, task);
        return ResponseEntity.ok(savedTask);
    }

    // Update task by taskId
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        Task updatedTask = taskService.updateTask(taskId, taskDetails);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // Get all tasks of a specific assignment
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<Task>> getTasksByAssignment(@PathVariable Long assignmentId) {
        List<Task> tasks = taskService.getTasksByAssignment(assignmentId);
        return ResponseEntity.ok(tasks);
    }
}
