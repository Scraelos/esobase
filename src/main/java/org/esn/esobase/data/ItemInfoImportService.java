/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.ItemSubType;
import org.esn.esobase.model.ItemType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
@Service
public class ItemInfoImportService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void updateItem(String name, String icon, Long type, Long subType) {
        Query q = em.createQuery("select i from GSpreadSheetsItemName i where i.textEn=:textEn");
        q.setParameter("textEn", name);
        List<GSpreadSheetsItemName> resultList = q.getResultList();
        for (GSpreadSheetsItemName i : resultList) {
            if (icon != null) {
                i.setIcon(icon);
            }
            if (type != null) {
                ItemType itemType = em.find(ItemType.class, type);
                if (itemType != null) {
                    i.setItemType(itemType);
                }
            }
            if (subType != null) {
                ItemSubType itemSubType = em.find(ItemSubType.class, subType);
                if (itemSubType != null) {
                    i.setItemSubType(itemSubType);
                }
            }
            em.merge(i);
        }
    }
}
