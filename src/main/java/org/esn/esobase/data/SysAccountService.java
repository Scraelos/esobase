/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.esn.esobase.model.SysAccount;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
@Service
public class SysAccountService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void updateUserPassword(SysAccount account, String newPassword) {
        SysAccount a = em.find(SysAccount.class, account.getId());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(newPassword);
        a.setPassword(hashedPassword);
        em.merge(a);
    }

    @Transactional
    public String newApiKey(SysAccount account) {
        SysAccount a = em.find(SysAccount.class, account.getId());
        UUID apiKey = UUID.randomUUID();
        a.setApiKey(apiKey.toString());
        em.merge(a);
        return apiKey.toString();
    }

    @Transactional(readOnly = true)
    public String getApiKey(SysAccount account) {
        SysAccount a = em.find(SysAccount.class, account.getId());
        return a.getApiKey();
    }

    @Transactional(readOnly = true)
    public SysAccount getAccountByApi(String apiKey) throws NoResultException {
        TypedQuery<SysAccount> q = em.createQuery("select a from SysAccount a where a.apiKey=:apiKey", SysAccount.class);
        q.setParameter("apiKey", apiKey);
        return q.getSingleResult();
    }

}
