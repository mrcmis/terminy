package com.fis.is.terminy.controllers;

import com.fis.is.terminy.converters.PrivilegesConverter;
import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanyWorkplace;
import com.fis.is.terminy.models.Reservations;
import com.fis.is.terminy.notifications.CalendarEventCreator;
import com.fis.is.terminy.notifications.EmailContent;
import com.fis.is.terminy.notifications.EmailService;
import com.fis.is.terminy.repositories.CompanyWorkplaceRepository;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Controller
public class ClientReservationController {
    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private CompanyWorkplaceRepository companyWorkplaceRepository;
    @Autowired
    private EmailService emailService;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentDate = LocalDate.now();

    @GetMapping("user/clientReservations")
    public String showAllReservations(Model model, @SessionAttribute("company") Company companyInSession)
    {
        Client currentClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CompanyWorkplace> companyWorkplaceList = companyWorkplaceRepository.findAllByCompanyId(companyInSession.getId());

        List<Reservations> reservationsUnits = new ArrayList<>();// = reservationsRepository.findByCompanyIdAndClientId(companyInSession.getId(), currentClient.getId());
        for(CompanyWorkplace companyWorkplace : companyWorkplaceList)
        {
            reservationsUnits.addAll(reservationsRepository.findAllByClientIdAndCompanyWorkplaceId(currentClient.getId(),companyWorkplace.getId()));
        }

        reservationsUnits.sort((Reservations reservation1, Reservations reservation2)-> reservation2.getDate().compareTo(reservation1.getDate()));
        if(reservationsUnits.isEmpty())
            System.out.println("EMPTY");
        model.addAttribute("reservationsList", reservationsUnits);
        model.addAttribute("reservationsListSize", reservationsUnits.size());
        return "clientReservations";
    }

    @DeleteMapping("user/clientReservations/delete/{reservationId}")
    public String cancelReservation(@PathVariable(value = "reservationId") Long reservationId,@SessionAttribute("company") Company companyInSession)
    {
        Client currentClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Reservations> reservationToDelete = reservationsRepository.findByIdAndClientId(reservationId, currentClient.getId());


        if(reservationToDelete.get().getDate().isAfter(currentDate))
        {
            notifyUsers(currentClient, companyInSession, reservationToDelete.get());
            reservationsRepository.delete(reservationToDelete.get());

            return "redirect:/user/clientReservations?deleted=true";
        }

        return "redirect:/user/clientReservations?error=true";
    }

    private void notifyUsers(Client currentClient,Company company, Reservations reservationToDelete)
    {
        EmailContent companyMailContent = new EmailContent().setSubject("Rezerwacja anulowana")
                .addReservationCancelling(reservationToDelete);
        EmailContent clientMailContent = new EmailContent().setSubject("Poprawnie odwołano termin")
                .addReservationCancelling(reservationToDelete);


        try {
            emailService.send(company.getMail(), companyMailContent);
            emailService.send(currentClient.getMail(), clientMailContent);
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}

