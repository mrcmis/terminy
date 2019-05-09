package com.fis.is.terminy.controllers;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.fis.is.terminy.models.BaseEntity;
import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class EditEntityController {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/user/editClient")
    public String editUser(Model model){
        Client client = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("client", client);

        return "editClient";
    }

    @GetMapping("/company/editCompany")
    public String editCompany(Model model){
        Company company = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("company", company);

        return "editCompany";
    }

    @PostMapping("/company/editCompany")
    public String editedCompany(@Validated(BaseEntity.editEntity.class) Company company, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "editCompany";
        } else {
            Company logged = (Company) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Company toEdit = companyRepository.getOne(logged.getId());
            toEdit.setMail(company.getMail());
            toEdit.setPhone(company.getPhone());
            toEdit.setName(company.getName());
            companyRepository.save(toEdit);
            return "redirect:/logout";
        }
    }

    @PostMapping("/user/editClient")
    public String editedUser(@Validated(BaseEntity.editEntity.class) Client client, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "editClient";
        } else {
            Client logged = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Client toEdit = clientRepository.getOne(logged.getId());
            toEdit.setMail(client.getMail());
            toEdit.setPhone(client.getPhone());
            clientRepository.save(toEdit);
            return "redirect:/logout";
        }
    }
}
