/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.security;

import java.util.Collection;
import org.esn.esobase.model.SysAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 *
 * @author scraelos
 */
public class EsnUser extends User {

    private final SysAccount sysAccount;
    public static final GrantedAuthority user_role = new SimpleGrantedAuthority("ROLE_USER");

    public EsnUser(Collection<? extends GrantedAuthority> authorities, SysAccount account) {
        super(account.getLogin(), account.getPassword(), !account.getIsBlocked(), true, true, true, authorities);
        sysAccount = account;
    }

    public SysAccount getSysAccount() {
        return sysAccount;
    }

}
