package it.univr.track.controller.web;

import it.univr.track.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/map")
@RequiredArgsConstructor
public class DashboardWebController {


    private final ShipmentService shipmentService;

    @GetMapping
    public String showMap(Model model) {
        model.addAttribute("activeShipments", shipmentService.getAllShipments());
        return "map";
    }



}
