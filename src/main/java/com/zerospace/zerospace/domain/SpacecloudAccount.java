package com.zerospace.zerospace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpacecloudAccount {
    @Id
    private String userId;
    private String spacecloudEmail;
    private String spacecloudPassword;
}
