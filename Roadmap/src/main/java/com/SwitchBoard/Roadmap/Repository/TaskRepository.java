package com.SwitchBoard.Roadmap.Repository;

import com.SwitchBoard.Roadmap.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
