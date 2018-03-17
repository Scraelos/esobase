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
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class QuestSpecification implements Specification<Quest> {

    private Set<TRANSLATE_STATUS> translateStatus;
    private SysAccount translator;
    private Boolean noTranslations;
    private Boolean emptyTranslations;
    private Location location;
    private String searchString;

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

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public Predicate toPredicate(Root<Quest> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        root.fetch("location", JoinType.LEFT).fetch("parentLocation", JoinType.LEFT);
        if (location != null) {
            predicates.add(cb.or(
                    cb.equal(root.get("location"), location),
                    cb.equal(root.get("location").get("parentLocation"), location)
            ));
        }
        if (searchString != null && (searchString.length() > 2)) {
            SetJoin<Object, Object> stepsJoin = root.joinSet("steps", JoinType.LEFT);
            SetJoin<Object, Object> stepsDirectionsJoin = stepsJoin.joinSet("directions", JoinType.LEFT);
            SetJoin<Object, Object> itemsJoin = root.joinSet("items", JoinType.LEFT);
            Join<Object, Object> join = root.join("sheetsQuestName", JoinType.LEFT);
            Join<Object, Object> join1 = root.join("sheetsQuestDescription", JoinType.LEFT);
            Join<Object, Object> join2 = stepsJoin.join("sheetsJournalEntry", JoinType.LEFT);
            Join<Object, Object> join3 = stepsDirectionsJoin.join("sheetsQuestDirection", JoinType.LEFT);
            Join<Object, Object> join4 = itemsJoin.join("name", JoinType.LEFT);
            Join<Object, Object> join5 = itemsJoin.join("description", JoinType.LEFT);
            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(join.get("textEn")), searchPattern),
                    cb.like(cb.lower(join1.get("textEn")), searchPattern),
                    cb.like(cb.lower(join2.get("textEn")), searchPattern),
                    cb.like(cb.lower(join3.get("textEn")), searchPattern),
                    cb.like(cb.lower(join4.get("textEn")), searchPattern),
                    cb.like(cb.lower(join5.get("textEn")), searchPattern),
                    cb.like(cb.lower(join.get("textRu")), searchPattern),
                    cb.like(cb.lower(join1.get("textRu")), searchPattern),
                    cb.like(cb.lower(join2.get("textRu")), searchPattern),
                    cb.like(cb.lower(join3.get("textRu")), searchPattern),
                    cb.like(cb.lower(join4.get("textRu")), searchPattern),
                    cb.like(cb.lower(join5.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations || emptyTranslations || (translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                SetJoin<Object, Object> stepsJoin = root.joinSet("steps", JoinType.LEFT);
                SetJoin<Object, Object> stepsDirectionsJoin = stepsJoin.joinSet("directions", JoinType.LEFT);
                SetJoin<Object, Object> itemsJoin = root.joinSet("items", JoinType.LEFT);
                Join<Object, Object> join = root.join("sheetsQuestName", JoinType.LEFT);
                Join<Object, Object> join1 = root.join("sheetsQuestDescription", JoinType.LEFT);
                Join<Object, Object> join2 = stepsJoin.join("sheetsJournalEntry", JoinType.LEFT);
                Join<Object, Object> join3 = stepsDirectionsJoin.join("sheetsQuestDirection", JoinType.LEFT);
                Join<Object, Object> join8 = itemsJoin.join("name", JoinType.LEFT);
                Join<Object, Object> join9 = itemsJoin.join("description", JoinType.LEFT);
                if (noTranslations) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNull(join.get("translator")),
                                    cb.isNotNull(root.get("sheetsQuestName"))
                            ),
                            cb.and(
                                    cb.isNull(join1.get("translator")),
                                    cb.isNotNull(root.get("sheetsQuestDescription"))
                            ),
                            cb.and(
                                    cb.isNull(join2.get("translator")),
                                    cb.isNotNull(stepsJoin.get("sheetsJournalEntry"))
                            ),
                            cb.and(
                                    cb.isNull(join3.get("translator")),
                                    cb.isNotNull(stepsDirectionsJoin.get("sheetsQuestDirection"))
                            ),
                            cb.and(
                                    cb.isNull(join8.get("translator")),
                                    cb.isNotNull(itemsJoin.get("name"))
                            ),
                            cb.and(
                                    cb.isNull(join9.get("translator")),
                                    cb.isNotNull(itemsJoin.get("description"))
                            )
                    ));
                }
                if (emptyTranslations) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNotNull(root.get("sheetsQuestName")),
                                    cb.isEmpty(join.get("translatedTexts")),
                                    cb.isNull(join.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(root.get("sheetsQuestDescription")),
                                    cb.isEmpty(join1.get("translatedTexts")),
                                    cb.isNull(join1.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(stepsJoin.get("sheetsJournalEntry")),
                                    cb.isEmpty(join2.get("translatedTexts")),
                                    cb.isNull(join2.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(stepsDirectionsJoin.get("sheetsQuestDirection")),
                                    cb.isEmpty(join3.get("translatedTexts")),
                                    cb.isNull(join3.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(itemsJoin.get("name")),
                                    cb.isEmpty(join8.get("translatedTexts")),
                                    cb.isNull(join8.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(itemsJoin.get("description")),
                                    cb.isEmpty(join9.get("translatedTexts")),
                                    cb.isNull(join9.get("translator"))
                            )
                    ));
                } else if ((translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                    Join<Object, Object> join4 = join.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join5 = join1.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join6 = join2.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join7 = join3.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join10 = join8.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join11 = join9.join("translatedTexts", JoinType.LEFT);
                    if (translateStatus != null && !translateStatus.isEmpty() && translator != null) {

                        predicates.add(cb.or(
                                cb.and(
                                        join4.get("status").in(translateStatus),
                                        cb.equal(join4.get("author"), translator)
                                ),
                                cb.and(
                                        join5.get("status").in(translateStatus),
                                        cb.equal(join5.get("author"), translator)
                                ),
                                cb.and(
                                        join6.get("status").in(translateStatus),
                                        cb.equal(join6.get("author"), translator)
                                ),
                                cb.and(
                                        join7.get("status").in(translateStatus),
                                        cb.equal(join7.get("author"), translator)
                                ),
                                cb.and(
                                        join10.get("status").in(translateStatus),
                                        cb.equal(join10.get("author"), translator)
                                ),
                                cb.and(
                                        join11.get("status").in(translateStatus),
                                        cb.equal(join11.get("author"), translator)
                                )
                        ));
                    } else if (translator != null) {
                        predicates.add(cb.or(
                                cb.equal(join4.get("author"), translator),
                                cb.equal(join5.get("author"), translator),
                                cb.equal(join6.get("author"), translator),
                                cb.equal(join7.get("author"), translator),
                                cb.equal(join10.get("author"), translator),
                                cb.equal(join11.get("author"), translator)
                        ));
                    } else if (translateStatus != null && !translateStatus.isEmpty()) {
                        predicates.add(cb.or(
                                join4.get("status").in(translateStatus),
                                join5.get("status").in(translateStatus),
                                join6.get("status").in(translateStatus),
                                join7.get("status").in(translateStatus),
                                join10.get("status").in(translateStatus),
                                join11.get("status").in(translateStatus)
                        ));
                    }
                }

            }
        }

        cq.distinct(true);
        cq.orderBy(cb.asc(root.get("name")));
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }

}
