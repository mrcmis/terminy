package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.*;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    public String saveReservation(@PathVariable(value = "id") int id)
    {
        Client currentClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Reservations reservationsToSave = new Reservations();
        reservationsToSave.setClient(currentClient);
        reservationsToSave.setCompany(company);
        reservationsToSave.setService(companyServiceRepository.findByIdAndCompanyId(serviceId,company.getId()).get());
        reservationsToSave.setDate(date);
        reservationsToSave.setStart_hour(reservationUnits.get(id).getStart_hour());
        reservationsToSave.setEnd_hour(reservationUnits.get(id).getEnd_hour());

        try
        {
            reservationsRepository.save(reservationsToSave);
        }
        catch (DataIntegrityViolationException e)
        {
            return "redirect:/user/reservation?notsaved=true";
        }

        return "redirect:/user";
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

        while(start.isBefore(calendar.getEnd_hour()) || start.equals(calendar.getEnd_hour()))
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
