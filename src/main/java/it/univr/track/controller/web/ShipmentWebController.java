package it.univr.track.controller.web;

import it.univr.track.entity.Shipment;
import it.univr.track.service.DeviceService;
import it.univr.track.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
public class ShipmentWebController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private DeviceService deviceService;

    //create new shipment
    @GetMapping("/web/newShipment")
    public String newShipment(Model model) {
        model.addAttribute("shipment", new Shipment());
        return "newShipment";
    }

    @PostMapping("/web/newShipment")
    public String saveShipment(@ModelAttribute Shipment shipment) {
        log.info("Salvataggio nuova spedizione: {}", shipment.getCode());
        shipmentService.createShipment(shipment);
        return "redirect:/web/shipments";
    }

    //list shipments
    @RequestMapping("/web/shipments")
    public String shipments(Model model) {
        model.addAttribute("shipments", shipmentService.getAllShipments());
        return "shipments";
    }

    //activate/deactivate tracking
    @PostMapping("/web/tracking/{id}")
    public String tracking(@PathVariable Long id) {
        shipmentService.toggleStatus(id);
        return "redirect:/web/shipments";
    }

    //allocate a device to a shipment
    @GetMapping("/web/shipmentAllocate/{id}")
    public String shipmentAllocate(@PathVariable Long id, Model model) {
        Shipment shipment = shipmentService.getById(id);
        model.addAttribute("shipment", shipment);
        // Passiamo solo i device READY per l'associazione
        model.addAttribute("availableDevices", deviceService.getReadyDevices());
        return "shipmentAllocate";
    }

    @PostMapping("/web/shipmentAllocate")
    public String doAllocate(@RequestParam Long shipmentId, @RequestParam String deviceUid) {
        shipmentService.associateDeviceToShipment(shipmentId, deviceUid);
        return "redirect:/web/shipments";
    }

}
