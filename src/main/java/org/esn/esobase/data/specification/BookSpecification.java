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
import org.esn.esobase.model.Book;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class BookSpecification implements Specification<Book> {

    private Set<TRANSLATE_STATUS> translateStatus;
    private SysAccount translator;
    private Boolean noTranslations;
    private Boolean emptyTranslations;
    private Location location;
    private String searchString;
    private Location subLocation;

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setSubLocation(Location subLocation) {
        this.subLocation = subLocation;
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
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate result = null;
        List<Predicate> predicates = new ArrayList<>();
        Join<Object, Object> textJoin = root.join("bookText");
        if (subLocation != null) {
            predicates.add(cb.equal(root.join("locations").get("id"), subLocation.getId()));
        } else if (location != null) {
            predicates.add(cb.equal(root.join("locations").get("parentLocation"), location));
        }
        if (searchString != null && (searchString.length() > 2)) {

            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nameEn")), searchPattern),
                    cb.like(cb.lower(textJoin.get("textEn")), searchPattern),
                    cb.like(cb.lower(root.get("nameRu")), searchPattern),
                    cb.like(cb.lower(textJoin.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations) {
                predicates.add(cb.or(
                        cb.equal(root.get("nameRu"), root.get("nameEn")),
                        cb.equal(textJoin.get("textRu"), textJoin.get("textEn")),
                        cb.isNull(root.get("nameRu")),
                        cb.isNull(textJoin.get("textRu"))
                ));
            }
            if (emptyTranslations) {
                predicates.add(cb.or(
                        cb.and(
                                cb.isEmpty(root.get("nameTranslations")),
                                cb.or(
                                cb.equal(root.get("nameRu"), root.get("nameEn")),cb.isNull(root.get("nameRu")))
                        ),
                        cb.and(
                                cb.isEmpty(textJoin.get("translations")),
                                cb.or(
                                cb.equal(textJoin.get("textRu"), textJoin.get("textEn")),cb.isNull(textJoin.get("textRu")))
                        )
                ));
            } else if ((translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                Join<Object, Object> join4 = root.join("nameTranslations", JoinType.LEFT);
                Join<Object, Object> join5 = textJoin.join("translations", JoinType.LEFT);

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
        query.distinct(true);
        query.orderBy(cb.asc(root.get("nameEn")));
        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        return result;
    }

}
