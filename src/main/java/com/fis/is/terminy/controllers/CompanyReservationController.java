package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.Reservations;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class CompanyReservationController {

    @Autowired
    private ReservationsRepository reservationsRepository;

    @GetMapping("/company")
    public String showAllReservations(Principal principal, Model model)
    {
        model.addAttribute("company", principal.getName());

        Company company = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Reservations> reservationsUnits = reservationsRepository.findAllByCompanyId(company.getId());
        reservationsUnits.sort((Reservations reservation1, Reservations reservation2)-> reservation2.getDate().compareTo(reservation1.getDate()));
        model.addAttribute("reservationsList", reservationsUnits);
        model.addAttribute("reservationsListSize", reservationsUnits.size());
        return "companyReservations";
    }
}
