package it.univr.track.controller.web;

import it.univr.track.entity.UserRegistered;
import it.univr.track.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@Slf4j
public class UserWebController {

    @Autowired
    private UserRepository userRepository;


    @RequestMapping("/")
    public String login(){
        return "redirect:/profile";
    }

    @GetMapping("/signUp")
    public String singUp(Model model) {
        model.addAttribute("user", new UserRegistered());
        return "signUp";
    }

    @PostMapping("/signUp")
    public String registerUser(@ModelAttribute("user") UserRegistered user, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Username già esistente!");
            return "signUp";
        }

        userRepository.save(user);

        return "redirect:/signIn?registered=true";
    }

    @GetMapping("/signIn")
    public String singIn() {
        return "signIn";
    }

    @PostMapping("/signIn")
    public String processSignIn(@RequestParam String username,
                                @RequestParam String password,
                                Model model,
                                HttpSession session) {

        Optional<UserRegistered> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            session.setAttribute("loggedInUser", userOpt.get());

            return "redirect:/profile";
        }

        model.addAttribute("errorMessage", "Credenziali non valide. Riprova.");
        return "signIn";
    }

    //edit user profile
    @RequestMapping("/profile")
    public String profile(HttpSession session, Model model) {
        UserRegistered user = (UserRegistered) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/signIn";
        }
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Logout effettuato");
        session.invalidate();
        return "redirect:/signIn?logout=true";
    }

}
