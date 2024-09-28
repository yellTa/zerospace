package com.zerospace.zerospace.service;

import com.zerospace.zerospace.domain.Member;

public interface MemberService {
    public boolean hasMember(String email);

    public void join(String email,String nickName);

    public Member findMember();
}
