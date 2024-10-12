package com.zerospace.zerospace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class HourplaceEmail {
    @Id
    private String userId;
    private String hourplaceEmail;
    private String hourplacePassword;
}
