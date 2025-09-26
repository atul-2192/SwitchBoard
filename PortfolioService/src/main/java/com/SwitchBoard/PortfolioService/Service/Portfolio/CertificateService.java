package com.SwitchBoard.PortfolioService.Service.Portfolio;

import com.SwitchBoard.PortfolioService.DTO.CertificateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CertificateService {
    
    /**
     * Get all certificates for a portfolio with pagination
     */
    Page<CertificateDTO> getCertificatesByPortfolioId(Long portfolioId, Pageable pageable);
    
    /**
     * Get all certificates for a portfolio
     */
    List<CertificateDTO> getAllCertificatesByPortfolioId(Long portfolioId);
    
    /**
     * Get certificate by ID
     */
    CertificateDTO getCertificateById(Long id);
    
    /**
     * Create a new certificate for a portfolio
     */
    CertificateDTO createCertificate(Long portfolioId, CertificateDTO certificateDTO);
    
    /**
     * Update an existing certificate
     */
    CertificateDTO updateCertificate(Long id, CertificateDTO certificateDTO);
    
    /**
     * Delete a certificate by ID
     */
    void deleteCertificate(Long id);
    
    /**
     * Upload a certificate image
     */
    String uploadCertificateImage(Long certificateId, MultipartFile file) throws IOException;
}
