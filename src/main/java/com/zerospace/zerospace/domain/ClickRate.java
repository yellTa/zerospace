package com.zerospace.zerospace.domain;

import com.zerospace.zerospace.service.utils.LocalDateConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class ClickRate {

    @Id
    private String userId;
    private LocalDate date;

    private int count;

    public ClickRate(String userId, int count) {
        this.userId = userId;
        this.count = count;
    }

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

}
