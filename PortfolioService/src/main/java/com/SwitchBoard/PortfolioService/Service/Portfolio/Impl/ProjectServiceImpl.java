package com.SwitchBoard.PortfolioService.Service.Portfolio.Impl;

import com.SwitchBoard.PortfolioService.DTO.ProjectDTO;
import com.SwitchBoard.PortfolioService.Entity.Portfolio;
import com.SwitchBoard.PortfolioService.Entity.Project;
import com.SwitchBoard.PortfolioService.Repository.PortfolioRepository;
import com.SwitchBoard.PortfolioService.Repository.ProjectRepository;
import com.SwitchBoard.PortfolioService.Service.Portfolio.ProjectService;
import com.SwitchBoard.PortfolioService.Util.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final FileUploadUtil fileUploadUtil;

    public ProjectServiceImpl(ProjectRepository projectRepository, PortfolioRepository portfolioRepository, FileUploadUtil fileUploadUtil) {
        this.projectRepository = projectRepository;
        this.portfolioRepository = portfolioRepository;
        this.fileUploadUtil = fileUploadUtil;
    }

    @Override
    public Page<ProjectDTO> getProjectsByPortfolioId(Long portfolioId, Pageable pageable) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return projectRepository.findByPortfolioId(portfolioId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<ProjectDTO> getAllProjectsByPortfolioId(Long portfolioId) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return projectRepository.findByPortfolioId(portfolioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDTO getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    @Override
    public ProjectDTO createProject(Long portfolioId, ProjectDTO projectDTO) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
        
        Project project = new Project();
        BeanUtils.copyProperties(projectDTO, project, "id", "createdAt", "updatedAt");
        project.setPortfolio(portfolio);
        
        Project savedProject = projectRepository.save(project);
        return convertToDTO(savedProject);
    }

    @Override
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        
        // Update only non-null fields
        if (projectDTO.getName() != null) {
            project.setName(projectDTO.getName());
        }
        if (projectDTO.getDescription() != null) {
            project.setDescription(projectDTO.getDescription());
        }
        if (projectDTO.getUrl() != null) {
            project.setUrl(projectDTO.getUrl());
        }
        if (projectDTO.getTechnologies() != null) {
            project.setTechnologies(projectDTO.getTechnologies());
        }
        if (projectDTO.getStartDate() != null) {
            project.setStartDate(projectDTO.getStartDate());
        }
        if (projectDTO.getEndDate() != null) {
            project.setEndDate(projectDTO.getEndDate());
        }
        if (projectDTO.getOngoing() != null) {
            project.setOngoing(projectDTO.getOngoing());
        }
        
        Project updatedProject = projectRepository.save(project);
        return convertToDTO(updatedProject);
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    public String uploadProjectImage(Long projectId, MultipartFile file) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
        
        // Delete old image if exists
        if (project.getImageUrl() != null && !project.getImageUrl().isEmpty()) {
            fileUploadUtil.deleteFile(project.getImageUrl());
        }
        
        // Upload new image
        String imagePath = fileUploadUtil.saveFile(file, "project-images");
        
        // Update project with new image path
        project.setImageUrl(imagePath);
        projectRepository.save(project);
        
        return imagePath;
    }
    
    /**
     * Convert Project entity to DTO
     */
    private ProjectDTO convertToDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        return projectDTO;
    }
}
