package com.ScreenMirror.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/str")
public class GetController {

    @GetMapping("getScreen")
    public String startStream() {
        return "Screenmirrot";
    }
}
