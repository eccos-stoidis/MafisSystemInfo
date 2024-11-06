package com.ep.sysinfo.MafisSyStemInfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BerichtController {

    @GetMapping("/alleBerichte")
    public String berichtListe() {
        return "berichte";
    }
}
