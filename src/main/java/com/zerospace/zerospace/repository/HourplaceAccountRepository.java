package com.zerospace.zerospace.repository;

import com.zerospace.zerospace.domain.HourplaceAccount;
import com.zerospace.zerospace.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HourplaceAccountRepository extends JpaRepository<HourplaceAccount, Integer> {

    HourplaceAccount findByUserId(String userId);
}
