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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.Topic;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class TopicSpecification implements Specification<Topic> {

    private final Npc npc;
    private final TRANSLATE_STATUS translateStatus;
    private final SysAccount translator;
    private final Boolean noTranslations;
    private final Boolean emptyTranslations;

    public TopicSpecification(Npc npc, TRANSLATE_STATUS translateStatus, SysAccount translator, Boolean noTranslations, Boolean emptyTranslations) {
        this.npc = npc;
        this.translateStatus = translateStatus;
        this.translator = translator;
        this.noTranslations = noTranslations;
        this.emptyTranslations = emptyTranslations;
    }

    @Override
    public Predicate toPredicate(Root<Topic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("npc"), npc));
        if (noTranslations) {
            predicates.add(cb.or(
                    cb.isNull(root.get("extNpcPhrase").get("translator")),
                    cb.isNull(root.get("extPlayerPhrase").get("translator"))
            ));
        }
        if (emptyTranslations) {

            Join<Object, Object> join = root.join("extNpcPhrase", JoinType.LEFT);
            Join<Object, Object> join1 = root.join("extPlayerPhrase", JoinType.LEFT);

            predicates.add(cb.or(
                    cb.and(
                            cb.isNotNull(root.get("extNpcPhrase")),
                            cb.isEmpty(join.get("translatedTexts")),
                            cb.isNull(join.get("translator"))
                    ),
                    cb.and(
                            cb.isNotNull(root.get("extPlayerPhrase")),
                            cb.isEmpty(join1.get("translatedTexts")),
                            cb.isNull(join1.get("translator"))
                    )
            ));

        } else if (translateStatus != null || translator != null) {
            Join<Object, Object> join = root.join("npcTranslations", JoinType.LEFT);
            Join<Object, Object> join1 = root.join("playerTranslations", JoinType.LEFT);
            Join<Object, Object> join2 = root.join("extNpcPhrase", JoinType.LEFT).join("translatedTexts", JoinType.LEFT);
            Join<Object, Object> join3 = root.join("extPlayerPhrase", JoinType.LEFT).join("translatedTexts", JoinType.LEFT);
            if (translateStatus != null && translator != null) {

                predicates.add(cb.or(
                        cb.and(
                                cb.equal(join.get("status"), translateStatus),
                                cb.equal(join.get("author"), translator)
                        ),
                        cb.and(
                                cb.equal(join1.get("status"), translateStatus),
                                cb.equal(join1.get("author"), translator)
                        ),
                        cb.and(
                                cb.equal(join2.get("status"), translateStatus),
                                cb.equal(join2.get("author"), translator)
                        ),
                        cb.and(
                                cb.equal(join3.get("status"), translateStatus),
                                cb.equal(join3.get("author"), translator)
                        )
                ));
            } else if (translator != null) {
                predicates.add(cb.or(
                        cb.equal(join.get("author"), translator),
                        cb.equal(join1.get("author"), translator),
                        cb.equal(join2.get("author"), translator),
                        cb.equal(join3.get("author"), translator)
                ));
            } else if (translateStatus != null) {
                predicates.add(cb.or(
                        cb.equal(join.get("status"), translateStatus),
                        cb.equal(join1.get("status"), translateStatus),
                        cb.equal(join2.get("status"), translateStatus),
                        cb.equal(join3.get("status"), translateStatus)
                ));
            }
        }
        query.distinct(true);
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }
}
