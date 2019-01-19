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
import org.esn.esobase.model.QuestDirection;
import org.esn.esobase.model.QuestStep;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class QuestDirectionSpecification implements Specification<QuestDirection> {

    private final QuestStep step;
    private final Set<TRANSLATE_STATUS> translateStatus;
    private final SysAccount translator;
    private final Boolean noTranslations;
    private final Boolean emptyTranslations;
    private final String searchString;

    public QuestDirectionSpecification(QuestStep step, Set<TRANSLATE_STATUS> translateStatus, SysAccount translator, Boolean noTranslations, Boolean emptyTranslations, String searchString) {
        this.step = step;
        this.translateStatus = translateStatus;
        this.translator = translator;
        this.noTranslations = noTranslations;
        this.emptyTranslations = emptyTranslations;
        this.searchString = searchString;
    }

    @Override
    public Predicate toPredicate(Root<QuestDirection> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("step"), step));
        if (searchString != null && (searchString.length() > 2)) {
            Join<Object, Object> join = root.join("sheetsQuestDirection", JoinType.LEFT);
            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(join.get("textEn")), searchPattern),
                    cb.like(cb.lower(join.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations || emptyTranslations || (translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                Join<Object, Object> join = root.join("sheetsQuestDirection", JoinType.LEFT);
                if (noTranslations) {
                    predicates.add(
                            cb.and(
                                    cb.isNull(join.get("translator")),
                                    cb.isNotNull(root.get("sheetsQuestDirection"))
                            )
                    );
                }
                if (emptyTranslations) {
                    Join<Object, Object> join4 = join.join("translatedTexts", JoinType.LEFT);
                    predicates.add(
                            cb.or(
                                    cb.and(
                                            cb.isNotNull(root.get("sheetsQuestDirection")),
                                            cb.isEmpty(join.get("translatedTexts")),
                                            cb.isNull(join.get("translator"))
                                    ),
                                    cb.equal(join4.get("status"), TRANSLATE_STATUS.DIRTY)
                            )
                    );
                } else if ((translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                    Join<Object, Object> join4 = join.join("translatedTexts", JoinType.LEFT);

                    if (translateStatus != null && !translateStatus.isEmpty() && translator != null) {

                        predicates.add(
                                cb.and(
                                        join4.get("status").in(translateStatus),
                                        cb.equal(join4.get("author"), translator)
                                )
                        );
                    } else if (translator != null) {
                        predicates.add(
                                cb.equal(join4.get("author"), translator)
                        );
                    } else if (translateStatus != null && !translateStatus.isEmpty()) {
                        predicates.add(
                                join4.get("status").in(translateStatus)
                        );
                    }
                }

            }
        }

        cq.distinct(true);
        //cq.orderBy(cb.asc(root.get("weight")));
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }

}
