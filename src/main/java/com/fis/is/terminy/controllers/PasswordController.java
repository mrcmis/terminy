package com.fis.is.terminy.controllers;

import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.services.ClientService;
import com.fis.is.terminy.services.CompanyService;
import com.fis.is.terminy.services.EmailServ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private EmailServ emailServ;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/forgotPassword")
    public ModelAndView displayForgotPasswordPage(){
        return new ModelAndView("forgotPassword");
    }

    @PostMapping("/forgotPassword")
    public ModelAndView processForgotPasswordForm(ModelAndView modelAndView, @RequestParam("email") String userEmail, HttpServletRequest request){

        Optional<Client> optionalClient = clientService.findClientByEmail(userEmail);
        Optional<Company> optionalCompany = companyService.findCompanyByEmail(userEmail);


        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setResetToken(UUID.randomUUID().toString());
            clientService.save(client);

            String appUrl = request.getScheme() + "://" + request.getServerName();
            String port  = ":8080";

            SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
            passwordResetEmail.setFrom("terminy.io@gmail.com");
            passwordResetEmail.setTo(client.getMail());
            passwordResetEmail.setSubject("Zmiana hasła");
            passwordResetEmail.setText("W celu zresetowanie hasła, proszę wejść w poniższy link:\n" + appUrl + port
                    + "/resetPassword?token=" + client.getResetToken());

            emailServ.sendEmail(passwordResetEmail);

            modelAndView.addObject("successMessage", "Wysłano link aktywacyjny do " + userEmail);
        }

        else if (optionalCompany.isPresent()){
            Company company = optionalCompany.get();
            company.setResetToken(UUID.randomUUID().toString());
            companyService.save(company);

            String appUrl = request.getScheme() + "://" + request.getServerName();
            String port  = ":8080";

            SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
            passwordResetEmail.setFrom("terminy.io@gmail.com");
            passwordResetEmail.setTo(company.getMail());
            passwordResetEmail.setSubject("Zmiana hasła");
            passwordResetEmail.setText("W celu zresetowanie hasła, proszę wejść w poniższy link:\n" + appUrl + port
                    + "/resetPassword?token=" + company.getResetToken());

            emailServ.sendEmail(passwordResetEmail);

            modelAndView.addObject("successMessage", "Wysłano link aktywacyjny do " + userEmail);
        }

        else{
            modelAndView.addObject("errorMessage?", "Podane konto e-mail nie istnieje");
            modelAndView.setViewName("forgotPassword?error=true");
        }

        modelAndView.setViewName("forgotPassword");
        return modelAndView;
    }

    @GetMapping("/resetPassword")
    public ModelAndView displayResetPasswordPage(ModelAndView modelAndView, @RequestParam("token") String token){
        Optional<Client> client = clientService.findClientByResetToken(token);
        Optional<Company> company = companyService.findCompanyByResetToken(token);

        if (client.isPresent() || company.isPresent()) {
            modelAndView.addObject("resetToken", token);
        } else{
            modelAndView.addObject("errorMessage", "Użyto nieprawidłowego przekierowania");
        }

        modelAndView.setViewName("resetPassword");
        return modelAndView;
    }

    @PostMapping("/resetPassword")
    public ModelAndView setNewPassword(ModelAndView modelAndView, @RequestParam Map<String, String> requestParams, RedirectAttributes redir){
        Optional<Client> client = clientService.findClientByResetToken(requestParams.get("token"));
        Optional<Company> company = companyService.findCompanyByResetToken(requestParams.get("token"));

        if (client.isPresent()) {
            Client resetClient = client.get();
            resetClient.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));
            resetClient.setResetToken(null);
            clientService.save(resetClient);

            redir.addFlashAttribute("successMessage", "Pomyślnie zaaktualizowano hasło, proszę zalogować sie ponownie na stronie firmy");
            modelAndView.setViewName("redirect:/register");
            return  modelAndView;
        }

        else if (company.isPresent()){
            Company resetCompany = company.get();
            resetCompany.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));
            resetCompany.setResetToken(null);
            companyService.save(resetCompany);

            redir.addFlashAttribute("successMessage", "Pomyślnie zaaktualizowano hasło, proszę zalogować sie ponownie na stronie firmy");
            modelAndView.setViewName("redirect:/logout");
            return  modelAndView;
        }

        else{
            modelAndView.addObject("errorMessage", "Użyto nieprawidłowego przekierowania!");
            modelAndView.setViewName("resetPassword");
        }

        return modelAndView;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:/logout");
    }
}
