package it.univr.track.controller.web;

import it.univr.track.dto.UserDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.security.CustomUserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserWebController {

    private final CustomUserProfileService userService;

    private static final String SIGN_UP_VIEW = "signUp";

    @GetMapping("/signin")
    public String signIn() {
        return "signIn";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return SIGN_UP_VIEW;
    }

    @PostMapping("/signup")
    public String doSignUp(@Valid @ModelAttribute("userDto") UserDTO userDto,
                           BindingResult result,
                           Model model) {
        log.info("Tentativo di registrazione per l'utente: {}", userDto.getUsername());

        if (result.hasErrors()) {
            log.error("Errore di validazione del form di registrazione: {}", result.getAllErrors());
            return SIGN_UP_VIEW;
        }

        if (!userDto.passwordsMatch()) {
            log.error("Le password non corrispondono");
            result.rejectValue("confirmPassword", "error.userDto", "Le password non corrispondono");
            return SIGN_UP_VIEW;
        }

        try {
            userService.registerNewUser(userDto);
            return "redirect:/user/signin?success";
        } catch (Exception e) {
            log.error("Errore durante la registrazione: ", e);
            model.addAttribute("errorMessage", "Errore: " + e.getMessage());
            return SIGN_UP_VIEW;
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String username = principal.getName();
        log.info("Accesso al profilo per l'utente: {}", username);

        UserRegistered user = userService.findByUsername(username);

        model.addAttribute("user", user);


        return "profile";
    }

    @GetMapping("/list")
    public String listUsers(Model model, Principal principal) {
        UserRegistered currentUser = userService.findByUsername(principal.getName());
        log.info("User found {}", currentUser.getUsername());

        if ("ADMIN".equals(currentUser.getRole().name())) {
            model.addAttribute("users", userService.getAllUsers());
            return "users";
        } else {
            model.addAttribute("user", currentUser);
            return "user";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        log.info("Delete user by id {}", id);
        userService.deleteUser(id);
        return "redirect:/user/list";
    }

    @GetMapping("/details")
    public String userDetails(Model model, Principal principal) {
        String username = principal.getName();

        UserRegistered user = userService.findByUsername(username);

        model.addAttribute("user", user);

        return "user";
    }

    @GetMapping("/details/{id}")
    public String userDetailsById(@PathVariable Long id, Model model) {
        UserRegistered user = userService.getById(id);
        model.addAttribute("user", user);

        return "user";
    }


}
