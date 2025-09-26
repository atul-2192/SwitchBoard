package com.SwitchBoard.PortfolioService.Repository;

import com.SwitchBoard.PortfolioService.Entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByPortfolioId(Long portfolioId);
    Page<Skill> findByPortfolioId(Long portfolioId, Pageable pageable);
    List<Skill> findByPortfolioIdAndCategory(Long portfolioId, String category);
}
