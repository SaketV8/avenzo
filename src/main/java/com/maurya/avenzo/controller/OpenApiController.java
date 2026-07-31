package com.maurya.avenzo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
//@RequestMapping("/docs")
public class OpenApiController {
    /*
        just show the HTML page (static file)
        and that HTML page will contain the scalar config and other data
    */

//    @GetMapping("/scalar")
    public String scalar() {
        return "redirect:/scalar.html";
    }
}
