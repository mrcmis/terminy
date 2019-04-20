package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.BaseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

        BaseEntity a = (BaseEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(" INJECTED CLASS ->>>>>>>>>>>> :"+ a.getClass().getName());

        return "home";
    }
}
