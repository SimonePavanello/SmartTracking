package it.univr.track.controller.web;

import it.univr.track.dto.DeviceConfigDTO;
import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import it.univr.track.security.CustomUserProfileService;
import it.univr.track.service.DeviceService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Slf4j
@Controller
public class DeviceWebController {

    @Autowired private DeviceService deviceService;

    @Autowired private CustomUserProfileService customUserProfileService;

    // Vista Lista Devices
    @GetMapping("/web/devices")
    public String devices(Model model, Principal principal) {
        model.addAttribute("devices", deviceService.getAllDevices());
        model.addAttribute("user", customUserProfileService.findByUsername(principal.getName()));
        return "devices";
    }

    // Provisioning (Mostra Form)
    @GetMapping("/web/provision")
    public String provision() {
        return "provision";
    }

    // Provisioning (Esegue Azione)
    @PostMapping("/web/provision")
    public String doProvision(@RequestParam String uid) {
        log.info("Provisioning di un nuovo dispositivo con UID: {}", uid);
        deviceService.registerNewDevice(uid);
        return "redirect:/web/devices";
    }

    // Decommissioning
    @PostMapping("/web/decommission/{id}")
    public String decommission(@PathVariable Long id) {
        log.info("Decommissioning di un dispositivo con ID: {}", id);
        deviceService.decommissionDevice(id);
        return "redirect:/web/devices";
    }

    // Configurazione: Visualizzazione e Modifica
    @GetMapping("/web/configDevice/{id}")
    public String configDevice(@PathVariable Long id, Model model) {
        log.info("Visualizzazione della configurazione del dispositivo con ID: {}", id);
        model.addAttribute("device", deviceService.getById(id));
        return "configDevice";
    }

    @PostMapping("/web/editConfigDevice")
    public String editConfigDevice(@ModelAttribute DeviceConfigDTO config) {
        log.info("Modifica della configurazione del dispositivo con ID: {}", config.getDeviceId());
        deviceService.updateConfiguration(config);
        return "redirect:/web/configDevice/" + config.getDeviceId() + "?updated=true";
    }

    // Invia configurazione al sensore
    @PostMapping("/web/sendConfigDevice/{id}")
    public String sendConfigDevice(@PathVariable Long id) {
        deviceService.pushConfigToHardware(id);
        return "redirect:/web/configDevice/" + id + "?sent=true";
    }

}
