package com.fis.is.terminy.controllers;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeController {
    @GetMapping("home")
    public String home(Principal principal, Model model){
        model.addAttribute("user", principal.getName());

        return "home";
    }
}
