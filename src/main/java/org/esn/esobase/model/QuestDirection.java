/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
public class QuestDirection extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(length = 1024)
    private String textEn;
    @Column(length = 1024)
    private String textRu;
    @ManyToOne
    private GSpreadSheetsQuestDirection sheetsQuestDirection;
    @ManyToOne
    private Quest quest;
    @ManyToOne
    private QuestStep step;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)")
    private DIRECTION_TYPE directionType;

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

    public GSpreadSheetsQuestDirection getSheetsQuestDirection() {
        return sheetsQuestDirection;
    }

    public void setSheetsQuestDirection(GSpreadSheetsQuestDirection sheetsQuestDirection) {
        this.sheetsQuestDirection = sheetsQuestDirection;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public QuestStep getStep() {
        return step;
    }

    public void setStep(QuestStep step) {
        this.step = step;
    }

    public DIRECTION_TYPE getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DIRECTION_TYPE directionType) {
        this.directionType = directionType;
    }

    public enum DIRECTION_TYPE {
        main,
        hints,
        hidden,
        optional

    }

}
