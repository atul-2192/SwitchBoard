package com.SwitchBoard.PortfolioService.Service.Portfolio;


import com.SwitchBoard.PortfolioService.DTO.AchievementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AchievementService {
    
    /**
     * Get all achievements for a portfolio with pagination
     */
    Page<AchievementDTO> getAchievementsByPortfolioId(Long portfolioId, Pageable pageable);
    
    /**
     * Get all achievements for a portfolio
     */
    List<AchievementDTO> getAllAchievementsByPortfolioId(Long portfolioId);
    
    /**
     * Get achievement by ID
     */
    AchievementDTO getAchievementById(Long id);
    
    /**
     * Create a new achievement for a portfolio
     */
    AchievementDTO createAchievement(Long portfolioId, AchievementDTO achievementDTO);
    
    /**
     * Update an existing achievement
     */
    AchievementDTO updateAchievement(Long id, AchievementDTO achievementDTO);
    
    /**
     * Delete an achievement by ID
     */
    void deleteAchievement(Long id);
}
