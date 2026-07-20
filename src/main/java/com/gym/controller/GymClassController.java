package com.gym.controller;

import com.gym.model.GymClass;
import com.gym.service.GymClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class GymClassController {

    @Autowired
    private GymClassService gymClassService;

    @GetMapping
    public ResponseEntity<List<GymClass>> getAllClasses() {
        return new ResponseEntity<>(gymClassService.getAllClasses(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymClass> getClassById(@PathVariable Long id) {
        Optional<GymClass> gymClass = gymClassService.getClassById(id);
        return gymClass.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<GymClass>> getClassesByTrainer(@PathVariable Long trainerId) {
        return new ResponseEntity<>(gymClassService.getClassesByTrainer(trainerId), HttpStatus.OK);
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymClass> addClass(@RequestBody GymClass gymClass) {
        return new ResponseEntity<>(gymClassService.addClass(gymClass), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymClass> updateClass(@PathVariable Long id, @RequestBody GymClass gymClass) {
        GymClass updated = gymClassService.updateClass(id, gymClass);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        if (gymClassService.deleteClass(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Autowired
    private com.gym.service.EnrollmentService enrollmentService;

    @PostMapping("/{id}/enroll")
    public ResponseEntity<String> enroll(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestAttribute("userId") Long userId) {
        String result = enrollmentService.enroll(userId, id);
        if ("SUCCESS".equals(result)) return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{id}/waitlist")
    public ResponseEntity<String> joinWaitlist(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestAttribute("userId") Long userId) {
        String result = enrollmentService.joinWaitlist(userId, id);
        if ("SUCCESS".equals(result)) return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/{id}/unenroll")
    public ResponseEntity<String> unenroll(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestAttribute("userId") Long userId) {
        String result = enrollmentService.unenroll(userId, id);
        if ("SUCCESS".equals(result)) return new ResponseEntity<>(result, HttpStatus.OK);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/my-enrollments")
    public ResponseEntity<List<com.gym.model.ClassEnrollment>> getMyEnrollments(@org.springframework.web.bind.annotation.RequestAttribute("userId") Long userId) {
        return new ResponseEntity<>(enrollmentService.getUserEnrollments(userId), HttpStatus.OK);
    }

    @GetMapping("/{id}/enrollments")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<com.gym.model.ClassEnrollment>> getClassEnrollments(@PathVariable Long id) {
        return new ResponseEntity<>(enrollmentService.getClassEnrollments(id), HttpStatus.OK);
    }
}
