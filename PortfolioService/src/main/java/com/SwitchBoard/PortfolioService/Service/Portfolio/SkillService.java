package com.SwitchBoard.PortfolioService.Service.Portfolio;

import com.SwitchBoard.PortfolioService.DTO.SkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkillService {
    
    /**
     * Get all skills for a portfolio with pagination
     */
    Page<SkillDTO> getSkillsByPortfolioId(Long portfolioId, Pageable pageable);
    
    /**
     * Get all skills for a portfolio
     */
    List<SkillDTO> getAllSkillsByPortfolioId(Long portfolioId);
    
    /**
     * Get skill by ID
     */
    SkillDTO getSkillById(Long id);
    
    /**
     * Create a new skill for a portfolio
     */
    SkillDTO createSkill(Long portfolioId, SkillDTO skillDTO);
    
    /**
     * Update an existing skill
     */
    SkillDTO updateSkill(Long id, SkillDTO skillDTO);
    
    /**
     * Delete a skill by ID
     */
    void deleteSkill(Long id);
    
    /**
     * Get skills by category
     */
    List<SkillDTO> getSkillsByCategory(Long portfolioId, String category);
}
