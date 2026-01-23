package it.univr.track.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/user/signup",
                                "/user/signin",
                                "/css/**",
                                "/js/**"
                        ).permitAll()
                        .requestMatchers("/web/provision/**", "/web/decommission/**", "/users/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        .requestMatchers("/web/shipments/**", "/web/devices/**").authenticated()
                        .requestMatchers("h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/user/signin")
                        .loginProcessingUrl("/user/signin")
                        .defaultSuccessUrl("/user/profile", true)
                        .failureUrl("/user/signin?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/user/signin?logout")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
