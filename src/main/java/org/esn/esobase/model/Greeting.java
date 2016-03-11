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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.esn.esobase.model.lib.DAO;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;

/**
 *
 * @author scraelos
 */
@Entity
public class Greeting extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private String num;
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
    @OneToMany(mappedBy = "greeting", fetch = FetchType.EAGER)
    private Set<TranslatedText> translations;
    private Integer weight;

    public Greeting() {
    }

    public Greeting(String num, String text, String textRu, Npc npc) {
        this.num = num;
        this.text = text;
        this.textRu = textRu;
        this.npc = npc;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Npc getNpc() {
        return npc;
    }

    public void setNpc(Npc npc) {
        this.npc = npc;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

}
