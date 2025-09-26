package com.SwitchBoard.PortfolioService.Controller;

import com.SwitchBoard.PortfolioService.DTO.ApiResponse;
import com.SwitchBoard.PortfolioService.DTO.ExperienceDTO;
import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.Service.Portfolio.ExperienceService;
import com.SwitchBoard.PortfolioService.Service.Portfolio.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios/{portfolioId}/experience")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ExperienceController {

    private final ExperienceService experienceService;
    private final PortfolioService portfolioService;

    public ExperienceController(ExperienceService experienceService, PortfolioService portfolioService) {
        this.experienceService = experienceService;
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<Page<ExperienceDTO>> getExperiences(
            @PathVariable Long portfolioId,
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {
        Page<ExperienceDTO> experiences = experienceService.getExperiencesByPortfolioId(portfolioId, pageable);
        return ResponseEntity.ok(experiences);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<ExperienceDTO>> getAllExperiences(@PathVariable Long portfolioId) {
        List<ExperienceDTO> experiences = experienceService.getAllExperiencesByPortfolioId(portfolioId);
        return ResponseEntity.ok(experiences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienceDTO> getExperienceById(@PathVariable Long id) {
        ExperienceDTO experience = experienceService.getExperienceById(id);
        return ResponseEntity.ok(experience);
    }

    @PostMapping
    public ResponseEntity<ExperienceDTO> createExperience(
            @PathVariable Long portfolioId,
            @Valid @RequestBody ExperienceDTO experienceDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        ExperienceDTO createdExperience = experienceService.createExperience(portfolioId, experienceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExperience);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExperienceDTO> updateExperience(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @Valid @RequestBody ExperienceDTO experienceDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        ExperienceDTO updatedExperience = experienceService.updateExperience(id, experienceDTO);
        return ResponseEntity.ok(updatedExperience);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteExperience(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        experienceService.deleteExperience(id);
        return ResponseEntity.ok(new ApiResponse(true, "Experience deleted successfully"));
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
