package com.gym.repository;

import com.gym.model.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {
    List<ClassEnrollment> findByGymClassId(Long gymClassId);
    List<ClassEnrollment> findByUserId(Long userId);
    boolean existsByUserIdAndGymClassId(Long userId, Long gymClassId);
    long countByGymClassId(Long gymClassId);
}
