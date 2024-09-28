package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.Member;
import com.zerospace.zerospace.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    void join() {
        String email = "example@example.com";
        String nickName = "testNick";
        String userId = "testUserId";

        memberService.join(email, nickName, userId);

        Member foundMember = memberRepository.findMemberByEmail(email);
        assertNotNull(foundMember);
        assertEquals(email, foundMember.getEmail());
        assertEquals(nickName, foundMember.getNickName());
    }
}