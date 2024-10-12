package com.zerospace.zerospace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class HourplaceAccount {
    @Id
    private String userId;
    private String hourplaceEmail;
    private String hourplacePassword;

    public HourplaceAccount() {
    }

    public HourplaceAccount(String userId, String hourplaceEmail, String hourplacePassword) {
        this.userId = userId;
        this.hourplaceEmail = hourplaceEmail;
        this.hourplacePassword = hourplacePassword;
    }


}
