package com.SwitchBoard.PortfolioService.Service.Portfolio.Impl;

import com.SwitchBoard.PortfolioService.DTO.SkillDTO;
import com.SwitchBoard.PortfolioService.Entity.Portfolio;
import com.SwitchBoard.PortfolioService.Entity.Skill;
import com.SwitchBoard.PortfolioService.Repository.PortfolioRepository;
import com.SwitchBoard.PortfolioService.Repository.SkillRepository;
import com.SwitchBoard.PortfolioService.Service.Portfolio.SkillService;
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
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final PortfolioRepository portfolioRepository;

    public SkillServiceImpl(SkillRepository skillRepository, PortfolioRepository portfolioRepository) {
        this.skillRepository = skillRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Page<SkillDTO> getSkillsByPortfolioId(Long portfolioId, Pageable pageable) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return skillRepository.findByPortfolioId(portfolioId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public List<SkillDTO> getAllSkillsByPortfolioId(Long portfolioId) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return skillRepository.findByPortfolioId(portfolioId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SkillDTO getSkillById(Long id) {
        return skillRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + id));
    }

    @Override
    public SkillDTO createSkill(Long portfolioId, SkillDTO skillDTO) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found with id: " + portfolioId));
        
        Skill skill = new Skill();
        BeanUtils.copyProperties(skillDTO, skill, "id", "createdAt", "updatedAt");
        skill.setPortfolio(portfolio);
        
        Skill savedSkill = skillRepository.save(skill);
        return convertToDTO(savedSkill);
    }

    @Override
    public SkillDTO updateSkill(Long id, SkillDTO skillDTO) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found with id: " + id));
        
        // Update only non-null fields
        if (skillDTO.getName() != null) {
            skill.setName(skillDTO.getName());
        }
        if (skillDTO.getCategory() != null) {
            skill.setCategory(skillDTO.getCategory());
        }
        if (skillDTO.getProficiencyLevel() != null) {
            skill.setProficiencyLevel(skillDTO.getProficiencyLevel());
        }
        if (skillDTO.getDescription() != null) {
            skill.setDescription(skillDTO.getDescription());
        }
        
        Skill updatedSkill = skillRepository.save(skill);
        return convertToDTO(updatedSkill);
    }

    @Override
    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }

    @Override
    public List<SkillDTO> getSkillsByCategory(Long portfolioId, String category) {
        // Verify portfolio exists
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new EntityNotFoundException("Portfolio not found with id: " + portfolioId);
        }
        
        return skillRepository.findByPortfolioIdAndCategory(portfolioId, category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Skill entity to DTO
     */
    private SkillDTO convertToDTO(Skill skill) {
        SkillDTO skillDTO = new SkillDTO();
        BeanUtils.copyProperties(skill, skillDTO);
        return skillDTO;
    }
}
