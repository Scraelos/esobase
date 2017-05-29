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
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class LocationSpecification implements Specification<Location> {

    private TRANSLATE_STATUS translateStatus;
    private SysAccount translator;
    private Boolean noTranslations;
    private Boolean emptyTranslations;

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

    @Override
    public Predicate toPredicate(Root<Location> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        Join<Object, Object> npcJoin = root.join("npcs");

        if (noTranslations) {
            predicates.add(cb.lt(npcJoin.get("progress"), BigDecimal.ONE));
        }

        if (emptyTranslations || translateStatus != null || translator != null) {
            Join<Object, Object> topicsJoin = npcJoin.joinSet("topics", JoinType.LEFT);
            Join<Object, Object> subtitlesJoin = npcJoin.joinSet("subtitles", JoinType.LEFT);

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

        query.distinct(true);
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }

}
