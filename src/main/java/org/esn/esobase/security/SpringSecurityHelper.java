/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.security;

import org.esn.esobase.model.SysAccount;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * вспомогательный класс
 *
 * @author scraelos
 */
public class SpringSecurityHelper {

    /**
     * возвращает true, если пользователь в текущей сессии обладает указанной
     * ролью
     *
     * @param role
     * @return
     */
    public static boolean hasRole(String role) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    //возвращает данные учетной записи пользователя
    public static SysAccount getSysAccount() {
        EsnUser user = (EsnUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getSysAccount();
    }
}
