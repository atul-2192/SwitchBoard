package com.SwitchBoard.PortfolioService.Repository;

import com.SwitchBoard.PortfolioService.Entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByEmailId(String emailId);
}