/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.esn.esobase.model.lib.DAO;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *
 * @author scraelos
 */
@Entity
public class Topic extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String playerText;
    @Column(columnDefinition = "TEXT")
    private String npcText;
    @Column(columnDefinition = "TEXT")
    private String playerTextRu;
    @Column(columnDefinition = "TEXT")
    private String playerTextRawRu;
    @Column(columnDefinition = "TEXT")
    private String npcTextRu;
    @Column(columnDefinition = "TEXT")
    private String npcTextRawRu;
    @ManyToOne
    private Npc npc;
    @ManyToOne
    private GSpreadSheetsPlayerPhrase extPlayerPhrase;
    @ManyToOne
    private GSpreadSheetsNpcPhrase extNpcPhrase;
    @Fetch(value = FetchMode.SELECT)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "playerTopic", fetch = FetchType.EAGER)
    private Set<TranslatedText> playerTranslations;
    @Fetch(value = FetchMode.SELECT)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "npcTopic", fetch = FetchType.EAGER)
    private Set<TranslatedText> npcTranslations;
    private Integer weight;
    @ManyToMany
    private Set<Topic> previousTopics;
    @ManyToMany(mappedBy = "previousTopics")
    private Set<Topic> nextTopics;

    public Topic() {
    }

    public Topic(String playerText, String npcText, String playerTextRu, String npcTextRu, Npc npc) {
        this.playerText = playerText;
        this.npcText = npcText;
        this.playerTextRu = playerTextRu;
        this.npcTextRu = npcTextRu;
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

    public String getPlayerText() {
        return playerText;
    }

    public void setPlayerText(String playerText) {
        this.playerText = playerText;
    }

    public String getNpcText() {
        return npcText;
    }

    public void setNpcText(String npcText) {
        this.npcText = npcText;
    }

    public Npc getNpc() {
        return npc;
    }

    public void setNpc(Npc npc) {
        this.npc = npc;
    }

    public String getPlayerTextRu() {
        return playerTextRu;
    }

    public void setPlayerTextRu(String playerTextRu) {
        this.playerTextRu = playerTextRu;
    }

    public String getNpcTextRu() {
        return npcTextRu;
    }

    public void setNpcTextRu(String npcTextRu) {
        this.npcTextRu = npcTextRu;
    }

    public GSpreadSheetsPlayerPhrase getExtPlayerPhrase() {
        return extPlayerPhrase;
    }

    public void setExtPlayerPhrase(GSpreadSheetsPlayerPhrase extPlayerPhrase) {
        this.extPlayerPhrase = extPlayerPhrase;
    }

    public GSpreadSheetsNpcPhrase getExtNpcPhrase() {
        return extNpcPhrase;
    }

    public void setExtNpcPhrase(GSpreadSheetsNpcPhrase extNpcPhrase) {
        this.extNpcPhrase = extNpcPhrase;
    }

    public String getNpcTextRawRu() {
        return npcTextRawRu;
    }

    public void setNpcTextRawRu(String npcTextRawRu) {
        this.npcTextRawRu = npcTextRawRu;
    }

    public String getPlayerTextRawRu() {
        return playerTextRawRu;
    }

    public void setPlayerTextRawRu(String playerTextRawRu) {
        this.playerTextRawRu = playerTextRawRu;
    }

    public Set<TranslatedText> getPlayerTranslations() {
        return playerTranslations;
    }

    public void setPlayerTranslations(Set<TranslatedText> playerTranslations) {
        this.playerTranslations = playerTranslations;
    }

    public Set<TranslatedText> getNpcTranslations() {
        return npcTranslations;
    }

    public void setNpcTranslations(Set<TranslatedText> npcTranslations) {
        this.npcTranslations = npcTranslations;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Set<Topic> getPreviousTopics() {
        return previousTopics;
    }

    public void setPreviousTopics(Set<Topic> previousTopics) {
        this.previousTopics = previousTopics;
    }

    public Set<Topic> getNextTopics() {
        return nextTopics;
    }

    public void setNextTopics(Set<Topic> nextTopics) {
        this.nextTopics = nextTopics;
    }

}
