package com.zerospace.zerospace.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Member {
    @Id
    private String userId;

    private String email;

    private String nickName;

    public Member(){
    }
    public Member(String userId, String email, String nickName){
        this.userId = userId;
        this.email = email;
        this.nickName = nickName;

    }

}
