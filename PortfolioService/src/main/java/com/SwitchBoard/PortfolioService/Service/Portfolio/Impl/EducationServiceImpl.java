package com.SwitchBoard.PortfolioService.Service.Portfolio.Impl;

import com.SwitchBoard.PortfolioService.DTO.EducationDTO;
import com.SwitchBoard.PortfolioService.Entity.Education;
import com.SwitchBoard.PortfolioService.Entity.Portfolio;
import com.SwitchBoard.PortfolioService.Repository.EducationRepository;
import com.SwitchBoard.PortfolioService.Repository.PortfolioRepository;
import com.SwitchBoard.PortfolioService.Service.Portfolio.EducationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final PortfolioRepository portfolioRepository;

    public EducationServiceImpl(EducationRepository educationRepository, PortfolioRepository portfolioRepository) {
        this.educationRepository = educationRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Page<EducationDTO> getEducationsByPortfolioId(Long portfolioId, Pageable pageable) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return educationRepository.findByPortfolioId(portfolioId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<EducationDTO> getAllEducationsByPortfolioId(Long portfolioId) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return educationRepository.findByPortfolioId(portfolioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EducationDTO getEducationById(Long id) {
        return educationRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with id: " + id));
    }

    @Override
    public EducationDTO createEducation(Long portfolioId, EducationDTO educationDTO) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
        
        Education education = new Education();
        BeanUtils.copyProperties(educationDTO, education, "id", "createdAt", "updatedAt");
        education.setPortfolio(portfolio);
        
        Education savedEducation = educationRepository.save(education);
        return convertToDTO(savedEducation);
    }

    @Override
    public EducationDTO updateEducation(Long id, EducationDTO educationDTO) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Education not found with id: " + id));
        
        // Update only non-null fields
        if (educationDTO.getInstitution() != null) {
            education.setInstitution(educationDTO.getInstitution());
        }
        if (educationDTO.getDegree() != null) {
            education.setDegree(educationDTO.getDegree());
        }
        if (educationDTO.getFieldOfStudy() != null) {
            education.setFieldOfStudy(educationDTO.getFieldOfStudy());
        }
        if (educationDTO.getGrade() != null) {
            education.setGrade(educationDTO.getGrade());
        }
        if (educationDTO.getStartDate() != null) {
            education.setStartDate(educationDTO.getStartDate());
        }
        if (educationDTO.getEndDate() != null) {
            education.setEndDate(educationDTO.getEndDate());
        }
        if (educationDTO.getOngoing() != null) {
            education.setOngoing(educationDTO.getOngoing());
        }
        if (educationDTO.getDescription() != null) {
            education.setDescription(educationDTO.getDescription());
        }
        
        Education updatedEducation = educationRepository.save(education);
        return convertToDTO(updatedEducation);
    }

    @Override
    public void deleteEducation(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new EntityNotFoundException("Education not found with id: " + id);
        }
        educationRepository.deleteById(id);
    }
    
    /**
     * Convert Education entity to DTO
     */
    private EducationDTO convertToDTO(Education education) {
        EducationDTO educationDTO = new EducationDTO();
        BeanUtils.copyProperties(education, educationDTO);
        return educationDTO;
    }
}
