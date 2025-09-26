package com.SwitchBoard.PortfolioService.Controller;

import com.SwitchBoard.PortfolioService.DTO.ApiResponse;
import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.DTO.SkillDTO;
import com.SwitchBoard.PortfolioService.Service.Portfolio.PortfolioService;
import com.SwitchBoard.PortfolioService.Service.Portfolio.SkillService;
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
@RequestMapping("/api/portfolios/{portfolioId}/skills")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SkillController {

    private final SkillService skillService;
    private final PortfolioService portfolioService;

    public SkillController(SkillService skillService, PortfolioService portfolioService) {
        this.skillService = skillService;
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<Page<SkillDTO>> getSkills(
            @PathVariable Long portfolioId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<SkillDTO> skills = skillService.getSkillsByPortfolioId(portfolioId, pageable);
        return ResponseEntity.ok(skills);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<SkillDTO>> getAllSkills(@PathVariable Long portfolioId) {
        List<SkillDTO> skills = skillService.getAllSkillsByPortfolioId(portfolioId);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDTO> getSkillById(@PathVariable Long id) {
        SkillDTO skill = skillService.getSkillById(id);
        return ResponseEntity.ok(skill);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<SkillDTO>> getSkillsByCategory(
            @PathVariable Long portfolioId,
            @PathVariable String category) {
        List<SkillDTO> skills = skillService.getSkillsByCategory(portfolioId, category);
        return ResponseEntity.ok(skills);
    }

    @PostMapping
    public ResponseEntity<SkillDTO> createSkill(
            @PathVariable Long portfolioId,
            @Valid @RequestBody SkillDTO skillDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        SkillDTO createdSkill = skillService.createSkill(portfolioId, skillDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSkill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> updateSkill(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @Valid @RequestBody SkillDTO skillDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        SkillDTO updatedSkill = skillService.updateSkill(id, skillDTO);
        return ResponseEntity.ok(updatedSkill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteSkill(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        skillService.deleteSkill(id);
        return ResponseEntity.ok(new ApiResponse(true, "Skill deleted successfully"));
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
