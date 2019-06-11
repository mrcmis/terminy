package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanySchedule;
import com.fis.is.terminy.models.CompanyScheduleHelper;
import com.fis.is.terminy.models.CompanyWorkplace;
import com.fis.is.terminy.repositories.CompanyScheduleRepository;
import com.fis.is.terminy.repositories.CompanyWorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



@Controller
public class CompanyScheduleController {

    @Autowired
    private CompanyScheduleRepository companyScheduleRepository;
    @Autowired
    private CompanyWorkplaceRepository companyWorkplaceRepository;

    @GetMapping("company/companySchedule")
    public String printCompanyWorkingDaysAndHours(@Valid CompanyScheduleHelper companyScheduleHelper, Model model, Pageable pageable)
    {
        List<CompanySchedule> companyScheduleList = new ArrayList<CompanySchedule>();
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CompanyWorkplace> companyWorkplaceList = companyWorkplaceRepository.findAllByCompanyId(currentCompany.getId());
       // model.addAttribute("dayList", companyScheduleRepository.findByCompanyId(currentCompany.getId(), pageable).getContent());
        for(CompanyWorkplace companyWorkplace : companyWorkplaceList)
        {
            List<CompanySchedule> listCompanySchedule = companyScheduleRepository.findAllByCompanyWorkplaceId(companyWorkplace.getId());
            if(listCompanySchedule!=null)
                companyScheduleList.addAll(listCompanySchedule);
           /* if(companyScheduleRepository.findByCompanyWorkplaceId(companyWorkplace.getId()).isPresent())
                 companyScheduleList.add(companyScheduleRepository.findByCompanyWorkplaceId(companyWorkplace.getId()).get());*/

        }
        model.addAttribute("dayList", companyScheduleList);
        model.addAttribute("dayListSize", companyScheduleList.size());

        model.addAttribute("workplaceList", companyWorkplaceList);
        return "companySchedule";
    }

   @PostMapping("company/companySchedule")
    public String createWorkingDay(@Valid CompanyScheduleHelper companyScheduleHelper)
    {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //companySchedule.setCompany(currentCompany);
        System.out.println(companyScheduleHelper.getStart_hour() + " " + companyScheduleHelper.getDay() + " " + companyScheduleHelper.getEnd_hour() + " " + companyScheduleHelper.getWorkplaceName()
        + " " + companyScheduleHelper.getId());

        CompanyWorkplace companyWorkplace  = companyWorkplaceRepository.findById(companyScheduleHelper.getId()).get();
        CompanySchedule companySchedule = new CompanySchedule();
        companySchedule.setDay(companyScheduleHelper.getDay());
        companySchedule.setStart_hour(companyScheduleHelper.getStart_hour());
        companySchedule.setEnd_hour(companyScheduleHelper.getEnd_hour());
        companySchedule.setCompanyWorkplace(companyWorkplace);
        if(isRowInDB(companySchedule))
            return "redirect:/company/companySchedule?wrongDay=true";
        if(companyScheduleHelper.getStart_hour().isAfter(companyScheduleHelper.getEnd_hour()))
            return  "redirect:/company/companySchedule?wrongHour=true";

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

    @DeleteMapping("company/companySchedule/delete/{dayId}")
    public String deleteWorkingDay(@PathVariable(value = "dayId") Long dayId)
    {
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<CompanySchedule> companySchedule = companyScheduleRepository.findById(dayId);
        companyScheduleRepository.delete(companySchedule.get());
        return "redirect:/company/companySchedule?deleted=true";
    }


    private boolean isRowInDB(CompanySchedule companySchedule)
    {
        return companyScheduleRepository.findByCompanyWorkplaceIdAndDay(companySchedule.getCompanyWorkplace().getId(), companySchedule.getDay()).isPresent();
    }


}
