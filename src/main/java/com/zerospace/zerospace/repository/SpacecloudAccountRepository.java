package com.zerospace.zerospace.repository;

import com.zerospace.zerospace.domain.HourplaceAccount;
import com.zerospace.zerospace.domain.SpacecloudAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpacecloudAccountRepository extends JpaRepository<SpacecloudAccount, Integer> {

    SpacecloudAccount findByuserId(String userId);

}
