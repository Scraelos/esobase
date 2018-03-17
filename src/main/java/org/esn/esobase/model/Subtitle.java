/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.esn.esobase.model.lib.DAO;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author scraelos
 */
@Entity
@Table(indexes = {
    @Index(columnList = "nextSubtitle_id", unique = true)})
public class Subtitle extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(columnDefinition = "TEXT", name = "text_en")
    private String text;
    @Column(columnDefinition = "TEXT", name = "text_ru")
    private String textRu;
    @Column(columnDefinition = "TEXT")
    private String textRawRu;
    @ManyToOne
    private Npc npc;
    @ManyToOne
    private GSpreadSheetsNpcPhrase extNpcPhrase;
    @Fetch(value = FetchMode.SELECT)
    @OneToMany(mappedBy = "subtitle", fetch = FetchType.LAZY)
    private Set<TranslatedText> translations;
    private Integer weight;
    @OneToOne
    @JoinColumn(unique = true)
    private Subtitle nextSubtitle;
    @OneToOne(mappedBy = "nextSubtitle", fetch = FetchType.LAZY)
    private Subtitle previousSubtitle;
    private Boolean extNpcPhraseFailed;

    public Subtitle() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Subtitle(String text, String textRu, Npc npc) {
        this.text = text;
        this.textRu = textRu;
        this.npc = npc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Npc getNpc() {
        return npc;
    }

    public void setNpc(Npc npc) {
        this.npc = npc;
    }

    public String getTextRu() {
        return textRu;
    }

    public void setTextRu(String textRu) {
        this.textRu = textRu;
    }

    public GSpreadSheetsNpcPhrase getExtNpcPhrase() {
        return extNpcPhrase;
    }

    public void setExtNpcPhrase(GSpreadSheetsNpcPhrase extNpcPhrase) {
        this.extNpcPhrase = extNpcPhrase;
    }

    public String getTextRawRu() {
        return textRawRu;
    }

    public void setTextRawRu(String textRawRu) {
        this.textRawRu = textRawRu;
    }

    public Set<TranslatedText> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<TranslatedText> translations) {
        this.translations = translations;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Subtitle getNextSubtitle() {
        return nextSubtitle;
    }

    public void setNextSubtitle(Subtitle nextSubtitle) {
        this.nextSubtitle = nextSubtitle;
    }

    public Subtitle getPreviousSubtitle() {
        return previousSubtitle;
    }

    public void setPreviousSubtitle(Subtitle previousSubtitle) {
        this.previousSubtitle = previousSubtitle;
    }

    public Boolean getExtNpcPhraseFailed() {
        return extNpcPhraseFailed;
    }

    public void setExtNpcPhraseFailed(Boolean extNpcPhraseFailed) {
        this.extNpcPhraseFailed = extNpcPhraseFailed;
    }

}
