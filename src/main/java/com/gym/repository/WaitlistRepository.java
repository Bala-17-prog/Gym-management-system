package com.gym.repository;

import com.gym.model.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findByGymClassIdOrderByAddedTimeAsc(Long gymClassId);
    boolean existsByUserIdAndGymClassId(Long userId, Long gymClassId);
}
