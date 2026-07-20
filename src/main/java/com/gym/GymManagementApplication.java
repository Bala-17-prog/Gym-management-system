package com.gym;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class GymManagementApplication {

    private static final Logger logger = LoggerFactory.getLogger(GymManagementApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Gym Management Application");
        SpringApplication.run(GymManagementApplication.class, args);
        logger.info("Gym Management Application started successfully");
    }
}