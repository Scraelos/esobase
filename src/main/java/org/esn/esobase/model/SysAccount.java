/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
public class SysAccount extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(unique = true)
    private String login;
    private String password;
    private Boolean isBlocked;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<SysAccountRole> roles;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Set<SysAccountRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SysAccountRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return this.login;
    }
    
    

}
