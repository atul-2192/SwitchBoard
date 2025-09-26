package com.SwitchBoard.PortfolioService.Service.Portfolio;

import com.SwitchBoard.PortfolioService.DTO.ExperienceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExperienceService {
    
    /**
     * Get all experience entries for a portfolio with pagination
     */
    Page<ExperienceDTO> getExperiencesByPortfolioId(Long portfolioId, Pageable pageable);
    
    /**
     * Get all experience entries for a portfolio
     */
    List<ExperienceDTO> getAllExperiencesByPortfolioId(Long portfolioId);
    
    /**
     * Get experience by ID
     */
    ExperienceDTO getExperienceById(Long id);
    
    /**
     * Create a new experience entry for a portfolio
     */
    ExperienceDTO createExperience(Long portfolioId, ExperienceDTO experienceDTO);
    
    /**
     * Update an existing experience entry
     */
    ExperienceDTO updateExperience(Long id, ExperienceDTO experienceDTO);
    
    /**
     * Delete an experience entry by ID
     */
    void deleteExperience(Long id);
}
