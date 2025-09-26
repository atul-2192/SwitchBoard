package com.SwitchBoard.PortfolioService.Service.Portfolio.Impl;


import com.SwitchBoard.PortfolioService.DTO.CertificateDTO;
import com.SwitchBoard.PortfolioService.Entity.Certificate;
import com.SwitchBoard.PortfolioService.Entity.Portfolio;
import com.SwitchBoard.PortfolioService.Repository.CertificateRepository;
import com.SwitchBoard.PortfolioService.Repository.PortfolioRepository;
import com.SwitchBoard.PortfolioService.Service.Portfolio.CertificateService;
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
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final PortfolioRepository portfolioRepository;
    private final FileUploadUtil fileUploadUtil;

    public CertificateServiceImpl(CertificateRepository certificateRepository, PortfolioRepository portfolioRepository, FileUploadUtil fileUploadUtil) {
        this.certificateRepository = certificateRepository;
        this.portfolioRepository = portfolioRepository;
        this.fileUploadUtil = fileUploadUtil;
    }

    @Override
    public Page<CertificateDTO> getCertificatesByPortfolioId(Long portfolioId, Pageable pageable) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return certificateRepository.findByPortfolioId(portfolioId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<CertificateDTO> getAllCertificatesByPortfolioId(Long portfolioId) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return certificateRepository.findByPortfolioId(portfolioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CertificateDTO getCertificateById(Long id) {
        return certificateRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with id: " + id));
    }

    @Override
    public CertificateDTO createCertificate(Long portfolioId, CertificateDTO certificateDTO) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
        
        Certificate certificate = new Certificate();
        BeanUtils.copyProperties(certificateDTO, certificate, "id", "createdAt", "updatedAt");
        certificate.setPortfolio(portfolio);
        
        Certificate savedCertificate = certificateRepository.save(certificate);
        return convertToDTO(savedCertificate);
    }

    @Override
    public CertificateDTO updateCertificate(Long id, CertificateDTO certificateDTO) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with id: " + id));
        
        // Update only non-null fields
        if (certificateDTO.getTitle() != null) {
            certificate.setTitle(certificateDTO.getTitle());
        }
        if (certificateDTO.getIssuer() != null) {
            certificate.setIssuer(certificateDTO.getIssuer());
        }
        if (certificateDTO.getIssueDate() != null) {
            certificate.setIssueDate(certificateDTO.getIssueDate());
        }
        if (certificateDTO.getExpiryDate() != null) {
            certificate.setExpiryDate(certificateDTO.getExpiryDate());
        }
        if (certificateDTO.getCredentialId() != null) {
            certificate.setCredentialId(certificateDTO.getCredentialId());
        }
        if (certificateDTO.getCredentialUrl() != null) {
            certificate.setCredentialUrl(certificateDTO.getCredentialUrl());
        }
        if (certificateDTO.getDescription() != null) {
            certificate.setDescription(certificateDTO.getDescription());
        }
        
        Certificate updatedCertificate = certificateRepository.save(certificate);
        return convertToDTO(updatedCertificate);
    }

    @Override
    public void deleteCertificate(Long id) {
        if (!certificateRepository.existsById(id)) {
            throw new EntityNotFoundException("Certificate not found with id: " + id);
        }
        certificateRepository.deleteById(id);
    }

    @Override
    public String uploadCertificateImage(Long certificateId, MultipartFile file) throws IOException {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with id: " + certificateId));

        // Delete old image if exists
        if (certificate.getCertificateImageUrl() != null && !certificate.getCertificateImageUrl().isEmpty()) {
            fileUploadUtil.deleteFile(certificate.getCertificateImageUrl());
        }

        // Upload new image
        String imagePath = fileUploadUtil.saveFile(file, "certificate-images");

        // Update certificate with new image path
        certificate.setCertificateImageUrl(imagePath);
        certificateRepository.save(certificate);

        return imagePath;

    }
    
    /**
     * Convert Certificate entity to DTO
     */
    private CertificateDTO convertToDTO(Certificate certificate) {
        CertificateDTO certificateDTO = new CertificateDTO();
        BeanUtils.copyProperties(certificate, certificateDTO);
        return certificateDTO;
    }
}
