/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
    private final Set<TRANSLATE_STATUS> translateStatus;
    private final SysAccount translator;
    private final Boolean noTranslations;
    private final Boolean emptyTranslations;
    private final String searchString;

    public TopicSpecification(Npc npc, Set<TRANSLATE_STATUS> translateStatus, SysAccount translator, Boolean noTranslations, Boolean emptyTranslations, String searchString) {
        this.npc = npc;
        this.translateStatus = translateStatus;
        this.translator = translator;
        this.noTranslations = noTranslations;
        this.emptyTranslations = emptyTranslations;
        this.searchString = searchString;
    }

    @Override
    public Predicate toPredicate(Root<Topic> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("npc"), npc));
        if (searchString != null && (searchString.length() > 2)) {
            Join<Object, Object> join = root.join("extNpcPhrase", JoinType.LEFT);
            Join<Object, Object> join1 = root.join("extPlayerPhrase", JoinType.LEFT);
            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(join.get("textEn")), searchPattern),
                    cb.like(cb.lower(join1.get("textEn")), searchPattern),
                    cb.like(cb.lower(join.get("textRu")), searchPattern),
                    cb.like(cb.lower(join1.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations || emptyTranslations || (translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                Join<Object, Object> join = root.join("extNpcPhrase", JoinType.LEFT);
                Join<Object, Object> join1 = root.join("extPlayerPhrase", JoinType.LEFT);
                if (noTranslations) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNull(join.get("translator")),
                                    cb.isNotNull(root.get("extNpcPhrase"))
                            ),
                            cb.and(
                                    cb.isNull(join1.get("translator")),
                                    cb.isNotNull(root.get("extPlayerPhrase"))
                            )
                    ));
                }
                if (emptyTranslations) {
                    Join<Object, Object> join2 = join.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join3 = join1.join("translatedTexts", JoinType.LEFT);
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
                            ),
                            cb.equal(join2.get("status"), TRANSLATE_STATUS.DIRTY),
                            cb.equal(join3.get("status"), TRANSLATE_STATUS.DIRTY)
                    ));

                } else if ((translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                    Join<Object, Object> join2 = join.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join3 = join1.join("translatedTexts", JoinType.LEFT);
                    if (translateStatus != null && !translateStatus.isEmpty() && translator != null) {

                        predicates.add(cb.or(
                                cb.and(
                                        join2.get("status").in(translateStatus),
                                        cb.equal(join2.get("author"), translator)
                                ),
                                cb.and(
                                        join3.get("status").in(translateStatus),
                                        cb.equal(join3.get("author"), translator)
                                )
                        ));
                    } else if (translator != null) {
                        predicates.add(cb.or(
                                cb.equal(join2.get("author"), translator),
                                cb.equal(join3.get("author"), translator)
                        ));
                    } else if (translateStatus != null && !translateStatus.isEmpty()) {
                        predicates.add(cb.or(
                                join2.get("status").in(translateStatus),
                                join3.get("status").in(translateStatus)
                        ));
                    }
                }
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
