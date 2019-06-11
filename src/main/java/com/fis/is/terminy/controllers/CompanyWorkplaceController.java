package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanyWorkplace;
import com.fis.is.terminy.repositories.CompanyWorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CompanyWorkplaceController {
    @Autowired
    CompanyWorkplaceRepository companyWorkplaceRepository;

    @GetMapping("company/companyWorkplace")
    public String printCompanyWorkplaces(@Valid CompanyWorkplace companyWorkplace, Model model, Pageable pageable)
    {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CompanyWorkplace> companyWorkplaceList = companyWorkplaceRepository.findAllByCompanyId(currentCompany.getId());
        model.addAttribute("companyWorkplaceList", companyWorkplaceList);
        model.addAttribute("companyWorkplaceListSize", companyWorkplaceList.size());

        return "companyWorkplace";
    }

    @PostMapping("company/companyWorkplace")
    public String createCompanyWorkplace(@Valid CompanyWorkplace companyWorkplace)
    {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(companyWorkplace.getName());

        companyWorkplace.setCompany(currentCompany);
        System.out.println(companyWorkplace.getCompany().getId());
        if(isWorkplaceAlreadyCreated(currentCompany.getId(),companyWorkplace.getName()))
        {
            return "redirect:/company/companyWorkplace?duplicate=true";
        }
        try
        {
            companyWorkplaceRepository.save(companyWorkplace);
        }
        catch(DataIntegrityViolationException e)
        {
            System.out.println(e.getMessage());
            return "redirect:/company/companyWorkplace?error=true";
        }

        return "redirect:/company/companyWorkplace?added=true";
    }

    private boolean isWorkplaceAlreadyCreated(Long companyId, String workplaceName)
    {
        return companyWorkplaceRepository.findByCompanyIdAndName(companyId,workplaceName).isPresent();
    }

}
