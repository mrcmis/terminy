package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.BaseEntity;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanyWorkplace;
import com.fis.is.terminy.models.Reservations;
import com.fis.is.terminy.repositories.CompanyRepository;
import com.fis.is.terminy.repositories.CompanyWorkplaceRepository;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class BlockingUsersController {

    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private CompanyWorkplaceRepository companyWorkplaceRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/company/blockingUsers")
    public String blockingUsers(Model model) {
        Company logged = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = companyRepository.getOne(logged.getId());
       // List<Reservations> reservations = reservationsRepository.findAllByCompanyId(logged.getId());

        List<Reservations> reservations = new ArrayList<>();
        List<CompanyWorkplace> companyWorkplaceList = companyWorkplaceRepository.findAllByCompanyId(company.getId());
        // List<Reservations> reservationsUnits = reservationsRepository.findAllByCompanyId(company.getId());
        for(CompanyWorkplace companyWorkplace : companyWorkplaceList)
        {
           // List<Reservations> list = reservationsRepository.findAllByCompanyWorkplaceId(companyWorkplace.getId());
            reservations.addAll(reservationsRepository.findAllByCompanyWorkplaceId(companyWorkplace.getId()));
        }

        Set<BaseEntity> clients = getUsersFromReservations(reservations);

        model.addAttribute("company", company);
        model.addAttribute("blockedUsers", company.getBlockedUsers());

        model.addAttribute("allClients", clients);

        return "blockingUsers";
    }

    @PostMapping("/company/blockingUsers")
    public String updateBlockedUsers(@ModelAttribute("company") Company company) {

        Company logged = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company toEdit = companyRepository.getOne(logged.getId());
        toEdit.setBlockedUsers(company.getBlockedUsers());
        companyRepository.save(toEdit);

        return "redirect:blockingUsers";
    }

    private Set<BaseEntity> getUsersFromReservations(List<? extends Reservations> reservations) {
        Set<BaseEntity> clients = new HashSet<>();
        for (Reservations res : reservations) {
            clients.add(res.getClient());
        }
        return clients;
    }
}
