package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.BaseEntity;
import com.fis.is.terminy.models.Company;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.security.Principal;

@Controller
public class HomeUserController {
    @GetMapping("user")
    public String home(Principal principal, @SessionAttribute("company") Company company, Model model){
        model.addAttribute("user", principal.getName());

        BaseEntity a = (BaseEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(" INJECTED CLASS ->>>>>>>>>>>> :"+ a.getClass().getName());

        System.out.println("COMPANY IN SESSION ->>>>>>>>>>>> :" + company.getName());

        return "homeUser";
    }

}