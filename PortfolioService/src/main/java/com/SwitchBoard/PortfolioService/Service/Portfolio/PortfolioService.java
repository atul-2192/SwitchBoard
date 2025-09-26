package com.SwitchBoard.PortfolioService.Service.Portfolio;

import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.DTO.PortfolioRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PortfolioService {
    
    /**
     * Get all portfolios with pagination
     */
//    Page<PortfolioDTO> getAllPortfolios(Pageable pageable);
    
    /**
     * Get portfolio by Email ID
     */
    PortfolioDTO getPortfolioByEmailId(String emailId);
    
    /**
     * Get portfolio by user ID
     */
     PortfolioDTO getPortfolioById(Long portfolioId);
    
    /**
     * Create a new portfolio
     */
    PortfolioDTO createPortfolio(PortfolioRequest portfolioRequest);
    
    /**
     * Update an existing portfolio
     */
    PortfolioDTO updatePortfolio(Long id, PortfolioRequest portfolioRequest);
    
    /**
     * Delete a portfolio by ID
     */
    void deletePortfolio(Long id);
    
    /**
     * Upload a profile image
     */
//    String uploadProfileImage(Long portfolioId, MultipartFile file) throws IOException;
//
    /**
     * Update overview section
     */
    PortfolioDTO updateOverview(Long portfolioId, String overview);
}
