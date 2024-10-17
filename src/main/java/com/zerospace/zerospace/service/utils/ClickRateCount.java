package com.zerospace.zerospace.service.utils;

import com.zerospace.zerospace.domain.ClickRate;
import com.zerospace.zerospace.repository.ClickRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ClickRateCount {
    private final ClickRateRepository clickRateRepository;

    public void clickRateCount(String userId, LocalDate date) {

        ClickRate todaysUserClick = clickRateRepository.findByDateAndUserId(date, userId);

        if (todaysUserClick == null) {
            ClickRate todaysFirstClickUser = new ClickRate(userId, 1);
            clickRateRepository.save(todaysFirstClickUser);
        } else {
            int preCount = todaysUserClick.getCount();
            todaysUserClick.setCount(preCount + 1);
            clickRateRepository.save(todaysUserClick);
        }
    }
}
