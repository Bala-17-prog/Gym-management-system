package com.gym.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.model.Attendance;
import com.gym.model.User;
import com.gym.repository.AttendanceRepository;
import com.gym.repository.UserRepository;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    public Attendance markAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public List<User> getAttendanceByDate(LocalDate date) {
        // Fetch attendance records for the specified date
        List<Attendance> attendances = attendanceRepository.findByDate(date);
        List<User> users = new ArrayList<>();

        // Log the number of attendance records found
        logger.info("Found {} attendance records for date: {}", attendances.size(), date);

        for (Attendance attendance : attendances) {
            try {
                User user = userRepository.findById(attendance.getUserId()).orElse(null);
                if (user != null) {
                    users.add(user);
                } else {
                    // Log if no user found for the attendance record
                    logger.warn("No user found for attendance with ID: {}", attendance.getUserId());
                }
            } catch (Exception e) {
                // Log any exceptions that occur during the loop
                logger.error("Error fetching user for attendance with ID: {}", attendance.getUserId(), e);
            }
        }

        // Return the list of users who were present on the given date
        return users;
    }

    public List<Attendance> getAttendanceByUser(Long userId) {
        return attendanceRepository.findByUserId(userId);
    }
    
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }
    
    public Attendance updateAttendance(Long id, Attendance updated) {
        return attendanceRepository.findById(id).map(a -> {
            a.setDate(updated.getDate());
            a.setUserId(updated.getUserId());
            a.setStatus(updated.getStatus());
            return attendanceRepository.save(a);
        }).orElse(null);
    }
    
    public boolean deleteAttendance(Long id) {
        if (attendanceRepository.existsById(id)) {
            attendanceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
