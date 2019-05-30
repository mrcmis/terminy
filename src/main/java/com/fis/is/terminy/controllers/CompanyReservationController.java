package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.Reservations;
import com.fis.is.terminy.notifications.EmailContent;
import com.fis.is.terminy.notifications.EmailService;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class CompanyReservationController {

    @Autowired
    private ReservationsRepository reservationsRepository;

    @Autowired
    private EmailService emailService;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentDate = LocalDate.now();

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

    @DeleteMapping("/company/delete/{reservationId}")
    public String cancelReservation(@PathVariable(value = "reservationId") Long reservationId)
    {
        Company company = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Reservations> reservationToDelete = reservationsRepository.findByIdAndCompanyId(reservationId, company.getId());

        if(reservationToDelete.isPresent() && reservationToDelete.get().getDate().isAfter(currentDate))
        {
            removeReservationWithConfirmation(reservationToDelete.get());
            return "redirect:/company?deleted=true";
        }
        return "redirect:/company?error=true";
    }

    private void removeReservationWithConfirmation(Reservations reservationToDelete) {
        reservationsRepository.delete(reservationToDelete);
        EmailContent emailContent = new EmailContent().setSubject("Odwołano rezerwację")
                .addReservationCancelling(reservationToDelete);
        try {
            emailService.send(reservationToDelete.getCompany().getMail(), emailContent);
            emailService.send(reservationToDelete.getClient().getMail(), emailContent);
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}
