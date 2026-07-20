package com.gym.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_enrollments")
public class ClassEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "gym_class_id", nullable = false)
    private GymClass gymClass;

    @Column(nullable = false)
    private LocalDateTime enrollmentTime;

    public ClassEnrollment() {}

    public ClassEnrollment(User user, GymClass gymClass, LocalDateTime enrollmentTime) {
        this.user = user;
        this.gymClass = gymClass;
        this.enrollmentTime = enrollmentTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public GymClass getGymClass() { return gymClass; }
    public void setGymClass(GymClass gymClass) { this.gymClass = gymClass; }
    public LocalDateTime getEnrollmentTime() { return enrollmentTime; }
    public void setEnrollmentTime(LocalDateTime enrollmentTime) { this.enrollmentTime = enrollmentTime; }
}
