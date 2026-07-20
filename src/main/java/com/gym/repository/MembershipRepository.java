package com.gym.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gym.model.Membership;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByUserId(Long userId);
}
