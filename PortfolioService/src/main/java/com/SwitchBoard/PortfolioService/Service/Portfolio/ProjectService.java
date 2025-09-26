package com.SwitchBoard.PortfolioService.Service.Portfolio;

import com.SwitchBoard.PortfolioService.DTO.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
    
    /**
     * Get all projects for a portfolio with pagination
     */
    Page<ProjectDTO> getProjectsByPortfolioId(Long portfolioId, Pageable pageable);
    
    /**
     * Get all projects for a portfolio
     */
    List<ProjectDTO> getAllProjectsByPortfolioId(Long portfolioId);
    
    /**
     * Get project by ID
     */
    ProjectDTO getProjectById(Long id);
    
    /**
     * Create a new project for a portfolio
     */
    ProjectDTO createProject(Long portfolioId, ProjectDTO projectDTO);
    
    /**
     * Update an existing project
     */
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    
    /**
     * Delete a project by ID
     */
    void deleteProject(Long id);
    
    /**
     * Upload a project image
     */
    String uploadProjectImage(Long projectId, MultipartFile file) throws IOException;
}
