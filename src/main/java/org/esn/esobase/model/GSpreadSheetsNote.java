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
    @Index(columnList = "rowNum", unique = false)
    ,
    @Index(columnList = "translator", unique = false)
    ,
    @Index(columnList = "aId,bId,cId", unique = false)})
public class GSpreadSheetsNote extends DAO implements GSpreadSheetEntity, TranslatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private Long aId;
    private Long bId;
    private Long cId;
    private Long rowNum;
    @Column(columnDefinition = "TEXT")
    private String textEn;
    @Column(columnDefinition = "TEXT")
    private String textRu;
    private String translator;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date changeTime;
    private Integer weight;
    @OneToMany(mappedBy = "spreadSheetsNote", fetch = FetchType.LAZY)
    private Set<TranslatedText> translatedTexts;
    private Boolean deprecated;

    public GSpreadSheetsNote() {
    }

    public GSpreadSheetsNote(Long rowNum, String textEn, String textRu, String translator, Integer weight) {
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

    @Override
    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
    }

    @Override
    public String getTextEn() {
        return textEn;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    @Override
    public String getTextRu() {
        return textRu;
    }

    public void setTextRu(String textRu) {
        this.textRu = textRu;
    }

    @Override
    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    @Override
    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    @Override
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

    @Override
    public Long getaId() {
        return aId;
    }

    public void setaId(Long aId) {
        this.aId = aId;
    }

    @Override
    public Long getbId() {
        return bId;
    }

    public void setbId(Long bId) {
        this.bId = bId;
    }

    @Override
    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    @Override
    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

}
