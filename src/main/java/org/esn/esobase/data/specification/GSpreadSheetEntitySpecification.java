/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.specification;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.esn.esobase.model.GSpreadSheetEntity;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.ItemSubType;
import org.esn.esobase.model.ItemType;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class GSpreadSheetEntitySpecification implements Specification<GSpreadSheetEntity> {

    private ItemType itemType;
    private ItemSubType itemSubType;

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public void setItemSubType(ItemSubType itemSubType) {
        this.itemSubType = itemSubType;
    }

    @Override
    public Predicate toPredicate(Root<GSpreadSheetEntity> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("deprecated"), Boolean.FALSE));
        if (root instanceof GSpreadSheetsItemName) {
            if (itemType != null) {
                predicates.add(cb.equal(root.get("itemType"), itemType));
            }
            if (itemSubType != null) {
                predicates.add(cb.equal(root.get("itemSubType"), itemSubType));
            }
        }
        Predicate result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        return result;
    }

}
