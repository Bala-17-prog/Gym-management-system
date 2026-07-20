package com.gym.service;

import com.gym.model.Trainer;
import com.gym.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;

    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainerById(Long id) {
        return trainerRepository.findById(id);
    }

    @Transactional
    public Trainer addTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    @Transactional
    public Trainer updateTrainer(Long id, Trainer updatedData) {
        return trainerRepository.findById(id).map(trainer -> {
            trainer.setName(updatedData.getName());
            trainer.setSpecialization(updatedData.getSpecialization());
            trainer.setPhone(updatedData.getPhone());
            trainer.setEmail(updatedData.getEmail());
            return trainerRepository.save(trainer);
        }).orElse(null);
    }

    @Transactional
    public boolean deleteTrainer(Long id) {
        if (trainerRepository.existsById(id)) {
            trainerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
