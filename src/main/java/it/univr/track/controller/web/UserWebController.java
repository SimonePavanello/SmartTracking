package it.univr.track.controller.web;

import it.univr.track.dto.UserDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.repository.UserRepository;
import it.univr.track.security.CustomUserProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller@RequestMapping("/user")
@Slf4j
public class UserWebController {

    @Autowired
    private CustomUserProfileService userService;

    @GetMapping("/signin")
    public String signIn() {
        return "signIn";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "signUp";
    }

    @PostMapping("/signup")
    public String doSignUp(@Valid @ModelAttribute("userDto") UserDTO userDto,
                           BindingResult result,
                           Model model) {
        log.info("Tentativo di registrazione per l'utente: {}", userDto.getUsername());

        if (result.hasErrors()) {
            log.error("Errore di validazione del form di registrazione: {}", result.getAllErrors());
            return "signUp";
        }

        if (!userDto.passwordsMatch()) {
            log.error("Le password non corrispondono");
            result.rejectValue("confirmPassword", "error.userDto", "Le password non corrispondono");
            return "signUp";
        }

        try {
            userService.registerNewUser(userDto);
            return "redirect:/user/signin?success";
        } catch (Exception e) {
            log.error("Errore durante la registrazione: ", e);
            model.addAttribute("errorMessage", "Errore: " + e.getMessage());
            return "signUp";
        }
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

}
