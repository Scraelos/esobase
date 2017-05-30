/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class NpcSpecification implements Specification<Npc> {

    private TRANSLATE_STATUS translateStatus;
    private SysAccount translator;
    private Boolean noTranslations;
    private Boolean emptyTranslations;
    private Quest quest;
    private Location location;
    private Location subLocation;
    private String searchString;

    public void setTranslateStatus(TRANSLATE_STATUS translateStatus) {
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

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setSubLocation(Location subLocation) {
        this.subLocation = subLocation;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public Predicate toPredicate(Root<Npc> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        root.fetch("location", JoinType.LEFT).fetch("parentLocation", JoinType.LEFT);
        if (subLocation != null) {
            predicates.add(
                    cb.equal(root.get("location"), subLocation)
            );
        } else if (location != null) {
            predicates.add(cb.or(
                    cb.equal(root.get("location"), location),
                    cb.equal(root.get("location").get("parentLocation"), location)
            ));
        }
        if (searchString != null && (searchString.length() > 2)) {
            Join<Object, Object> topicsJoin = root.joinSet("topics", JoinType.LEFT);
            Join<Object, Object> subtitlesJoin = root.joinSet("subtitles", JoinType.LEFT);
            Join<Object, Object> join = topicsJoin.join("extNpcPhrase", JoinType.LEFT);
            Join<Object, Object> join1 = topicsJoin.join("extPlayerPhrase", JoinType.LEFT);
            Join<Object, Object> join2 = subtitlesJoin.join("extNpcPhrase", JoinType.LEFT);
            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(join.get("textEn")), searchPattern),
                    cb.like(cb.lower(join1.get("textEn")), searchPattern),
                    cb.like(cb.lower(join2.get("textEn")), searchPattern),
                    cb.like(cb.lower(join.get("textRu")), searchPattern),
                    cb.like(cb.lower(join1.get("textRu")), searchPattern),
                    cb.like(cb.lower(join2.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations) {
                predicates.add(cb.lt(root.get("progress"), BigDecimal.ONE));
            }
            if (quest != null) {
                predicates.add(cb.equal(root.join("quests").get("id"), quest.getId()));
            }
            if (emptyTranslations || translateStatus != null || translator != null) {
                Join<Object, Object> topicsJoin = root.joinSet("topics", JoinType.LEFT);
                Join<Object, Object> subtitlesJoin = root.joinSet("subtitles", JoinType.LEFT);

                if (emptyTranslations) {
                    Join<Object, Object> join = topicsJoin.join("extNpcPhrase", JoinType.LEFT);
                    Join<Object, Object> join1 = topicsJoin.join("extPlayerPhrase", JoinType.LEFT);
                    Join<Object, Object> join2 = subtitlesJoin.join("extNpcPhrase", JoinType.LEFT);

                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNotNull(topicsJoin.get("extNpcPhrase")),
                                    cb.isEmpty(join.get("translatedTexts")),
                                    cb.isNull(join.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(topicsJoin.get("extPlayerPhrase")),
                                    cb.isEmpty(join1.get("translatedTexts")),
                                    cb.isNull(join1.get("translator"))
                            ),
                            cb.and(
                                    cb.isNotNull(subtitlesJoin.get("extNpcPhrase")),
                                    cb.isEmpty(join2.get("translatedTexts")),
                                    cb.isNull(join2.get("translator"))
                            )
                    ));
                } else if (translateStatus != null || translator != null) {
                    Join<Object, Object> join = topicsJoin.join("npcTranslations", JoinType.LEFT);
                    Join<Object, Object> join1 = topicsJoin.join("playerTranslations", JoinType.LEFT);
                    Join<Object, Object> join2 = topicsJoin.join("extNpcPhrase", JoinType.LEFT).join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join3 = topicsJoin.join("extPlayerPhrase", JoinType.LEFT).join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join4 = subtitlesJoin.join("translations", JoinType.LEFT);
                    Join<Object, Object> join5 = subtitlesJoin.join("extNpcPhrase", JoinType.LEFT).join("translatedTexts", JoinType.LEFT);
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
                                ),
                                cb.and(
                                        cb.equal(join4.get("status"), translateStatus),
                                        cb.equal(join4.get("author"), translator)
                                ),
                                cb.and(
                                        cb.equal(join5.get("status"), translateStatus),
                                        cb.equal(join5.get("author"), translator)
                                )
                        ));
                    } else if (translator != null) {
                        predicates.add(cb.or(
                                cb.equal(join.get("author"), translator),
                                cb.equal(join1.get("author"), translator),
                                cb.equal(join2.get("author"), translator),
                                cb.equal(join3.get("author"), translator),
                                cb.equal(join4.get("author"), translator),
                                cb.equal(join5.get("author"), translator)
                        ));
                    } else if (translateStatus != null) {
                        predicates.add(cb.or(
                                cb.equal(join.get("status"), translateStatus),
                                cb.equal(join1.get("status"), translateStatus),
                                cb.equal(join2.get("status"), translateStatus),
                                cb.equal(join3.get("status"), translateStatus),
                                cb.equal(join4.get("status"), translateStatus),
                                cb.equal(join5.get("status"), translateStatus)
                        ));
                    }
                }

            }
        }

        query.distinct(true);
        query.orderBy(cb.asc(root.get("name")));
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }

}
