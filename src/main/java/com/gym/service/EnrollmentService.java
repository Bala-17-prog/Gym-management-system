package com.gym.service;

import com.gym.model.*;
import com.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    @Autowired
    private ClassEnrollmentRepository enrollmentRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private GymClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String enroll(Long userId, Long classId) {
        if (enrollmentRepository.existsByUserIdAndGymClassId(userId, classId)) {
            return "ALREADY_ENROLLED";
        }
        
        GymClass gymClass = classRepository.findById(classId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        
        if (gymClass == null || user == null) return "NOT_FOUND";
        
        long currentEnrollments = enrollmentRepository.countByGymClassId(classId);
        
        if (currentEnrollments >= gymClass.getMaxCapacity()) {
            return "CLASS_FULL";
        }
        
        ClassEnrollment enrollment = new ClassEnrollment(user, gymClass, LocalDateTime.now());
        enrollmentRepository.save(enrollment);
        return "SUCCESS";
    }

    @Transactional
    public String joinWaitlist(Long userId, Long classId) {
        if (waitlistRepository.existsByUserIdAndGymClassId(userId, classId)) {
            return "ALREADY_ON_WAITLIST";
        }
        if (enrollmentRepository.existsByUserIdAndGymClassId(userId, classId)) {
            return "ALREADY_ENROLLED";
        }

        GymClass gymClass = classRepository.findById(classId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        
        if (gymClass == null || user == null) return "NOT_FOUND";

        Waitlist waitlist = new Waitlist(user, gymClass, LocalDateTime.now());
        waitlistRepository.save(waitlist);
        return "SUCCESS";
    }

    @Transactional
    public String unenroll(Long userId, Long classId) {
        List<ClassEnrollment> enrollments = enrollmentRepository.findByUserId(userId);
        ClassEnrollment toRemove = enrollments.stream()
                .filter(e -> e.getGymClass().getId().equals(classId))
                .findFirst()
                .orElse(null);
                
        if (toRemove != null) {
            enrollmentRepository.delete(toRemove);
            promoteFromWaitlist(classId);
            return "SUCCESS";
        }
        return "NOT_FOUND";
    }

    private void promoteFromWaitlist(Long classId) {
        List<Waitlist> waitlist = waitlistRepository.findByGymClassIdOrderByAddedTimeAsc(classId);
        if (!waitlist.isEmpty()) {
            Waitlist nextInLine = waitlist.get(0);
            
            // Move to enrollment
            ClassEnrollment newEnrollment = new ClassEnrollment(
                    nextInLine.getUser(), 
                    nextInLine.getGymClass(), 
                    LocalDateTime.now());
            
            enrollmentRepository.save(newEnrollment);
            waitlistRepository.delete(nextInLine);
            
            // TODO: Trigger NotificationService here in the future
        }
    }

    public List<ClassEnrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public List<ClassEnrollment> getClassEnrollments(Long classId) {
        return enrollmentRepository.findByGymClassId(classId);
    }
}
