package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.*;
import com.fis.is.terminy.repositories.CompanyRepository;
import com.fis.is.terminy.repositories.CompanyScheduleRepository;
import com.fis.is.terminy.repositories.CompanyServiceRepository;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.jws.WebParam;
import javax.validation.Valid;
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
    @Autowired
    private CompanyRepository companyRepository;

    private Reservations reservationsToSave = new Reservations();
    private Long companyId;
    private Long serviceId;
    private List<ReservationUnit> reservationUnits;

    @GetMapping("user/reservationIntro")
    public String printAllCompanies(Model model, Pageable pageable)
    {
        model.addAttribute("companyList", companyRepository.findAll());

        return "reservationIntro";
    }

    @GetMapping("user/reservationIntro/company/{companyId}")
    public String printAllCompanyServices(@PathVariable(value = "companyId") Long companyId, Model model, Pageable pageable)
    {
        Client currentClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        reservationsToSave.setClient(currentClient);
        this.companyId = companyId;
        reservationsToSave.setCompany(companyRepository.findById(companyId).get());
        model.addAttribute("serviceList", companyServiceRepository.findByCompanyId(companyId,pageable).getContent());
        return "reservationIntro";
    }

    @GetMapping("user/reservationIntro/service/{serviceId}")
    public String showReservation(@PathVariable(value = "serviceId") Long serviceId, Model model, Pageable pageable)
    {
        this.serviceId = serviceId;
        reservationsToSave.setService(companyServiceRepository.findByIdAndCompanyId(serviceId,companyId).get());
        return "redirect:/user/reservation";
    }

    @GetMapping("user/reservation")
    public String getDate(@Valid Calendar calendar, Model model)
    {
        return "reservation";
    }

    @GetMapping("user/reservation/add/{id}")
    public String saveReservation(@PathVariable(value = "id") int id)
    {
        reservationsToSave.setStart_hour(reservationUnits.get(id-1).getStart_hour());
        reservationsToSave.setEnd_hour(reservationUnits.get(id-1).getEnd_hour());
        try
        {
            reservationsRepository.save(reservationsToSave);
        }
        catch (DataIntegrityViolationException e)
        {
            return "redirect:/user/reservation?notsaved=true";
        }

        return "redirect:/user/reservation?saved=true";
    }

    @PostMapping("user/reservation")
    public String printTerms(@Valid Calendar calendar, Model model)
    {
        System.out.println("DATE "  +  calendar.getDate());
        reservationsToSave.setDate(calendar.getDate().plusDays(1));
        CompanyService companyService = companyServiceRepository.findByIdAndCompanyId(serviceId,companyId).get();
        Optional<CompanySchedule> companyScheduleOptional = companyScheduleRepository.findByCompanyIdAndDay(companyId, calendar.getDayName());
        List<Reservations> reservations = reservationsRepository.findAllByCompanyIdAndDate(companyId, reservationsToSave.getDate());

        if(!companyScheduleOptional.isPresent())
            return "redirect:/user/reservation?error=true";

        CompanySchedule companySchedule = companyScheduleOptional.get();
        calendar.setStart_hour(companySchedule.getStart_hour().minusMinutes(60));
        calendar.setEnd_hour(companySchedule.getEnd_hour().minusMinutes(60));

        reservationUnits = allAvailableReservationUnitList(calendar,reservations, companyService.getDuration());

        if(reservationUnits.get(reservationUnits.size() - 1).getEnd_hour().isAfter(calendar.getEnd_hour()) )
            reservationUnits.remove(reservationUnits.size() - 1);

        model.addAttribute("reservationsList", reservationUnits);


        return "reservation";
    }


    private ArrayList<ReservationUnit> allAvailableReservationUnitList(Calendar calendar, List<Reservations> reservations, Long duration)
    {
        ArrayList<ReservationUnit> reservationUnits = new ArrayList<>();

        LocalTime start = calendar.getStart_hour();
        LocalTime end = start.plusMinutes(duration);
        int id = 1;

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
