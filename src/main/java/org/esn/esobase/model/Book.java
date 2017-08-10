/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
public class Book extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private Long aId;
    private Long bId;
    private Long cId;
    private String nameEn;
    private String nameRu;
    private String translator;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date changeTime;
    @OneToMany(mappedBy = "bookName", fetch = FetchType.LAZY)
    private Set<TranslatedText> nameTranslations;
    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<Location> locations;
    @OneToOne(fetch = FetchType.LAZY)
    private BookText bookText;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getaId() {
        return aId;
    }

    public void setaId(Long aId) {
        this.aId = aId;
    }

    public Long getbId() {
        return bId;
    }

    public void setbId(Long bId) {
        this.bId = bId;
    }

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public Set<Location> getLocations() {
        return locations;
    }

    public void setLocations(Set<Location> locations) {
        this.locations = locations;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    public Set<TranslatedText> getNameTranslations() {
        return nameTranslations;
    }

    public void setNameTranslations(Set<TranslatedText> nameTranslations) {
        this.nameTranslations = nameTranslations;
    }

    public BookText getBookText() {
        return bookText;
    }

    public void setBookText(BookText bookText) {
        this.bookText = bookText;
    }

    @Override
    public String toString() {
        String result = null;
        if (nameRu == null) {
            result = nameEn;
        } else if (nameEn == null) {
            result = nameRu;
        } else {
            result = nameEn + "/" + nameRu;
        }

        return result;
    }

}
