package com.fis.is.terminy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyServicesController {
    @GetMapping("homeCompany/companyServices")
    public String services(/*Principal principal, @SessionAttribute("company") Company company, Model model*/){

        return "companyServices";
    }
}
