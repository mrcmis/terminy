package com.fis.is.terminy.controllers;

import com.fis.is.terminy.converters.PrivilegesConverter;
import com.fis.is.terminy.notifications.CalendarEventCreator;
import com.fis.is.terminy.models.*;
import com.fis.is.terminy.notifications.EmailContent;
import com.fis.is.terminy.notifications.EmailService;
import com.fis.is.terminy.repositories.CompanyScheduleRepository;
import com.fis.is.terminy.repositories.CompanyServiceRepository;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Controller
public class ReservationController {
    @Autowired
    private ReservationsRepository reservationsRepository;
    @Autowired
    private CompanyServiceRepository companyServiceRepository;
    @Autowired
    private CompanyScheduleRepository companyScheduleRepository;
    @Autowired
    private EmailService emailService;


    private Long serviceId;
    private Company company;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private List<ReservationUnit> reservationUnits;


    @GetMapping("user/reservationIntro")
    public String printAllCompanyServices(Model model, Pageable pageable, @SessionAttribute("company") Company companyInSession)
    {
        company = companyInSession;
        model.addAttribute("serviceList", companyServiceRepository.findByCompanyId(company.getId(),pageable).getContent());
        return "reservationIntro";
    }

    @GetMapping("user/reservationIntro/service/{serviceId}")
    public String showReservation(@PathVariable(value = "serviceId") Long serviceId)
    {
        this.serviceId = serviceId;
        return "redirect:/user/reservation";
    }

    @GetMapping("user/reservation")
    public String getDate(@Valid Calendar calendar)
    {
        return "reservation";
    }

    @GetMapping("user/reservation/add/{id}")
    public String saveReservation(@PathVariable(value = "id") int id, RedirectAttributes redirectAttributes)
    {
        Client currentClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Reservations reservationToSave = prepareReservation(id, currentClient);

        if(date.isBefore(LocalDate.now()))
        {
            return "redirect:/user/reservation?badTerm=true";
        }
        else if(date.equals(LocalDate.now()) && LocalTime.now().isAfter(reservationUnits.get(id).getStart_hour()))
        {
            return "redirect:/user/reservation?badterm=true";
        }

        try
        {
            if(company.getBlockedUsers().contains(currentClient)){
                return "redirect:/user/reservation?notallowed=true";
            }
            reservationsRepository.save(reservationToSave);
        }
        catch (DataIntegrityViolationException e)
        {
            return "redirect:/user/reservation?notsaved=true";
        }

        setConfirmation(redirectAttributes, reservationToSave);
        notifyUsers(redirectAttributes, currentClient, reservationToSave);

        return "redirect:/user";
    }

    private void setConfirmation(RedirectAttributes redirectAttributes, Reservations reservationToSave) {
        String confirmation = String.format("%s - %s, %s", reservationToSave.getService().getName(),
                reservationToSave.getDate(), reservationToSave.getStart_hour());
        redirectAttributes.addFlashAttribute("confirmation", confirmation);
    }

    private void notifyUsers(RedirectAttributes redirectAttributes, Client currentClient, Reservations reservationToSave) {
        EmailContent companyMailContent = new EmailContent().setSubject("Nowa rezerwacja")
                .addCompanyReservationBasicContent(reservationToSave);
        EmailContent clientMailContent = new EmailContent().setSubject("Poprawnie zarezerwowano termin")
                .addClientReservationBasicContent(reservationToSave);

        Collection<String> privileges = PrivilegesConverter.convertAuthoritiesToPrivilegesList(company.getAuthorities());
        if(privileges.contains("MAIL_NOTIFICATION")) {
            String eventHtmlLink = CalendarEventCreator.createEventHtmlLink(reservationToSave);
            if (!eventHtmlLink.isEmpty()) {
                redirectAttributes.addFlashAttribute("googleEventLink", eventHtmlLink);
                companyMailContent.addGCalendar(eventHtmlLink);
                clientMailContent.addGCalendar(eventHtmlLink);
            }
        }

        try {
            emailService.send(company.getMail(), companyMailContent);
            emailService.send(currentClient.getMail(), clientMailContent);
        } catch(Exception exception) {
                System.out.println(exception.getMessage());
        }
    }

    private Reservations prepareReservation(int id, Client currentClient) {
        Reservations reservationToSave = new Reservations();
        reservationToSave.setClient(currentClient);
        reservationToSave.setCompany(company);
        reservationToSave.setService(companyServiceRepository.findByIdAndCompanyId(serviceId,company.getId()).get());
        reservationToSave.setDate(date);
        reservationToSave.setStart_hour(reservationUnits.get(id).getStart_hour());
        reservationToSave.setEnd_hour(reservationUnits.get(id).getEnd_hour());
        return reservationToSave;
    }

    @PostMapping("user/reservation")
    public String printTerms(@Valid Calendar calendar, Model model)
    {
        System.out.println("DATE "  +  calendar.getDate());
        date = calendar.getDate();
        CompanyService companyService = companyServiceRepository.findByIdAndCompanyId(serviceId,company.getId()).get();
        Optional<CompanySchedule> companyScheduleOptional = companyScheduleRepository.findByCompanyIdAndDay(company.getId(), calendar.getDayName());
        List<Reservations> reservations = reservationsRepository.findAllByCompanyIdAndDate(company.getId(), date);

        if(!companyScheduleOptional.isPresent())
            return "redirect:/user/reservation?error=true";

        CompanySchedule companySchedule = companyScheduleOptional.get();
        calendar.setStart_hour(companySchedule.getStart_hour());
        calendar.setEnd_hour(companySchedule.getEnd_hour());

        reservationUnits = allAvailableReservationUnitList(calendar,reservations, companyService.getDuration());

        model.addAttribute("reservationsList", reservationUnits);
        return "reservation";
    }


    private ArrayList<ReservationUnit> allAvailableReservationUnitList(Calendar calendar, List<Reservations> reservations, Long duration)
    {
        ArrayList<ReservationUnit> reservationUnits = new ArrayList<>();

        LocalTime start = calendar.getStart_hour();
        LocalTime end = start.plusMinutes(duration);
        int id = 0;

        while((start.isBefore(calendar.getEnd_hour()) || start.equals(calendar.getEnd_hour()))  && (end.isBefore(calendar.getEnd_hour()) || end.equals(calendar.getEnd_hour())))
        {
            boolean canBeAdded = true;
            System.out.println("/t" + id + "/t");
            for (Reservations reservation : reservations)
            {
                if(!canBeReserved(start,end, reservation.getStart_hour(), reservation.getEnd_hour()))
                {
                    canBeAdded = false;
                    break;
                }
            }

            if(canBeAdded) {
                reservationUnits.add(new ReservationUnit(id, start, end));
                ++id;
            }

            start = start.plusMinutes(duration);
            end = end.plusMinutes(duration);

        }

        return  reservationUnits;
    }

    private boolean canBeReserved(LocalTime start, LocalTime end, LocalTime rstart, LocalTime rend)
    {
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
        return true;
    }
}
