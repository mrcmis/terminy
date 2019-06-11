package com.fis.is.terminy.controllers;
import com.fis.is.terminy.models.BaseEntity;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.models.CompanyWorkplace;
import com.fis.is.terminy.models.Reservations;
import com.fis.is.terminy.repositories.CompanyWorkplaceRepository;
import com.fis.is.terminy.repositories.ReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Controller
public class ReportController {

    @Autowired
    private ReservationsRepository reservationsRepository;

    @Autowired
    private CompanyWorkplaceRepository companyWorkplaceRepository;



    @GetMapping("/company/Report")
    public String showAllReservations(Principal principal, Model model)
    {
        model.addAttribute("company", principal.getName());

        Company company = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Reservations> reservationsUnits = new ArrayList<>();
        List<CompanyWorkplace> companyWorkplaceList = companyWorkplaceRepository.findAllByCompanyId(company.getId());
        // List<Reservations> reservationsUnits = reservationsRepository.findAllByCompanyId(company.getId());
        for(CompanyWorkplace companyWorkplace : companyWorkplaceList)
        {
            List<Reservations> list = reservationsRepository.findAllByCompanyWorkplaceId(companyWorkplace.getId());
            reservationsUnits.addAll(list);

        }

        int[] reservarions = new int[7];

        for( Reservations i : reservationsUnits)
        {

            switch (i.getDate().getDayOfWeek().name())
            {
                case "MONDAY":
                    reservarions[0] += 1;
                    break;
                case "TUESDAY":
                    reservarions[1] += 1;
                    break;
                case "WEDNESDAY":
                    reservarions[2] += 1;
                    break;
                case "THURSDAY":
                    reservarions[3] += 1;
                    break;
                case "FRIDAY":
                    reservarions[4] += 1;
                    break;
                case "SATURDAY":
                    reservarions[5] += 1;
                    break;
                case "SUNDAY":
                    reservarions[6] += 1;
                    break;
            }
        }
        model.addAttribute("reservation", reservarions);

        List<String> workplace = new ArrayList<String>();


        for( CompanyWorkplace i : companyWorkplaceList)
        {
            workplace.add(i.getName());
        }
        int[] reservationPerWokrplace = new int[workplace.size()];

        for( Reservations i : reservationsUnits)
        {
            reservationPerWokrplace[workplace.indexOf(i.getCompanyWorkplace().getName())] += 1;
        }


        String[] workplaceArray = new String[workplace.size()];
        workplaceArray = workplace.toArray(workplaceArray);



        model.addAttribute("workplaceArray", workplaceArray);
        model.addAttribute("reservationPerWokrplace", reservationPerWokrplace);








        return "ReportGenerate";
    }




}
