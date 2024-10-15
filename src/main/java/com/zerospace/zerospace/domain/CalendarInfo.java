package com.zerospace.zerospace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class CalendarInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long indexNum;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String price;
    private String location;
    private String platform;
    private String process;
    private String customer;
    private String reservationNumber;
    private String link;

}
