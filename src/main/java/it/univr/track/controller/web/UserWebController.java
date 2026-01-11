package it.univr.track.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserWebController {
    @RequestMapping("/")
    public String login(){
        return "redirect:/profile";
    }

    //crete user account
    @RequestMapping("/singUp")
    public String singUp() {
        return "singUp";
    }

    //authentication (users)
    @RequestMapping("/singIn")
    public String singIn() {
        return "singIn";
    }

    //edit user profile
    @RequestMapping("/profile")
    public String profile() {
        return "profile";
    }

}
