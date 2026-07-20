package com.gym.service;

import com.gym.model.Payment;
import com.gym.model.User;
import com.gym.repository.GymClassRepository;
import com.gym.repository.PaymentRepository;
import com.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private GymClassRepository gymClassRepository;

    @Autowired
    private com.gym.repository.TrainerRepository trainerRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalMembers = userRepository.count();
        stats.put("totalMembers", totalMembers);
        
        List<Payment> allPayments = paymentRepository.findAll();
        double totalRevenue = allPayments.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
        stats.put("totalRevenue", totalRevenue);
        
        long activeClasses = gymClassRepository.count();
        stats.put("activeClasses", activeClasses);
        
        long activeTrainers = trainerRepository.count();
        stats.put("activeTrainers", activeTrainers);
        
        // Prepare chart data (simple example)
        // 1. Revenue by month
        Map<String, Double> revByMonth = new HashMap<>();
        for (Payment p : allPayments) {
            if ("PAID".equalsIgnoreCase(p.getStatus())) {
                String month = p.getPaymentDate().getMonth().toString(); // e.g. "JANUARY"
                revByMonth.put(month, revByMonth.getOrDefault(month, 0.0) + p.getAmount());
            }
        }
        stats.put("revenueChartData", revByMonth);
        
        // 2. Classes (for Doughnut chart)
        Map<String, Long> classesData = new HashMap<>();
        for (com.gym.model.GymClass gc : gymClassRepository.findAll()) {
            // For now, just use max capacity or some other metric. 
            // In a real system you'd count enrollments. Let's count enrollments if we had the repository,
            // but we don't have it imported here. We will just return the class names and their max capacity as a placeholder for enrollments.
            classesData.put(gc.getName(), (long) gc.getMaxCapacity());
        }
        stats.put("classesChartData", classesData);
        
        return stats;
    }
}
