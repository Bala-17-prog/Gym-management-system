package com.gym.controller;

import com.gym.model.MembershipPlan;
import com.gym.service.MembershipPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/plans")
@CrossOrigin(origins = "*")
public class MembershipPlanController {

    @Autowired
    private MembershipPlanService planService;

    @GetMapping
    public ResponseEntity<List<MembershipPlan>> getAllPlans() {
        return new ResponseEntity<>(planService.getAllPlans(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPlan> createPlan(@RequestBody MembershipPlan plan) {
        return new ResponseEntity<>(planService.createPlan(plan), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MembershipPlan> updatePlan(@PathVariable Long id, @RequestBody MembershipPlan plan) {
        MembershipPlan updated = planService.updatePlan(id, plan);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        if (planService.deletePlan(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
