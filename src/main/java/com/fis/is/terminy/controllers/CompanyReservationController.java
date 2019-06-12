package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.*;
import com.fis.is.terminy.notifications.EmailContent;
import com.fis.is.terminy.notifications.EmailService;
import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.repositories.CompanyServiceRepository;
import com.fis.is.terminy.repositories.CompanyWorkplaceRepository;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CompanyReservationController {

    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private CompanyWorkplaceRepository companyWorkplaceRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CompanyServiceRepository companyServiceRepository;
    @Autowired
    private EmailService emailService;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentDate = LocalDate.now();

    @GetMapping("/company")
    public String showAllReservations(Principal principal, Model model)
    {
        model.addAttribute("company", principal.getName());

        Company company = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Reservations> reservationsUnits = new ArrayList<>();
        List<CompanyWorkplace> companyWorkplaceList = companyWorkplaceRepository.findAllByCompanyId(company.getId());
        for(CompanyWorkplace companyWorkplace : companyWorkplaceList)
        {
            List<Reservations> list = reservationsRepository.findAllByCompanyWorkplaceId(companyWorkplace.getId());
            reservationsUnits.addAll(list);

        }
        reservationsUnits.sort((Reservations reservation1, Reservations reservation2)-> reservation2.getDate().compareTo(reservation1.getDate()));

        List<CompanyReservationsHelper> companyReservationsHelperList = new ArrayList<>();
        for(Reservations reservations : reservationsUnits)
        {
            Optional<Client> clientOptional= clientRepository.findById(reservations.getClient().getId());
            if(clientOptional.isPresent()) {
                Client client = clientOptional.get();
                CompanyReservationsHelper companyReservationsHelper = new CompanyReservationsHelper();
                companyReservationsHelper.setId(reservations.getId());
                companyReservationsHelper.setDate(reservations.getDate());
                companyReservationsHelper.setStart_hour(reservations.getStart_hour());
                companyReservationsHelper.setServiceName(reservations.getService().getName());
                companyReservationsHelper.setName(client.getName());
                companyReservationsHelper.setMail(client.getMail());
                companyReservationsHelper.setPhone(client.getPhone());
                companyReservationsHelper.setSurname(client.getSurname());

                companyReservationsHelperList.add(companyReservationsHelper);
            }
        }

        model.addAttribute("reservationsList", companyReservationsHelperList);
        model.addAttribute("reservationsListSize", companyReservationsHelperList.size());
        return "companyReservations";
    }

    @DeleteMapping("/company/delete/{reservationId}")
    public String cancelReservation(@PathVariable(value = "reservationId") Long reservationId)
    {
        Optional<Reservations> reservationToDelete = reservationsRepository.findById(reservationId);

        if(reservationToDelete.isPresent() && reservationToDelete.get().getDate().isAfter(currentDate))
        {
            removeReservationWithConfirmation(reservationToDelete.get(), clientRepository.findById(reservationToDelete.get().getClient().getId()).get());
            return "redirect:/company?deleted=true";
        }
        return "redirect:/company?error=true";
    }


    @GetMapping("/company/blockReservation")
    public String blockReservation(@Valid CompanyScheduleHelper companyScheduleHelper, Model model)
    {
        List<CompanySchedule> companyScheduleList = new ArrayList<CompanySchedule>();
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CompanyWorkplace> companyWorkplaceList = companyWorkplaceRepository.findAllByCompanyId(currentCompany.getId());
        List<Reservations> reservationsUnits = new ArrayList<>();
        for(CompanyWorkplace workplace : companyWorkplaceList)
        {
            reservationsUnits.addAll(reservationsRepository.findAllByClientIdAndCompanyWorkplaceId(currentCompany.getId(), workplace.getId()));
        }
        model.addAttribute("reservationsList", reservationsUnits);
        model.addAttribute("reservationsListSize", reservationsUnits.size());
        model.addAttribute("workplaceList", companyWorkplaceList);
        return "companyBlockedReservations";
    }

    @PostMapping("/company/blockReservation")
    public String submitBlockReservation(@Valid CompanyScheduleHelper companyScheduleHelper, Model model)
    {
        CompanyWorkplace companyWorkplace  = companyWorkplaceRepository.findById(companyScheduleHelper.getId()).get();
        Company currentCompany = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<CompanyService> companyServiceList = companyServiceRepository.findAllByCompanyId(currentCompany.getId());
        CompanyService companyService = companyServiceList.get(0);
        List<Reservations> reservationsList = reservationsRepository.findAllByCompanyWorkplaceIdAndDate(companyWorkplace.getId(),companyScheduleHelper.getDate());
        if(canBeBlocked(reservationsList, companyScheduleHelper.getStart_hour(), companyScheduleHelper.getEnd_hour())) {
            try {
                Reservations reservations = new Reservations();
                reservations.setCompanyWorkplace(companyWorkplace);
                reservations.setDate(companyScheduleHelper.getDate());
                reservations.setClient(currentCompany);
                reservations.setStart_hour(companyScheduleHelper.getStart_hour());
                reservations.setEnd_hour(companyScheduleHelper.getEnd_hour());
                reservations.setService(companyService);

                reservationsRepository.save(reservations);

            }
            catch (DataIntegrityViolationException e)
            {
                return "redirect:/company/blockReservation?error=true";
            }
        }
        else {
            return "redirect:/company/blockReservation?wrongHour=true";
        }
        return "redirect:/company/blockReservation?added=true";
    }

    @DeleteMapping("/company/blockReservation/delete/{reservationId}")
    public String cancelCompanyReservation(@PathVariable(value = "reservationId") Long reservationId)
    {
        Optional<Reservations> reservationToDelete = reservationsRepository.findById(reservationId);

        if(reservationToDelete.isPresent() && reservationToDelete.get().getDate().isAfter(currentDate))
        {
            reservationsRepository.delete(reservationToDelete.get());
            return "redirect:/company/blockReservation?deleted=true";
        }
        return "redirect:/company/blockReservation?error=true";
    }

    private void removeReservationWithConfirmation(Reservations reservationToDelete, Client client) {
        reservationsRepository.delete(reservationToDelete);
        Company company = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EmailContent emailContent = new EmailContent().setSubject("Odwołano rezerwację")
                .addReservationCancelling(reservationToDelete);
        try {
            emailService.send(company.getMail(), emailContent);
            emailService.send(client.getMail(), emailContent);
        } catch(Exception exception) {
            System.out.println(exception.getMessage());
        }
    }


    private boolean canBeBlocked(List<Reservations> reservationsList, LocalTime rstart, LocalTime rend)
    {
        for(Reservations reservations : reservationsList)
        {
            LocalTime start = reservations.getStart_hour();
            LocalTime end = reservations.getEnd_hour();
            if(start.isAfter(rstart) && end .isBefore(rend))
                return false;
            if (start.equals(rstart) && end.equals(rend))
                return false;
            if(start.equals(rstart) && end.isBefore(rend))
                return false;
            if(start.isAfter(rstart) && end.equals(rend))
                return false;
            if(start.isBefore(rstart) && end.isAfter(rstart))
                return false;
            if(start.isBefore(rend) && end.isAfter(rend))
                return false;
        }
        return true;
    }
}
