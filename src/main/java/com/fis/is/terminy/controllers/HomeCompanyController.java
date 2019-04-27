package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.BaseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class HomeCompanyController {
    @GetMapping("homeCompany")
    public String home(Principal principal, Model model){
        model.addAttribute("company", principal.getName());

        BaseEntity a = (BaseEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(" INJECTED CLASS ->>>>>>>>>>>> :"+ a.getClass().getName());

        return "homeCompany";
    }
}
