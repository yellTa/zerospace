package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.Member;
import com.zerospace.zerospace.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public boolean hasMember(String email) {
        Member foundMember = memberRepository.findMemberByEmail(email);
        if(foundMember ==null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void join(String email, String nickName) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);

        String userId =  Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        Member member = new Member(userId, email, nickName);

        memberRepository.save(member);
    }

    @Override
    public Member findMember() {
        return null;
    }
}
