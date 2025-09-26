package com.SwitchBoard.PortfolioService.Controller;


import com.SwitchBoard.PortfolioService.DTO.ApiResponse;
import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.Service.Portfolio.CertificateService;
import com.SwitchBoard.PortfolioService.Service.Portfolio.PortfolioService;
import com.SwitchBoard.PortfolioService.DTO.CertificateDTO;
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
@RequestMapping("/api/portfolios/{portfolioId}/certificates")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CertificateController {

    private final CertificateService certificateService;
    private final PortfolioService portfolioService;

    public CertificateController(CertificateService certificateService, PortfolioService portfolioService) {
        this.certificateService = certificateService;
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<Page<CertificateDTO>> getCertificates(
            @PathVariable Long portfolioId,
            @PageableDefault(size = 10, sort = "issueDate") Pageable pageable) {
        Page<CertificateDTO> certificates = certificateService.getCertificatesByPortfolioId(portfolioId, pageable);
        return ResponseEntity.ok(certificates);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<CertificateDTO>> getAllCertificates(@PathVariable Long portfolioId) {
        List<CertificateDTO> certificates = certificateService.getAllCertificatesByPortfolioId(portfolioId);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTO> getCertificateById(@PathVariable Long id) {
        CertificateDTO certificate = certificateService.getCertificateById(id);
        return ResponseEntity.ok(certificate);
    }

    @PostMapping
    public ResponseEntity<CertificateDTO> createCertificate(
            @PathVariable Long portfolioId,
            @Valid @RequestBody CertificateDTO certificateDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        CertificateDTO createdCertificate = certificateService.createCertificate(portfolioId, certificateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCertificate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificateDTO> updateCertificate(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @Valid @RequestBody CertificateDTO certificateDTO,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        CertificateDTO updatedCertificate = certificateService.updateCertificate(id, certificateDTO);
        return ResponseEntity.ok(updatedCertificate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCertificate(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        certificateService.deleteCertificate(id);
        return ResponseEntity.ok(new ApiResponse(true, "Certificate deleted successfully"));
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadCertificateImage(
            @PathVariable Long portfolioId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        // Verify the authenticated user is the owner of the portfolio
        verifyPortfolioOwnership(portfolioId, authentication);
        
        try {
            String imagePath = certificateService.uploadCertificateImage(id, file);
            return ResponseEntity.ok(new ApiResponse(true, "Certificate image uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Failed to upload certificate image: " + e.getMessage()));
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
