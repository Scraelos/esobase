/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

/**
 *
 * @author scraelos
 */
@Entity
public class Quest extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private String name;
    private String nameRu;
    private String descriptionEn;
    private String descriptionRu;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Npc> npcs;
    private BigDecimal progress;
    @ManyToOne
    private Location location;
    @OneToMany(mappedBy = "quest")
    private Set<QuestJournalEntry> questJournalEntrys;
    @OneToMany(mappedBy = "quest")
    private Set<QuestDirection> questDirections;
    @ManyToOne
    private GSpreadSheetsQuestName sheetsQuestName;
    @ManyToOne
    private GSpreadSheetsQuestDescription sheetsQuestDescription;
    @OneToMany(mappedBy = "quest")
    private Set<QuestStep> steps;

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

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public Set<Npc> getNpcs() {
        return npcs;
    }

    public void setNpcs(Set<Npc> npcs) {
        this.npcs = npcs;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    public Set<QuestJournalEntry> getQuestJournalEntrys() {
        return questJournalEntrys;
    }

    public void setQuestJournalEntrys(Set<QuestJournalEntry> questJournalEntrys) {
        this.questJournalEntrys = questJournalEntrys;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public Set<QuestDirection> getQuestDirections() {
        return questDirections;
    }

    public void setQuestDirections(Set<QuestDirection> questDirections) {
        this.questDirections = questDirections;
    }

    public GSpreadSheetsQuestName getSheetsQuestName() {
        return sheetsQuestName;
    }

    public void setSheetsQuestName(GSpreadSheetsQuestName sheetsQuestName) {
        this.sheetsQuestName = sheetsQuestName;
    }

    public GSpreadSheetsQuestDescription getSheetsQuestDescription() {
        return sheetsQuestDescription;
    }

    public void setSheetsQuestDescription(GSpreadSheetsQuestDescription sheetsQuestDescription) {
        this.sheetsQuestDescription = sheetsQuestDescription;
    }

    public Set<QuestStep> getSteps() {
        return steps;
    }

    public void setSteps(Set<QuestStep> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        String result = null;
        if (nameRu == null) {
            result = name;
        } else if (name == null) {
            result = nameRu;
        } else {
            result = name + "/" + nameRu;
        }
        if (progress != null) {
            String r = progress.multiply(BigDecimal.valueOf(100L).setScale(2, RoundingMode.HALF_DOWN)).setScale(0, RoundingMode.HALF_UP).toString() + "%";
            result += "(" + r + ")";
        }
        return result;
    }

}
