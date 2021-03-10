/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
@Table(indexes = {
    @Index(columnList = "aId,bId,cId", unique = true)})
public class EsoRawString extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private Long aId;
    private Long bId;
    private Long cId;
    @Column(columnDefinition = "TEXT")
    private String textEn;
    @Column(columnDefinition = "TEXT")
    private String textDe;
    @Column(columnDefinition = "TEXT")
    private String textFr;
    @Column(columnDefinition = "TEXT")
    private String textRu;
    @Column(columnDefinition = "TEXT")
    private String textJp;
    @Column(columnDefinition = "TEXT")
    private String textRuoff;
    @Column(length = 12)
    private String ver;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long paramObject) {
        this.id = paramObject;
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

    public String getTextEn() {
        return textEn;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    public String getTextDe() {
        return textDe;
    }

    public void setTextDe(String textDe) {
        this.textDe = textDe;
    }

    public String getTextFr() {
        return textFr;
    }

    public void setTextFr(String textFr) {
        this.textFr = textFr;
    }

    public String getTextRu() {
        return textRu;
    }

    public void setTextRu(String textRu) {
        this.textRu = textRu;
    }

    public String getTextJp() {
        return textJp;
    }

    public void setTextJp(String textJp) {
        this.textJp = textJp;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getTextRuoff() {
        return textRuoff;
    }

    public void setTextRuoff(String textRuoff) {
        this.textRuoff = textRuoff;
    }

}
