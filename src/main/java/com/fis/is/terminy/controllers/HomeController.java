package com.fis.is.terminy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/terminyHome")
    public String register(Model model){
        return "terminyHome";
    }
}
