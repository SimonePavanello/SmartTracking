package it.univr.track.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import it.univr.track.entity.*;

@Controller
public class TrackDataController {

    // write new data
    @PostMapping("/api/trackdata")
    public boolean writeData() {
        return true;
    }

    // read data
    @GetMapping("/api/trackdata")
    public TrackData readData() {
        return new TrackData();
    }

}
