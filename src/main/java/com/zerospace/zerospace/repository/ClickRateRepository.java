package com.zerospace.zerospace.repository;

import com.zerospace.zerospace.domain.ClickRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ClickRateRepository extends JpaRepository<ClickRate, Integer> {
    public ClickRate findByDateAndUserId(LocalDate date, String userId);
}
