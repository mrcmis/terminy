package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanySchedule;
import com.fis.is.terminy.repositories.CompanyScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;



@Controller
public class CompanyScheduleController {

    @Autowired
    private CompanyScheduleRepository companyScheduleRepository;

    @GetMapping("company/companySchedule")
    public String printCompanyWorkingDaysAndHours(@Valid CompanySchedule companySchedule, Model model, Pageable pageable)
    {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("dayList", companyScheduleRepository.findByCompanyId(currentCompany.getId(), pageable).getContent());
        return "companySchedule";
    }

   @PostMapping("company/companySchedule")
    public String createWorkingDay(@Valid CompanySchedule companySchedule)
    {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        companySchedule.setCompany(currentCompany);

        if(isRowInDB(companySchedule) || !isDayValid(companySchedule))
            return "redirect:/company/companySchedule?wrongDay=true";

        try
        {
            companyScheduleRepository.save(companySchedule);
        }
        catch(DataIntegrityViolationException e)
        {
            return "redirect:/company/companySchedule?error=true";
        }
        return "redirect:/company/companySchedule?added=true";
    }

    private boolean isRowInDB(CompanySchedule companySchedule)
    {
        if(companyScheduleRepository.findByCompanyIdAndDay(companySchedule.getCompany().getId(), companySchedule.getDay()).isPresent())
            return true;
        return false;
    }

    private boolean isDayValid(CompanySchedule companySchedule)
    {
        String[] days = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};

        boolean isValid = false;
        for (String day : days) {
            if(companySchedule.getDay().equals(day))
                isValid = true;
        }
        return isValid;
    }

}
