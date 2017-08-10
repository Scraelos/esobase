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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
public class QuestStep extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(length = 1024)
    private String textEn;
    @Column(length = 1024)
    private String textRu;
    private Integer weight;
    @OneToMany(mappedBy = "step")
    private Set<QuestDirection> directions;
    @ManyToOne
    private Quest quest;
    @ManyToOne
    private GSpreadSheetsJournalEntry sheetsJournalEntry;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Set<QuestDirection> getDirections() {
        return directions;
    }

    public void setDirections(Set<QuestDirection> directions) {
        this.directions = directions;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public GSpreadSheetsJournalEntry getSheetsJournalEntry() {
        return sheetsJournalEntry;
    }

    public void setSheetsJournalEntry(GSpreadSheetsJournalEntry sheetsJournalEntry) {
        this.sheetsJournalEntry = sheetsJournalEntry;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

}
