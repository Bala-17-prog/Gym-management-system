package com.gym.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlists")
public class Waitlist {

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
    private LocalDateTime addedTime;

    public Waitlist() {}

    public Waitlist(User user, GymClass gymClass, LocalDateTime addedTime) {
        this.user = user;
        this.gymClass = gymClass;
        this.addedTime = addedTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public GymClass getGymClass() { return gymClass; }
    public void setGymClass(GymClass gymClass) { this.gymClass = gymClass; }
    public LocalDateTime getAddedTime() { return addedTime; }
    public void setAddedTime(LocalDateTime addedTime) { this.addedTime = addedTime; }
}
