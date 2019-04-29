package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanyService;
import com.fis.is.terminy.repositories.CompanyServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class CompanyServicesController {
    @Autowired
    private CompanyServiceRepository companyServiceRepository;

    @GetMapping("homeCompany/companyServices")
    public ModelAndView printServices(@Valid CompanyService companyService, BindingResult bindingResult, Pageable pageable){
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView mav = new ModelAndView("companyServices");
        mav.addObject("servicesList", companyServiceRepository.findByCompanyId(currentCompany.getId(), pageable).getContent());
        return mav;
    }

    @PostMapping("homeCompany/companyServices")
    public String createService(@Valid CompanyService companyService, BindingResult bindingResult, Model model) {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        companyService.setCompany(currentCompany);
        companyServiceRepository.save(companyService);

        return "redirect:/homeCompany/companyServices";
    }

}
