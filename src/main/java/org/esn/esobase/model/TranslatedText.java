/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
public class TranslatedText extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String text;
    @ManyToOne(fetch = FetchType.EAGER)
    private SysAccount author;
    @ManyToOne
    private Topic playerTopic;
    @ManyToOne
    private Topic npcTopic;
    @ManyToOne
    private Greeting greeting;
    @ManyToOne
    private Subtitle subtitle;
    @Enumerated(EnumType.STRING)
    private TRANSLATE_STATUS status;
    @ManyToOne(fetch = FetchType.EAGER)
    private SysAccount approvedBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date changeTime;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date apptovedTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SysAccount getAuthor() {
        return author;
    }

    public void setAuthor(SysAccount author) {
        this.author = author;
    }

    public Topic getPlayerTopic() {
        return playerTopic;
    }

    public void setPlayerTopic(Topic playerTopic) {
        this.playerTopic = playerTopic;
    }

    public Topic getNpcTopic() {
        return npcTopic;
    }

    public void setNpcTopic(Topic npcTopic) {
        this.npcTopic = npcTopic;
    }

    public Greeting getGreeting() {
        return greeting;
    }

    public void setGreeting(Greeting greeting) {
        this.greeting = greeting;
    }

    public Subtitle getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Subtitle subtitle) {
        this.subtitle = subtitle;
    }

    public TRANSLATE_STATUS getStatus() {
        return status;
    }

    public void setStatus(TRANSLATE_STATUS status) {
        this.status = status;
    }

    public SysAccount getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(SysAccount approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }

    public Date getApptovedTime() {
        return apptovedTime;
    }

    public void setApptovedTime(Date apptovedTime) {
        this.apptovedTime = apptovedTime;
    }

}
