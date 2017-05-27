/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.esn.esobase.model.Book;
import org.esn.esobase.model.Location;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author scraelos
 */
public class BookSpecification implements Specification<Book> {

    private Location location;
    private Location subLocation;

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setSubLocation(Location subLocation) {
        this.subLocation = subLocation;
    }

    @Override
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate result = null;
        if (subLocation != null) {
            result = cb.equal(root.join("locations").get("id"), subLocation.getId());
            query.distinct(true);
        } else if (location != null) {
            result = cb.equal(root.join("locations").get("parentLocation"), location);
            query.distinct(true);
        }
        return result;
    }

}
