package com.zerospace.zerospace.repository;

import com.zerospace.zerospace.domain.CalendarInfo;
import com.zerospace.zerospace.domain.HourplaceAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarInfoRepository extends JpaRepository<CalendarInfo, Integer> {
    public CalendarInfo findByReservationNumber(String number);
}
