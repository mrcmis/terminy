package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.Reservations;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class ClientReservationController {
    @Autowired
    private ReservationsRepository reservationsRepository;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentDate = LocalDate.now();

    @GetMapping("user/clientReservations")
    public String showAllReservations(Model model, @SessionAttribute("company") Company companyInSession)
    {
        Client currentClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Reservations> reservationsUnits = reservationsRepository.findByCompanyIdAndClientId(companyInSession.getId(), currentClient.getId());
        reservationsUnits.sort((Reservations resrvation1, Reservations resrvation2)->{return -resrvation1.getDate().compareTo(resrvation2.getDate());});
        model.addAttribute("reservationsList", reservationsUnits);
        return "clientReservations";
    }

    @DeleteMapping("user/clientReservations/delete/{reservationId}")
    public String cancelReservation(@PathVariable(value = "reservationId") Long reservationId)
    {
        Client currentClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Reservations> reservationToDelete = reservationsRepository.findByIdAndClientId(reservationId, currentClient.getId());

        if(reservationToDelete.get().getDate().isAfter(currentDate))
        {
            reservationsRepository.delete(reservationToDelete.get());

            return "redirect:/user/clientReservations?deleted=true";
        }

        return "redirect:/user/clientReservations?error=true";
    }
}

