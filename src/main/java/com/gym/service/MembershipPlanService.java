package com.gym.service;

import com.gym.model.MembershipPlan;
import com.gym.repository.MembershipPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipPlanService {

    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    public List<MembershipPlan> getAllPlans() {
        return membershipPlanRepository.findAll();
    }

    public MembershipPlan createPlan(MembershipPlan plan) {
        return membershipPlanRepository.save(plan);
    }

    public MembershipPlan updatePlan(Long id, MembershipPlan planDetails) {
        Optional<MembershipPlan> optionalPlan = membershipPlanRepository.findById(id);
        if (optionalPlan.isPresent()) {
            MembershipPlan existing = optionalPlan.get();
            existing.setName(planDetails.getName());
            existing.setPrice(planDetails.getPrice());
            existing.setDurationMonths(planDetails.getDurationMonths());
            existing.setBenefits(planDetails.getBenefits());
            return membershipPlanRepository.save(existing);
        }
        return null;
    }

    public boolean deletePlan(Long id) {
        if (membershipPlanRepository.existsById(id)) {
            membershipPlanRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
