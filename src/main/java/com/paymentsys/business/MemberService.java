package com.paymentsys.business;

import com.paymentsys.jpa.Member;
import com.paymentsys.persistence.MemberRepository;
import com.paymentsys.presentation.MemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member autoRegister() {
        Member member = Member.builder()
                .username(UUID.randomUUID().toString())
                .email("example@example.com")
                .address("서울특별시 금천구 독산동")
                .build();

        return memberRepository.save(member);
    }
}
