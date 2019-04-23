package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthenticationController {

    @Autowired
    ClientRepository clientRepository;

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
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model){
        Client client = new Client();
        model.addAttribute("client", client);

        return "login";
    }



}
