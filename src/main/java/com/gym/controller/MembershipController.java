package com.gym.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.model.Membership;
import com.gym.service.MembershipService;

@RestController
@RequestMapping("/api/membership")
@CrossOrigin
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @PostMapping("/add")
    public Membership addMembership(@RequestBody Membership membership) {
        return membershipService.addMembership(membership);
    }

    @org.springframework.web.bind.annotation.GetMapping("/user/{userId}")
    @org.springframework.security.access.prepost.PreAuthorize("@securityService.isOwner(#userId)")
    public java.util.List<Membership> getMembershipsByUser(@org.springframework.web.bind.annotation.PathVariable Long userId) {
        return membershipService.getMembershipsByUser(userId);
    }
}
