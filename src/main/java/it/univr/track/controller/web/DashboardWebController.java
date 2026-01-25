package it.univr.track.controller.web;

import it.univr.track.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/map")
public class DashboardWebController {

    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    public String showMap(Model model) {
        // Carichiamo solo le spedizioni che hanno dei device associati
        model.addAttribute("activeShipments", shipmentService.getAllShipments());
        return "map";
    }
//
//    //visualize the status of all devices
//    @RequestMapping("/web/devices")
//    public String devices() {
//        return "devices";
//    }
//
//   // visualize the status of all shipments
//    @RequestMapping("/web/shipments")
//    public String shipments() {
//        return "shipments";
//    }
//
//    //visualize the status of a single shipment
//    @RequestMapping("/web/shipment")
//    public String shipment() {
//        return "shipment";
//    }


}
