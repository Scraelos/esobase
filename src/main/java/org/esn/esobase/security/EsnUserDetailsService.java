/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.security;

import java.util.ArrayList;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.SysAccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @author scraelos
 */
public class EsnUserDetailsService implements UserDetailsService {

    @Autowired
    DBService service;

    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {
        service.createRoles();
        service.createDefaultAdminUser();
        SysAccount account = service.getAccount(string);
        ArrayList<GrantedAuthority> authoritys = new ArrayList<GrantedAuthority>();
        for (SysAccountRole role : account.getRoles()) {
            authoritys.add(new SimpleGrantedAuthority(role.getNic()));
        }

        UserDetails result = new EsnUser(authoritys, account);
        return result;
    }

}
