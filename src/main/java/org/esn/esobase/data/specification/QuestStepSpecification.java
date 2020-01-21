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
import org.esn.esobase.model.QuestDirection;
import org.esn.esobase.model.QuestStep;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class QuestStepSpecification implements Specification<QuestStep> {

    private Quest quest;
    private Set<TRANSLATE_STATUS> translateStatus;
    private SysAccount translator;
    private Boolean noTranslations;
    private Boolean emptyTranslations;
    private String searchString;

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public void setTranslateStatus(Set<TRANSLATE_STATUS> translateStatus) {
        this.translateStatus = translateStatus;
    }

    public void setTranslator(SysAccount translator) {
        this.translator = translator;
    }

    public void setNoTranslations(Boolean noTranslations) {
        this.noTranslations = noTranslations;
    }

    public void setEmptyTranslations(Boolean emptyTranslations) {
        this.emptyTranslations = emptyTranslations;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public Predicate toPredicate(Root<QuestStep> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("quest"), quest));
        if (searchString != null && (searchString.length() > 2)) {
            SetJoin<Object, Object> stepsDirectionsJoin = root.joinSet("directions", JoinType.LEFT);
            Join<Object, Object> join = root.join("sheetsJournalEntry", JoinType.LEFT);
            Join<Object, Object> join1 = stepsDirectionsJoin.join("sheetsQuestDirection", JoinType.LEFT);
            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(join.get("textEn")), searchPattern),
                    cb.like(cb.lower(join1.get("textEn")), searchPattern),
                    cb.like(cb.lower(join.get("textRu")), searchPattern),
                    cb.like(cb.lower(join1.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations || emptyTranslations || (translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                SetJoin<Object, Object> stepsDirectionsJoin = root.joinSet("directions", JoinType.LEFT);
                Join<Object, Object> join = root.join("sheetsJournalEntry", JoinType.LEFT);
                Join<Object, Object> join1 = stepsDirectionsJoin.join("sheetsQuestDirection", JoinType.LEFT);
                if (noTranslations) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNull(join.get("translator")),
                                    cb.isNotNull(root.get("sheetsJournalEntry"))
                            ),
                            cb.and(
                                    cb.isNull(join1.get("translator")),
                                    cb.isNotNull(stepsDirectionsJoin.get("sheetsQuestDirection"))
                            )
                    ));
                }
                if (emptyTranslations) {
                    Join<Object, Object> join4 = join.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join5 = join1.join("translatedTexts", JoinType.LEFT);
                    predicates.add(cb.or(
//                            cb.and(
//                                    cb.isNotNull(root.get("sheetsJournalEntry")),
//                                    cb.isEmpty(join.get("translatedTexts")),
//                                    cb.isNull(join.get("translator"))
//                            ),
                            cb.and(
                                    cb.isNotNull(stepsDirectionsJoin.get("sheetsQuestDirection")),
                                    cb.isEmpty(join1.get("translatedTexts")),
                                    cb.isNull(join1.get("translator")),
                                    cb.notEqual(stepsDirectionsJoin.get("directionType"), QuestDirection.DIRECTION_TYPE.hidden)
                            ),
//                            cb.equal(join4.get("status"), TRANSLATE_STATUS.DIRTY),
                            cb.equal(join5.get("status"), TRANSLATE_STATUS.DIRTY)
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
        cq.orderBy(cb.asc(root.get("weight")));
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }

}
