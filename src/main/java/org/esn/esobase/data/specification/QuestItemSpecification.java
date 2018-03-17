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
import javax.persistence.criteria.SetJoin;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.QuestItem;
import org.esn.esobase.model.QuestStep;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class QuestItemSpecification implements Specification<QuestItem> {

    private final Quest quest;
    private final Set<TRANSLATE_STATUS> translateStatus;
    private final SysAccount translator;
    private final Boolean noTranslations;
    private final Boolean emptyTranslations;
    private final String searchString;

    public QuestItemSpecification(Quest quest, Set<TRANSLATE_STATUS> translateStatus, SysAccount translator, Boolean noTranslations, Boolean emptyTranslations, String searchString) {
        this.quest = quest;
        this.translateStatus = translateStatus;
        this.translator = translator;
        this.noTranslations = noTranslations;
        this.emptyTranslations = emptyTranslations;
        this.searchString = searchString;
    }

    @Override
    public Predicate toPredicate(Root<QuestItem> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.join("quests"), quest));
        if (searchString != null && (searchString.length() > 2)) {
            Join<Object, Object> join = root.join("name", JoinType.LEFT);
            Join<Object, Object> join1 = root.join("description", JoinType.LEFT);
            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(join.get("textEn")), searchPattern),
                    cb.like(cb.lower(join1.get("textEn")), searchPattern),
                    cb.like(cb.lower(join.get("textRu")), searchPattern),
                    cb.like(cb.lower(join1.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations || emptyTranslations || (translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                Join<Object, Object> join = root.join("name", JoinType.LEFT);
                Join<Object, Object> join1 = root.join("description", JoinType.LEFT);
                if (noTranslations) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNull(join.get("translator")),
                                    cb.isNotNull(root.get("name"))
                            ),
                            cb.and(
                                    cb.isNull(join1.get("translator")),
                                    cb.isNotNull(root.get("description"))
                            )
                    ));
                }
                if (emptyTranslations) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNotNull(root.get("name")),
                                    cb.isEmpty(join.get("translatedTexts")),
                                    cb.isNull(join.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(root.get("description")),
                                    cb.isEmpty(join1.get("translatedTexts")),
                                    cb.isNull(join1.get("translator"))
                            )
                    ));
                } else if ((translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                    Join<Object, Object> join4 = join.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join5 = join1.join("translatedTexts", JoinType.LEFT);

                    if (translateStatus != null && !translateStatus.isEmpty() && translator != null) {

                        predicates.add(cb.or(
                                cb.and(
                                        join4.get("status").in(translateStatus),
                                        cb.equal(join4.get("author"), translator)
                                ),
                                cb.and(
                                        join5.get("status").in(translateStatus),
                                        cb.equal(join5.get("author"), translator)
                                )
                        ));
                    } else if (translator != null) {
                        predicates.add(cb.or(
                                cb.equal(join4.get("author"), translator),
                                cb.equal(join5.get("author"), translator)
                        ));
                    } else if (translateStatus != null && !translateStatus.isEmpty()) {
                        predicates.add(cb.or(
                                join4.get("status").in(translateStatus),
                                join5.get("status").in(translateStatus)
                        ));
                    }
                }

            }
        }

        cq.distinct(true);
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }

}
