package com.SwitchBoard.PortfolioService.Service.Portfolio;

import com.SwitchBoard.PortfolioService.DTO.EducationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EducationService {
    
    /**
     * Get all education entries for a portfolio with pagination
     */
    Page<EducationDTO> getEducationsByPortfolioId(Long portfolioId, Pageable pageable);
    
    /**
     * Get all education entries for a portfolio
     */
    List<EducationDTO> getAllEducationsByPortfolioId(Long portfolioId);
    
    /**
     * Get education by ID
     */
    EducationDTO getEducationById(Long id);
    
    /**
     * Create a new education entry for a portfolio
     */
    EducationDTO createEducation(Long portfolioId, EducationDTO educationDTO);
    
    /**
     * Update an existing education entry
     */
    EducationDTO updateEducation(Long id, EducationDTO educationDTO);
    
    /**
     * Delete an education entry by ID
     */
    void deleteEducation(Long id);
}
