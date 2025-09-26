package com.SwitchBoard.PortfolioService.Controller;

import com.SwitchBoard.PortfolioService.DTO.ApiResponse;
import com.SwitchBoard.PortfolioService.DTO.OverviewRequest;
import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.DTO.PortfolioRequest;
import com.SwitchBoard.PortfolioService.Service.Portfolio.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/portfolio")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;


//    @GetMapping
//    public ResponseEntity<Page<PortfolioDTO>> getAllPortfolios(
//            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
//        Page<PortfolioDTO> portfolios = portfolioService.getAllPortfolios(pageable);
//        return ResponseEntity.ok(portfolios);
//    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Long portfolioId) {
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/user/{emailId}")
    public ResponseEntity<PortfolioDTO> getPortfolioByUserId(@PathVariable String emailId) {
        PortfolioDTO portfolio = portfolioService.getPortfolioByEmailId(emailId);
        return ResponseEntity.ok(portfolio);
    }

    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(
            @Valid @RequestBody PortfolioRequest portfolioRequest,
            Authentication authentication) {
        
        // Verify the authenticated user is creating their own portfolio
        Long authenticatedUserId = (Long) authentication.getPrincipal();
        if (!authenticatedUserId.equals(portfolioRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        PortfolioDTO createdPortfolio = portfolioService.createPortfolio(portfolioRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
    }

    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(
                        @PathVariable Long portfolioId,
                        @Valid @RequestBody PortfolioRequest portfolioRequest,
                        @RequestHeader("X-User-Email")String emailId){

        PortfolioDTO existingPortfolio = portfolioService.getPortfolioById(portfolioId);
        
        if (!emailId.equals(existingPortfolio.getEmailId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        PortfolioDTO updatedPortfolio = portfolioService.updatePortfolio(portfolioId, portfolioRequest);
        return ResponseEntity.ok(updatedPortfolio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePortfolio(@PathVariable Long id, @RequestHeader("X-User-Email")String emailId) {
        
        // Get the portfolio to check if the authenticated user is the owner
        PortfolioDTO existingPortfolio = portfolioService.getPortfolioById(id);

        if (!emailId.equals(existingPortfolio.getEmailId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        portfolioService.deletePortfolio(id);
        return ResponseEntity.ok( ApiResponse.success( "Portfolio deleted successfully",true));
    }

//    @PostMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ApiResponse> uploadProfileImage(
//            @PathVariable Long id,
//            @RequestParam("file") MultipartFile file,
//            Authentication authentication) {
//
//        // Get the portfolio to check if the authenticated user is the owner
//        PortfolioDTO existingPortfolio = portfolioService.getPortfolioById(id);
//        Long authenticatedUserId = (Long) authentication.getPrincipal();
//
//        if (!authenticatedUserId.equals(existingPortfolio.getUserId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        try {
//            String imagePath = portfolioService.uploadProfileImage(id, file);
//            return ResponseEntity.ok(new ApiResponse(true, "Profile image uploaded successfully", imagePath));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ApiResponse(false, "Failed to upload profile image: " + e.getMessage()));
//        }
//    }

    @GetMapping("/{portfolioId}/overview")
    public ResponseEntity<String> getOverview(@PathVariable Long portfolioId) {
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio.getOverview());
    }

    @PutMapping("/{id}/overview")
    public ResponseEntity<PortfolioDTO> updateOverview(
            @PathVariable Long id,
            @Valid @RequestBody OverviewRequest overviewRequest,
            @RequestHeader("X-User-Email")String emailId) {
        
        // Get the portfolio to check if the authenticated user is the owner
        PortfolioDTO existingPortfolio = portfolioService.getPortfolioById(id);

        if (!emailId.equals(existingPortfolio.getEmailId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        PortfolioDTO updatedPortfolio = portfolioService.updateOverview(id, overviewRequest.getOverview());
        return ResponseEntity.ok(updatedPortfolio);
    }
}
