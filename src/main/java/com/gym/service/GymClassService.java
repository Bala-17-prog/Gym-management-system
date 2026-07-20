package com.gym.service;

import com.gym.model.GymClass;
import com.gym.repository.GymClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GymClassService {

    @Autowired
    private GymClassRepository gymClassRepository;

    @Transactional(readOnly = true)
    public List<GymClass> getAllClasses() {
        return gymClassRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<GymClass> getClassById(Long id) {
        return gymClassRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<GymClass> getClassesByTrainer(Long trainerId) {
        return gymClassRepository.findByTrainerId(trainerId);
    }

    @Transactional
    public GymClass addClass(GymClass gymClass) {
        return gymClassRepository.save(gymClass);
    }

    @Transactional
    public GymClass updateClass(Long id, GymClass updatedData) {
        return gymClassRepository.findById(id).map(gymClass -> {
            gymClass.setName(updatedData.getName());
            gymClass.setScheduleTime(updatedData.getScheduleTime());
            gymClass.setMaxCapacity(updatedData.getMaxCapacity());
            gymClass.setTrainer(updatedData.getTrainer());
            return gymClassRepository.save(gymClass);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteClass(Long id) {
        if (gymClassRepository.existsById(id)) {
            gymClassRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
