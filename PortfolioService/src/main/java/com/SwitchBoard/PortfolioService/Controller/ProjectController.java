package com.SwitchBoard.PortfolioService.Controller;

import com.SwitchBoard.PortfolioService.DTO.ApiResponse;
import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.DTO.ProjectDTO;
import com.SwitchBoard.PortfolioService.Service.Portfolio.PortfolioService;
import com.SwitchBoard.PortfolioService.Service.Portfolio.ProjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/projects")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProjectController {

    private final ProjectService projectService;
    private final PortfolioService portfolioService;

    public ProjectController(ProjectService projectService, PortfolioService portfolioService) {
        this.projectService = projectService;
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getProjects(
            @PathVariable Long portfolioId,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<ProjectDTO> projects = projectService.getProjectsByPortfolioId(portfolioId, pageable);
        return ResponseEntity.ok(projects);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<ProjectDTO>> getAllProjects(@PathVariable Long portfolioId) {
        List<ProjectDTO> projects = projectService.getAllProjectsByPortfolioId(portfolioId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(
            @PathVariable Long portfolioId,
            @Valid @RequestBody ProjectDTO projectDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        ProjectDTO createdProject = projectService.createProject(portfolioId, projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO projectDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProject(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        projectService.deleteProject(id);
        return ResponseEntity.ok(new ApiResponse(true, "Project deleted successfully"));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadProjectImage(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        try {
            String imagePath = projectService.uploadProjectImage(id, file);
            return ResponseEntity.ok(new ApiResponse(true, "Project image uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to upload project image: " + e.getMessage()));
        }
    }
    
    /**
     * Helper method to verify that the authenticated user is the owner of the portfolio
     */
    private void verifyPortfolioOwnership(Long portfolioId, Authentication authentication) {
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        
        if (!authenticatedUserId.equals(portfolio.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to modify this portfolio");
        }
    }
}
