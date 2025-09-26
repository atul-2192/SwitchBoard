package com.SwitchBoard.Roadmap.Repository;

import com.SwitchBoard.Roadmap.Entity.RoadMapAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadMapRepository extends JpaRepository<RoadMapAssignment,Long> {
}
