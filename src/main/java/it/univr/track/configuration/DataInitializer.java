package it.univr.track.configuration;

import it.univr.track.dto.UserDTO;
import it.univr.track.repository.UserRepository;
import it.univr.track.security.CustomUserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CustomUserProfileService userService, UserRepository userRepository) {
        return args -> {
            String adminUsername = "admin";

            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                log.info("Database initialization: creating default admin account...");
                UserDTO adminDto = new UserDTO();
                adminDto.setUsername(adminUsername);
                adminDto.setPassword("123456789");
                adminDto.setConfirmPassword("123456789");
                adminDto.setRole("ADMIN");

                userService.registerNewUser(adminDto);
                log.info("Admin account created successfully. Username: admin, Password: 123456789");
            } else {
                log.info("Admin account already exists, skipping initialization.");
            }
        };
    }
}
