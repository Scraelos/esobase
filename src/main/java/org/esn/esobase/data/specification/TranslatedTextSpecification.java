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
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.TranslatedText;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class TranslatedTextSpecification implements Specification<TranslatedText> {

    private SysAccount author;
    private String translationType;
    private TRANSLATE_STATUS status;

    public void setAuthor(SysAccount author) {
        this.author = author;
    }

    public void setTranslationType(String translationType) {
        this.translationType = translationType;
    }

    public void setStatus(TRANSLATE_STATUS status) {
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<TranslatedText> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isNull(root.get("playerTopic")));
        predicates.add(cb.isNull(root.get("npcTopic")));
        predicates.add(cb.isNull(root.get("subtitle")));
        if (author != null) {
            predicates.add(cb.equal(root.get("author"), author));
        }
        if (translationType != null && !translationType.isEmpty()) {
            predicates.add(cb.isNotNull(root.get(translationType)));
        }
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        Predicate result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        return result;
    }

}
