package com.fis.is.terminy.converters;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrivilegesConverter {
    public static Collection<String> convertAuthoritiesToPrivilegesList(Collection<? extends GrantedAuthority> authorities){
        List<String> privileges = new ArrayList<>();
        if(authorities != null){
            for(GrantedAuthority authority : authorities){
                privileges.add(authority.toString());
            }
        }
        return privileges;
    }
}
