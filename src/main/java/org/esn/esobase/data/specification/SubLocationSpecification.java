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
import org.esn.esobase.model.Location;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class SubLocationSpecification implements Specification<Location> {

    private Set<TRANSLATE_STATUS> translateStatus;
    private SysAccount translator;
    private Boolean noTranslations;
    private Boolean emptyTranslations;
    private String searchString;
    private Location parentLocation;

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

    public void setParentLocation(Location parentLocation) {
        this.parentLocation = parentLocation;
    }

    @Override
    public Predicate toPredicate(Root<Location> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        if (parentLocation != null) {
            predicates.add(cb.or(cb.equal(root.get("parentLocation"), parentLocation), cb.equal(root.get("id"), parentLocation.getId())));
        }
        Join<Object, Object> npcJoin = root.join("npcs");

        if (searchString != null && (searchString.length() > 2)) {
            Join<Object, Object> topicsJoin = npcJoin.joinSet("topics", JoinType.LEFT);
            Join<Object, Object> subtitlesJoin = npcJoin.joinSet("subtitles", JoinType.LEFT);
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
            if (noTranslations || emptyTranslations || (translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                Join<Object, Object> topicsJoin = npcJoin.joinSet("topics", JoinType.LEFT);
                Join<Object, Object> subtitlesJoin = npcJoin.joinSet("subtitles", JoinType.LEFT);
                Join<Object, Object> join = topicsJoin.join("extNpcPhrase", JoinType.LEFT);
                Join<Object, Object> join1 = topicsJoin.join("extPlayerPhrase", JoinType.LEFT);
                Join<Object, Object> join2 = subtitlesJoin.join("extNpcPhrase", JoinType.LEFT);
                if (noTranslations) {
                    predicates.add(cb.or(
                            cb.and(
                                    cb.isNull(join.get("translator")),
                                    cb.isNotNull(topicsJoin.get("extNpcPhrase"))
                            ),
                            cb.and(
                                    cb.isNull(join1.get("translator")),
                                    cb.isNotNull(topicsJoin.get("extPlayerPhrase"))
                            ),
                            cb.and(
                                    cb.isNull(join2.get("translator")),
                                    cb.isNotNull(subtitlesJoin.get("extNpcPhrase"))
                            )
                    ));
                }
                if (emptyTranslations) {
                    Join<Object, Object> join3 = join.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join4 = join1.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join5 = join2.join("translatedTexts", JoinType.LEFT);
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
                            ),
                            cb.equal(join3.get("status"), TRANSLATE_STATUS.DIRTY),
                            cb.equal(join4.get("status"), TRANSLATE_STATUS.DIRTY),
                            cb.equal(join5.get("status"), TRANSLATE_STATUS.DIRTY)
                    ));
                } else if ((translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                    Join<Object, Object> join3 = join.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join4 = join1.join("translatedTexts", JoinType.LEFT);
                    Join<Object, Object> join5 = join2.join("translatedTexts", JoinType.LEFT);
                    if (translateStatus != null && !translateStatus.isEmpty() && translator != null) {

                        predicates.add(cb.or(
                                cb.and(
                                        join3.get("status").in(translateStatus),
                                        cb.equal(join3.get("author"), translator)
                                ),
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
                                cb.equal(join3.get("author"), translator),
                                cb.equal(join4.get("author"), translator),
                                cb.equal(join5.get("author"), translator)
                        ));
                    } else if (translateStatus != null && !translateStatus.isEmpty()) {
                        predicates.add(cb.or(
                                join3.get("status").in(translateStatus),
                                join4.get("status").in(translateStatus),
                                join5.get("status").in(translateStatus)
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
