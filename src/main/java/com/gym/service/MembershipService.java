package com.gym.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.model.Membership;
import com.gym.repository.MembershipRepository;

@Service
public class MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    public Membership addMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    public java.util.List<Membership> getMembershipsByUser(Long userId) {
        return membershipRepository.findByUserId(userId);
    }
}
