package com.SwitchBoard.PortfolioService.Controller;

import com.SwitchBoard.PortfolioService.DTO.ApiResponse;
import com.SwitchBoard.PortfolioService.DTO.EducationDTO;
import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.Service.Portfolio.EducationService;
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
@RequestMapping("/api/portfolios/{portfolioId}/education")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EducationController {

    private final EducationService educationService;
    private final PortfolioService portfolioService;

    public EducationController(EducationService educationService, PortfolioService portfolioService) {
        this.educationService = educationService;
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<Page<EducationDTO>> getEducations(
            @PathVariable Long portfolioId,
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable) {
        Page<EducationDTO> educations = educationService.getEducationsByPortfolioId(portfolioId, pageable);
        return ResponseEntity.ok(educations);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<EducationDTO>> getAllEducations(@PathVariable Long portfolioId) {
        List<EducationDTO> educations = educationService.getAllEducationsByPortfolioId(portfolioId);
        return ResponseEntity.ok(educations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EducationDTO> getEducationById(@PathVariable Long id) {
        EducationDTO education = educationService.getEducationById(id);
        return ResponseEntity.ok(education);
    }

    @PostMapping
    public ResponseEntity<EducationDTO> createEducation(
            @PathVariable Long portfolioId,
            @Valid @RequestBody EducationDTO educationDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        EducationDTO createdEducation = educationService.createEducation(portfolioId, educationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEducation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationDTO> updateEducation(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @Valid @RequestBody EducationDTO educationDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        EducationDTO updatedEducation = educationService.updateEducation(id, educationDTO);
        return ResponseEntity.ok(updatedEducation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEducation(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        educationService.deleteEducation(id);
        return ResponseEntity.ok(new ApiResponse(true, "Education entry deleted successfully"));
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
