package com.zerospace.zerospace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class SpacecloudAccount {
    @Id
    private String userId;
    private String spacecloudEmail;
    private String spacecloudPassword;

    public SpacecloudAccount() {
    }

    public SpacecloudAccount(String userId, String hourplaceEmail, String hourplacePassword) {
        this.userId = userId;
        this.spacecloudEmail = hourplaceEmail;
        this.spacecloudPassword = hourplacePassword;
    }
}
