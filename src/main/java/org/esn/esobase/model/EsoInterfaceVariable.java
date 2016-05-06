/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
public class EsoInterfaceVariable extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String textEn;
    @Column(columnDefinition = "TEXT")
    private String textRu;
    private String translator;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date changeTime;
    @OneToMany(mappedBy = "esoInterfaceVariable")
    private Set<TranslatedText> translatedTexts;
    private Boolean changed;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long paramObject) {
        this.id = paramObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextEn() {
        return textEn;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    public String getTextRu() {
        return textRu;
    }

    public void setTextRu(String textRu) {
        this.textRu = textRu;
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

    public Set<TranslatedText> getTranslatedTexts() {
        return translatedTexts;
    }

    public void setTranslatedTexts(Set<TranslatedText> translatedTexts) {
        this.translatedTexts = translatedTexts;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

}
