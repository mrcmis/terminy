package com.fis.is.terminy.controllers;

import com.fis.is.terminy.converters.PrivilegesConverter;
import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Controller
@SessionAttributes("company")
public class AuthenticationController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    CompanyRepository companyRepository;

    @GetMapping("/register")
    public String register(Model model){
        Client client = new Client();
        model.addAttribute("client", client);

        return "register";
    }

    @PostMapping("/register")
    public String registered(@Valid Client client, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "register";
        }
        clientRepository.saveModifiedClient(client);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth instanceof AnonymousAuthenticationToken)) {
            Collection<String> privileges = PrivilegesConverter.convertAuthoritiesToPrivilegesList(auth.getAuthorities());
            if(privileges.contains("USER")){
                return "redirect:/user";
            } else if(privileges.contains("COMPANY")){
                return "redirect:/company";
            }
        }

        Client client = new Client();
        model.addAttribute("client", client);
        model.addAttribute("company", new Company());

        return "login";
    }

    @GetMapping(value = "/login/{codedCompany}")
    public String login(Model model, @PathVariable("codedCompany") String codedCompany){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth instanceof AnonymousAuthenticationToken)) {
            Collection<String> privileges = PrivilegesConverter.convertAuthoritiesToPrivilegesList(auth.getAuthorities());
            if(privileges.contains("USER")){
                return "redirect:/user";
            } else if(privileges.contains("COMPANY")){
                return "redirect:/company";
            }
        }

        Client client = new Client();
        model.addAttribute("client", client);

        Optional<Company> optionalCompany = companyRepository.findByCodedName(codedCompany);
        if(optionalCompany.isPresent()){
            Company company = optionalCompany.get();
            model.addAttribute("company", company);
        } else {
            model.addAttribute("company", new Company());
        }

        return "login";
    }
}
