package it.univr.track.controller.web;

import it.univr.track.dto.DeviceConfigDTO;
import it.univr.track.security.CustomUserProfileService;
import it.univr.track.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Slf4j
@Controller
public class DeviceWebController {

    @Autowired private DeviceService deviceService;

    @Autowired private CustomUserProfileService customUserProfileService;

    @GetMapping("/web/devices")
    public String devices(Model model, Principal principal) {
        model.addAttribute("devices", deviceService.getAllDevices());
        model.addAttribute("user", customUserProfileService.findByUsername(principal.getName()));
        return "devices";
    }

    @GetMapping("/web/provision")
    public String provision() {
        return "provision";
    }

    @PostMapping("/web/provision")
    public String doProvision(@RequestParam String uid) {
        log.info("Provisioning di un nuovo dispositivo con UID: {}", uid);
        deviceService.registerNewDevice(uid);
        return "redirect:/web/devices";
    }

    @PostMapping("/web/decommission/{uuid}")
    public String decommission(@PathVariable String uuid) {
        log.info("Decommissioning di un dispositivo con uuid: {}", uuid);
        deviceService.decommissionDevice(uuid);
        return "redirect:/web/devices";
    }

    @GetMapping("/web/configDevice/{uuid}")
    public String configDevice(@PathVariable String uuid, Model model) {
        log.info("Show configDevice page for device with uuid: {}", uuid);
        model.addAttribute("device", deviceService.getByUid(uuid).get());
        return "configDevice";
    }

    @PostMapping("/web/editConfigDevice")
    public String editConfigDevice(@ModelAttribute DeviceConfigDTO config) {
        log.info("Received configDevice edit request: {} with device id: {}", config, config.getUuid());
        deviceService.updateConfiguration(config);
        return "redirect:/web/configDevice/" + config.getUuid() + "?updated=true";
    }

    @PostMapping("/web/sendConfigDevice/{uuid}")
    public String sendConfigDevice(@PathVariable String uuid) {
        log.info("Sending configDevice to device with uuid: {}", uuid);
        deviceService.pushConfigToHardware(uuid);
        return "redirect:/web/configDevice/" + uuid + "?sent=true";
    }

}
