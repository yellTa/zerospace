package com.zerospace.zerospace.repository;

import com.zerospace.zerospace.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository  extends JpaRepository<Member, Integer> {
    public Member findMemberByEmail(String email);

    public Member findMemberByuserId(String userId);
}
