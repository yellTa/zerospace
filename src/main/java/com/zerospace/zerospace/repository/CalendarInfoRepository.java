package com.zerospace.zerospace.repository;

import com.zerospace.zerospace.domain.CalendarInfo;
import com.zerospace.zerospace.domain.HourplaceAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CalendarInfoRepository extends JpaRepository<CalendarInfo, Integer> {
    public CalendarInfo findByReservationNumber(String number);
    List<CalendarInfo> findAllByUserIdAndStartTimeBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);
}
