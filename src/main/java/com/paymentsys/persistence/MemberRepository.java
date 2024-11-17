package com.paymentsys.persistence;

import com.paymentsys.jpa.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}
