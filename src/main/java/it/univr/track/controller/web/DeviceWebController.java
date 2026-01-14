package it.univr.track.controller.web;

import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import it.univr.track.repository.DeviceRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Controller
public class DeviceWebController {

    @Autowired private DeviceRepository deviceRepository;

    //provisioning of a new device (QR-code?)
    @GetMapping("/web/provision")
    public String showProvisionPage(Model model, HttpSession session) {
        UserRegistered user = (UserRegistered) session.getAttribute("loggedInUser");
        if (user == null || !user.getRole().name().equals("ADMIN")) {
            return "redirect:/signIn";
        }
        return "provision";
    }


    //decommissioning of an old device
    @RequestMapping("/web/decommission")
    public String decommission() {
        return "decommission";
    }

   // list devices
    @RequestMapping("/web/devices")
    public String devices(HttpSession session, Model model) {
        UserRegistered user = (UserRegistered) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/signIn";
        }
        model.addAttribute("user", user);
        return "devices";
    }

    //view device configuration
    @RequestMapping("/web/configDevice")
    public String configDevice() {
        return "configDevice";
    }

    //edit device configuration
    @RequestMapping("/web/editConfigDevice")
    public String editConfigDevice() {
        return "editConfigDevice";
    }

    //send configuration to device
    @RequestMapping("/web/sendConfigDevice")
    public String sendConfigDevice() {
        return "sendConfigDevice";
    }

}
