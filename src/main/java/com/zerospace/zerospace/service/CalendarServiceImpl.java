package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.HourplaceAccount;
import com.zerospace.zerospace.domain.SpacecloudAccount;
import com.zerospace.zerospace.repository.HourplaceAccountRepository;
import com.zerospace.zerospace.repository.SpacecloudAccountRepository;
import com.zerospace.zerospace.service.utils.JWTTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl {
    private final JWTTokenService jwtTokenService;
    private final HourplaceAccountRepository hourplaceAccountRepository;
    private final SpacecloudAccountRepository spacecloudAccountRepository;

    @Transactional
    public void saveHourplaceAccount(String platform, String email, String password, HttpServletRequest request){
        String accessToken = jwtTokenService.getAccessToken(request);
        String userId = jwtTokenService.getUserIdFromToken(accessToken);

        if(platform.contains("hourplace")){
            HourplaceAccount hourplaceAccount = hourplaceAccountRepository.findByUserId(userId);
            if (hourplaceAccount != null) {
                hourplaceAccount.setHourplaceEmail(email);
                hourplaceAccount.setHourplacePassword(password);
            } else {
                hourplaceAccount = new HourplaceAccount(userId, email, password);
                hourplaceAccountRepository.save(hourplaceAccount);
            }

        }else if(platform.contains("spacecloud")){
            SpacecloudAccount spacecloudAccount = spacecloudAccountRepository.findByuserId(userId);
            if (spacecloudAccount != null) {
                spacecloudAccount.setHourplaceEmail(email);
                spacecloudAccount.setHourplacePassword(password);
            } else {
                spacecloudAccount = new SpacecloudAccount(userId, email, password);
                spacecloudAccountRepository.save(spacecloudAccount);
            }
        }


    }

}
