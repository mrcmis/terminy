package com.fis.is.terminy.services;

import com.fis.is.terminy.models.BaseEntity;
import com.fis.is.terminy.models.Client;
import com.fis.is.terminy.models.Company;
import com.fis.is.terminy.repositories.ClientRepository;
import com.fis.is.terminy.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CompanyRepository companyRepository;


    @Override
    //TODO
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        //Client one = clientRepository.getOne(1L);
        //Client one = optionalClient.get();

        BaseEntity logged;
        Optional<Client> optionalClient = clientRepository.findByLogin(login);
        Optional<Company> optionalCompany = companyRepository.findByLogin(login);

        if (optionalClient.isPresent()) {
            logged = optionalClient.get();
        } else if (optionalCompany.isPresent()){
            logged = optionalCompany.get();
        } else{
            throw new UsernameNotFoundException("Login not found");
        }

        //TODO
        String forceEagerFetch = logged.getUsername();
        Collection<? extends GrantedAuthority> grantedAuthorities = logged.getAuthorities();

        return logged;
    }
}
