package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanyService;
import com.fis.is.terminy.repositories.CompanyServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CompanyServicesController {
    @Autowired
    private CompanyServiceRepository companyServiceRepository;

    @GetMapping("company/companyServices")
    public String printServices(@Valid CompanyService companyService, Model model, Pageable pageable){
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("servicesList", companyServiceRepository.findByCompanyId(currentCompany.getId(), pageable).getContent());
        return "companyServices";
    }

    @PostMapping("company/companyServices")
    public String createService(@Valid CompanyService companyService) {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        companyService.setCompany(currentCompany);
        try {
            companyServiceRepository.save(companyService);
        }catch (DataIntegrityViolationException e) {
            return "redirect:/company/companyServices?error=true";
        }
        return "redirect:/company/companyServices?added=true";
    }

    @DeleteMapping("company/companyServices/delete/{serviceId}")
    public String deleteService(@PathVariable(value = "serviceId") Long serviceId) {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<CompanyService> companyService = companyServiceRepository.findByIdAndCompanyId(serviceId, currentCompany.getId());
        companyServiceRepository.delete(companyService.get());

        return "redirect:/company/companyServices?deleted=true";
    }

}
