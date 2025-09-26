package com.SwitchBoard.PortfolioService.Service.Portfolio.Impl;

import com.SwitchBoard.PortfolioService.DTO.PortfolioDTO;
import com.SwitchBoard.PortfolioService.DTO.PortfolioRequest;
import com.SwitchBoard.PortfolioService.Entity.Portfolio;
import com.SwitchBoard.PortfolioService.Repository.PortfolioRepository;
import com.SwitchBoard.PortfolioService.Service.Portfolio.PortfolioService;
import com.SwitchBoard.PortfolioService.Util.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final FileUploadUtil fileUploadUtil;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository, FileUploadUtil fileUploadUtil) {
        this.portfolioRepository = portfolioRepository;
        this.fileUploadUtil = fileUploadUtil;
    }

//    @Override
//    public Page<PortfolioDTO> getAllPortfolios(Pageable pageable) {
//        return portfolioRepository.findAll(pageable)
//                .map(this::convertToDTO);
//    }

    @Override
    public PortfolioDTO getPortfolioByEmailId(String emailId) {
        return portfolioRepository.findByEmailId(emailId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + emailId));
    }

    @Override
    public PortfolioDTO getPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found for user with id: " + portfolioId));
    }

    @Override
    public PortfolioDTO createPortfolio(PortfolioRequest portfolioRequest) {
        // Check if a portfolio already exists for this user
        if (portfolioRepository.findByEmailId(portfolioRequest.getEmailId()).isPresent()) {
            throw new IllegalStateException("A portfolio already exists for this user");
        }
        
        Portfolio portfolio = new Portfolio();
        portfolio.setEmailId(portfolioRequest.getEmailId());
        portfolio.setFullName(portfolioRequest.getFullName());
        portfolio.setBio(portfolioRequest.getBio());
        portfolio.setProfileImageUrl(portfolioRequest.getProfileImageUrl());
        portfolio.setSocialLinks(portfolioRequest.getSocialLinks());
        portfolio.setOverview(portfolioRequest.getOverview());
        
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return convertToDTO(savedPortfolio);
    }

    @Override
    public PortfolioDTO updatePortfolio(Long id, PortfolioRequest portfolioRequest) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + id));
        
        // Update only non-null fields
        if (portfolioRequest.getFullName() != null) {
            portfolio.setFullName(portfolioRequest.getFullName());
        }
        if (portfolioRequest.getBio() != null) {
            portfolio.setBio(portfolioRequest.getBio());
        }
        if (portfolioRequest.getProfileImageUrl() != null) {
            portfolio.setProfileImageUrl(portfolioRequest.getProfileImageUrl());
        }
        if (portfolioRequest.getSocialLinks() != null) {
            portfolio.setSocialLinks(portfolioRequest.getSocialLinks());
        }
        if (portfolioRequest.getOverview() != null) {
            portfolio.setOverview(portfolioRequest.getOverview());
        }
        
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        return convertToDTO(updatedPortfolio);
    }

    @Override
    public void deletePortfolio(Long id) {
        if (!portfolioRepository.existsById(id)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + id);
        }
        portfolioRepository.deleteById(id);
    }

//    @Override
//    public String uploadProfileImage(Long portfolioId, MultipartFile file) throws IOException {
//        Portfolio portfolio = portfolioRepository.findById(portfolioId)
//                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
//
//        // Delete old image if exists
//        if (portfolio.getProfileImageUrl() != null && !portfolio.getProfileImageUrl().isEmpty()) {
//            fileUploadUtil.deleteFile(portfolio.getProfileImageUrl());
//        }
//
//        // Upload new image
//        String imagePath = fileUploadUtil.saveFile(file, "profile-images");
//
//        // Update portfolio with new image path
//        portfolio.setProfileImageUrl(imagePath);
//        portfolioRepository.save(portfolio);
//
//        return imagePath;
//    }

    @Override
    public PortfolioDTO updateOverview(Long portfolioId, String overview) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
        
        portfolio.setOverview(overview);
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        return convertToDTO(updatedPortfolio);
    }
    
    /**
     * Convert Portfolio entity to DTO
     */
    private PortfolioDTO convertToDTO(Portfolio portfolio) {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        BeanUtils.copyProperties(portfolio, portfolioDTO);
        // Note: We're not including nested collections here to avoid circular references
        // These should be loaded separately via their respective services
        return portfolioDTO;
    }
}
