package it.univr.track.security;

import it.univr.track.dto.UserDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Role;
import it.univr.track.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class CustomUserProfileService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRegistered userRegistered = userRepository.findByUsername(username).orElseThrow();
        return new CustomUserDetails(userRegistered);
    }

    public void registerNewUser(UserDTO userDto) {
        // Verifica se esiste già
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username già occupato");
        }

        UserRegistered newUser = new UserRegistered();
        newUser.setUsername(userDto.getUsername());

        // CRUCIALE: Criptiamo la password prima di salvarla
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Assegniamo il ruolo (es. ROLE_USER di default o quello dal DTO)
        newUser.setRole(Role.valueOf(userDto.getRole()));

        userRepository.save(newUser);
        log.info("Nuovo utente registrato con successo: {}", newUser.getUsername());
    }

    public UserRegistered findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
