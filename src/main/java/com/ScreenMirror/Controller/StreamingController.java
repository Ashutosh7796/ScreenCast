package com.ScreenMirror.Controller;

import com.ScreenMirror.Service.ScreenCastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stream")
public class StreamingController {
    @Autowired
    private ScreenCastService screenCastService;

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/start")
    public String startStream() {
        try {
            screenCastService.startStreaming();
            return "Streaming started";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start streaming";
        }
    }
    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/stop")
    public String stopStream() {
        screenCastService.stopStreaming();
        return "Streaming stopped";
    }
}
