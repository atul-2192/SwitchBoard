package com.SwitchBoard.PortfolioService.Service.Portfolio.Impl;

import com.SwitchBoard.PortfolioService.DTO.AchievementDTO;
import com.SwitchBoard.PortfolioService.Entity.Achievement;
import com.SwitchBoard.PortfolioService.Entity.Portfolio;
import com.SwitchBoard.PortfolioService.Repository.AchievementRepository;
import com.SwitchBoard.PortfolioService.Repository.PortfolioRepository;
import com.SwitchBoard.PortfolioService.Service.Portfolio.AchievementService;
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
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final PortfolioRepository portfolioRepository;

    public AchievementServiceImpl(AchievementRepository achievementRepository, PortfolioRepository portfolioRepository) {
        this.achievementRepository = achievementRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Page<AchievementDTO> getAchievementsByPortfolioId(Long portfolioId, Pageable pageable) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return achievementRepository.findByPortfolioId(portfolioId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<AchievementDTO> getAllAchievementsByPortfolioId(Long portfolioId) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return achievementRepository.findByPortfolioId(portfolioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AchievementDTO getAchievementById(Long id) {
        return achievementRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Achievement not found with id: " + id));
    }

    @Override
    public AchievementDTO createAchievement(Long portfolioId, AchievementDTO achievementDTO) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
        
        Achievement achievement = new Achievement();
        BeanUtils.copyProperties(achievementDTO, achievement, "id", "createdAt", "updatedAt");
        achievement.setPortfolio(portfolio);
        
        Achievement savedAchievement = achievementRepository.save(achievement);
        return convertToDTO(savedAchievement);
    }

    @Override
    public AchievementDTO updateAchievement(Long id, AchievementDTO achievementDTO) {
        Achievement achievement = achievementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Achievement not found with id: " + id));
        
        // Update only non-null fields
        if (achievementDTO.getTitle() != null) {
            achievement.setTitle(achievementDTO.getTitle());
        }
        if (achievementDTO.getDate() != null) {
            achievement.setDate(achievementDTO.getDate());
        }
        if (achievementDTO.getIssuer() != null) {
            achievement.setIssuer(achievementDTO.getIssuer());
        }
        if (achievementDTO.getDescription() != null) {
            achievement.setDescription(achievementDTO.getDescription());
        }
        if (achievementDTO.getUrl() != null) {
            achievement.setUrl(achievementDTO.getUrl());
        }
        
        Achievement updatedAchievement = achievementRepository.save(achievement);
        return convertToDTO(updatedAchievement);
    }

    @Override
    public void deleteAchievement(Long id) {
        if (!achievementRepository.existsById(id)) {
            throw new EntityNotFoundException("Achievement not found with id: " + id);
        }
        achievementRepository.deleteById(id);
    }
    
    /**
     * Convert Achievement entity to DTO
     */
    private AchievementDTO convertToDTO(Achievement achievement) {
        AchievementDTO achievementDTO = new AchievementDTO();
        BeanUtils.copyProperties(achievement, achievementDTO);
        return achievementDTO;
    }
}
