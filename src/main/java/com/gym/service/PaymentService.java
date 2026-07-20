package com.gym.service;

import com.gym.model.Payment;
import com.gym.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    @Transactional
    public Payment processPayment(Payment payment) {
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        if (payment.getStatus() == null) {
            payment.setStatus("PAID");
        }
        return paymentRepository.save(payment);
    }
    
    @Transactional
    public Payment updatePayment(Long id, Payment updated) {
        return paymentRepository.findById(id).map(payment -> {
            payment.setAmount(updated.getAmount());
            payment.setStatus(updated.getStatus());
            payment.setUser(updated.getUser());
            return paymentRepository.save(payment);
        }).orElse(null);
    }
    
    @Transactional
    public boolean deletePayment(Long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
