/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
@Table(indexes = {
    @Index(columnList = "rowNum", unique = false),
    @Index(columnList = "textEn", unique = false),
    @Index(columnList = "textRu", unique = false),
    @Index(columnList = "translator", unique = false)})
public class GSpreadSheetsJournalEntry extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    private Long rowNum;
    @Column(columnDefinition = "TEXT")
    private String textEn;
    @Column(columnDefinition = "TEXT")
    private String textRu;
    private String translator;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date changeTime;
    private Integer weight;
    @OneToMany(mappedBy = "spreadSheetsJournalEntry", fetch = FetchType.LAZY)
    private Set<TranslatedText> translatedTexts;

    public GSpreadSheetsJournalEntry() {
    }

    public GSpreadSheetsJournalEntry(Long rowNum, String textEn, String textRu, String translator, Integer weight) {
        this.rowNum = rowNum;
        this.textEn = textEn;
        this.textRu = textRu;
        this.translator = translator;
        this.weight = weight;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Set<TranslatedText> getTranslatedTexts() {
        return translatedTexts;
    }

    public void setTranslatedTexts(Set<TranslatedText> translatedTexts) {
        this.translatedTexts = translatedTexts;
    }

}
