package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.*;
import com.fis.is.terminy.notifications.EmailContent;
import com.fis.is.terminy.notifications.EmailService;
import com.fis.is.terminy.repositories.CompanyWorkplaceRepository;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDate;
import java.util.ArrayList;
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
        List<CompanyReservationsHelper> companyReservationsHelperList = new ArrayList<>();
        for(Reservations reservations : reservationsUnits)
        {
                CompanyReservationsHelper companyReservationsHelper = new CompanyReservationsHelper();
                companyReservationsHelper.setId(reservations.getId());
                companyReservationsHelper.setDate(reservations.getDate());
                companyReservationsHelper.setStart_hour(reservations.getStart_hour());
                companyReservationsHelper.setServiceName(reservations.getService().getName());
                companyReservationsHelper.setWorkplace(reservations.getCompanyWorkplace().getName());

                companyReservationsHelperList.add(companyReservationsHelper);
        }

        model.addAttribute("reservationsList", companyReservationsHelperList);
        model.addAttribute("reservationsListSize", companyReservationsHelperList.size());
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

