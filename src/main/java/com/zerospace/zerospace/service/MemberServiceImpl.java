package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.Member;
import com.zerospace.zerospace.repository.CalendarInfoRepository;
import com.zerospace.zerospace.repository.HourplaceAccountRepository;
import com.zerospace.zerospace.repository.MemberRepository;
import com.zerospace.zerospace.repository.SpacecloudAccountRepository;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl {
    private final MemberRepository memberRepository;
    private final CalendarInfoRepository calendarInfoRepository;
    private final HourplaceAccountRepository hourplaceAccountRepository;
    private final SpacecloudAccountRepository spacecloudAccountRepository;
    private final JWTTokenService jwtTokenService;


    public boolean hasMember(String email) {
        try {
            Member foundMember = memberRepository.findMemberByEmail(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void join(String email, String nickName, String userId) {
        Member member = new Member(userId, email, nickName);
        memberRepository.save(member);

    }


    public String getMemberuserId(String email) {
        Member foundMember = memberRepository.findMemberByEmail(email);

        return foundMember.getUserId();
    }

    public String getMemberEmailfromUserId(String userId) {
        Member foundMember = memberRepository.findMemberByuserId(userId);

        return foundMember.getEmail();
    }

    @Transactional
    public void deleteUserData(HttpServletRequest request) {
        String accessToken = jwtTokenService.getAccessToken(request);
        String userId = jwtTokenService.getUserIdFromToken(accessToken);

        // CalendarInfo 삭제
        calendarInfoRepository.deleteAllByUserId(userId);

        // HourplaceAccount 삭제
        hourplaceAccountRepository.deleteByUserId(userId);

        // SpacecloudAccount 삭제
        spacecloudAccountRepository.deleteByUserId(userId);

        // Member 삭제
        memberRepository.deleteByUserId(userId);
    }

}
