package it.univr.track.controller.web;

import it.univr.track.dto.UserDTO;
import it.univr.track.entity.UserRegistered;
import it.univr.track.security.CustomUserProfileService;
import jakarta.validation.Valid;
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
    public String profile(Model model, Principal principal) {
        String username = principal.getName();
        log.info("Accesso al profilo per l'utente: {}", username);

        UserRegistered user = userService.findByUsername(username);

        model.addAttribute("user", user);


        return "profile";
    }

    @GetMapping("/list")
    public String listUsers(Model model, Principal principal) {
        // Recuperiamo l'utente loggato
        UserRegistered currentUser = userService.findByUsername(principal.getName());
        log.info("User found {}",currentUser.getUsername());

        if ("ADMIN".equals(currentUser.getRole().name())) {
            model.addAttribute("users", userService.getAllUsers());
            return "users"; // Pagina con tabella di tutti gli utenti
        } else {
            model.addAttribute("user", currentUser);
            return "user"; // Pagina profilo singolo
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        log.info("Delete user by id {}",id);
        userService.deleteUser(id);
        return  "redirect:/user/list";
    }

    @GetMapping("/details")
    public String userDetails(Model model, Principal principal) {
        // 1. Recuperiamo lo username dell'utente loggato dalla sessione
        String username = principal.getName();

        // 2. Usiamo il service per recuperare l'entità User completa dal DB
        UserRegistered user = userService.findByUsername(username);

        // 3. Passiamo l'oggetto al model per Thymeleaf
        model.addAttribute("user", user);

        // 4. Restituiamo il nome del template (userProfile.html)
        return "user";
    }

    @GetMapping("/details/{id}")
    public String userDetailsById(@PathVariable Long id, Model model) {
        // Recuperiamo l'utente specifico tramite ID
        UserRegistered user = userService.getById(id);
        model.addAttribute("user", user);

        // Usiamo lo stesso template userProfile.html
        return "user";
    }



}
