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
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
@Table(indexes = {
    @Index(columnList = "spreadSheetsNpcPhrase_id", unique = false)
    ,@Index(columnList = "spreadSheetsPlayerPhrase_id", unique = false)})
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
    @ManyToOne(fetch = FetchType.LAZY)
    private Topic playerTopic;
    @ManyToOne(fetch = FetchType.LAZY)
    private Topic npcTopic;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subtitle subtitle;
    @Enumerated(EnumType.STRING)
    private TRANSLATE_STATUS status;
    @ManyToOne(fetch = FetchType.EAGER)
    private SysAccount approvedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    private SysAccount correctedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    private SysAccount preApprovedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    private SysAccount rejectedBy;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createTime;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date changeTime;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date apptovedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsActivator spreadSheetsActivator;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsItemDescription spreadSheetsItemDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsItemName spreadSheetsItemName;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsJournalEntry spreadSheetsJournalEntry;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsLocationName spreadSheetsLocationName;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsNpcName spreadSheetsNpcName;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsNpcPhrase spreadSheetsNpcPhrase;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsPlayerPhrase spreadSheetsPlayerPhrase;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsQuestDescription spreadSheetsQuestDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsQuestDirection spreadSheetsQuestDirection;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsQuestName spreadSheetsQuestName;
    @ManyToOne(fetch = FetchType.LAZY)
    private EsoInterfaceVariable esoInterfaceVariable;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsAchievement spreadSheetsAchievement;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsAchievementDescription spreadSheetsAchievementDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsNote spreadSheetsNote;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsAbilityDescription sheetsAbilityDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsCollectible sheetsCollectible;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsCollectibleDescription sheetsCollectibleDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsLoadscreen sheetsLoadscreen;
    @ManyToOne(fetch = FetchType.LAZY)
    private BookText book;
    @ManyToOne(fetch = FetchType.LAZY)
    private Book bookName;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsQuestStartTip spreadSheetsQuestStartTip;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsQuestEndTip spreadSheetsQuestEndTip;

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

    public GSpreadSheetsActivator getSpreadSheetsActivator() {
        return spreadSheetsActivator;
    }

    public void setSpreadSheetsActivator(GSpreadSheetsActivator spreadSheetsActivator) {
        this.spreadSheetsActivator = spreadSheetsActivator;
    }

    public GSpreadSheetsItemDescription getSpreadSheetsItemDescription() {
        return spreadSheetsItemDescription;
    }

    public void setSpreadSheetsItemDescription(GSpreadSheetsItemDescription spreadSheetsItemDescription) {
        this.spreadSheetsItemDescription = spreadSheetsItemDescription;
    }

    public GSpreadSheetsItemName getSpreadSheetsItemName() {
        return spreadSheetsItemName;
    }

    public void setSpreadSheetsItemName(GSpreadSheetsItemName spreadSheetsItemName) {
        this.spreadSheetsItemName = spreadSheetsItemName;
    }

    public GSpreadSheetsJournalEntry getSpreadSheetsJournalEntry() {
        return spreadSheetsJournalEntry;
    }

    public void setSpreadSheetsJournalEntry(GSpreadSheetsJournalEntry spreadSheetsJournalEntry) {
        this.spreadSheetsJournalEntry = spreadSheetsJournalEntry;
    }

    public GSpreadSheetsLocationName getSpreadSheetsLocationName() {
        return spreadSheetsLocationName;
    }

    public void setSpreadSheetsLocationName(GSpreadSheetsLocationName spreadSheetsLocationName) {
        this.spreadSheetsLocationName = spreadSheetsLocationName;
    }

    public GSpreadSheetsNpcName getSpreadSheetsNpcName() {
        return spreadSheetsNpcName;
    }

    public void setSpreadSheetsNpcName(GSpreadSheetsNpcName spreadSheetsNpcName) {
        this.spreadSheetsNpcName = spreadSheetsNpcName;
    }

    public GSpreadSheetsNpcPhrase getSpreadSheetsNpcPhrase() {
        return spreadSheetsNpcPhrase;
    }

    public void setSpreadSheetsNpcPhrase(GSpreadSheetsNpcPhrase spreadSheetsNpcPhrase) {
        this.spreadSheetsNpcPhrase = spreadSheetsNpcPhrase;
    }

    public GSpreadSheetsPlayerPhrase getSpreadSheetsPlayerPhrase() {
        return spreadSheetsPlayerPhrase;
    }

    public void setSpreadSheetsPlayerPhrase(GSpreadSheetsPlayerPhrase spreadSheetsPlayerPhrase) {
        this.spreadSheetsPlayerPhrase = spreadSheetsPlayerPhrase;
    }

    public GSpreadSheetsQuestDescription getSpreadSheetsQuestDescription() {
        return spreadSheetsQuestDescription;
    }

    public void setSpreadSheetsQuestDescription(GSpreadSheetsQuestDescription spreadSheetsQuestDescription) {
        this.spreadSheetsQuestDescription = spreadSheetsQuestDescription;
    }

    public GSpreadSheetsQuestDirection getSpreadSheetsQuestDirection() {
        return spreadSheetsQuestDirection;
    }

    public void setSpreadSheetsQuestDirection(GSpreadSheetsQuestDirection spreadSheetsQuestDirection) {
        this.spreadSheetsQuestDirection = spreadSheetsQuestDirection;
    }

    public GSpreadSheetsQuestName getSpreadSheetsQuestName() {
        return spreadSheetsQuestName;
    }

    public void setSpreadSheetsQuestName(GSpreadSheetsQuestName spreadSheetsQuestName) {
        this.spreadSheetsQuestName = spreadSheetsQuestName;
    }

    public EsoInterfaceVariable getEsoInterfaceVariable() {
        return esoInterfaceVariable;
    }

    public void setEsoInterfaceVariable(EsoInterfaceVariable esoInterfaceVariable) {
        this.esoInterfaceVariable = esoInterfaceVariable;
    }

    public GSpreadSheetsAchievement getSpreadSheetsAchievement() {
        return spreadSheetsAchievement;
    }

    public void setSpreadSheetsAchievement(GSpreadSheetsAchievement spreadSheetsAchievement) {
        this.spreadSheetsAchievement = spreadSheetsAchievement;
    }

    public GSpreadSheetsAchievementDescription getSpreadSheetsAchievementDescription() {
        return spreadSheetsAchievementDescription;
    }

    public void setSpreadSheetsAchievementDescription(GSpreadSheetsAchievementDescription spreadSheetsAchievementDescription) {
        this.spreadSheetsAchievementDescription = spreadSheetsAchievementDescription;
    }

    public GSpreadSheetsNote getSpreadSheetsNote() {
        return spreadSheetsNote;
    }

    public void setSpreadSheetsNote(GSpreadSheetsNote spreadSheetsNote) {
        this.spreadSheetsNote = spreadSheetsNote;
    }

    public GSpreadSheetsAbilityDescription getSheetsAbilityDescription() {
        return sheetsAbilityDescription;
    }

    public void setSheetsAbilityDescription(GSpreadSheetsAbilityDescription sheetsAbilityDescription) {
        this.sheetsAbilityDescription = sheetsAbilityDescription;
    }

    public GSpreadSheetsCollectible getSheetsCollectible() {
        return sheetsCollectible;
    }

    public void setSheetsCollectible(GSpreadSheetsCollectible sheetsCollectible) {
        this.sheetsCollectible = sheetsCollectible;
    }

    public GSpreadSheetsCollectibleDescription getSheetsCollectibleDescription() {
        return sheetsCollectibleDescription;
    }

    public void setSheetsCollectibleDescription(GSpreadSheetsCollectibleDescription sheetsCollectibleDescription) {
        this.sheetsCollectibleDescription = sheetsCollectibleDescription;
    }

    public GSpreadSheetsLoadscreen getSheetsLoadscreen() {
        return sheetsLoadscreen;
    }

    public void setSheetsLoadscreen(GSpreadSheetsLoadscreen sheetsLoadscreen) {
        this.sheetsLoadscreen = sheetsLoadscreen;
    }

    public BookText getBook() {
        return book;
    }

    public void setBook(BookText book) {
        this.book = book;
    }

    public Book getBookName() {
        return bookName;
    }

    public void setBookName(Book bookName) {
        this.bookName = bookName;
    }

    public SysAccount getCorrectedBy() {
        return correctedBy;
    }

    public void setCorrectedBy(SysAccount correctedBy) {
        this.correctedBy = correctedBy;
    }

    public SysAccount getPreApprovedBy() {
        return preApprovedBy;
    }

    public void setPreApprovedBy(SysAccount preApprovedBy) {
        this.preApprovedBy = preApprovedBy;
    }

    public SysAccount getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(SysAccount rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public GSpreadSheetsQuestStartTip getSpreadSheetsQuestStartTip() {
        return spreadSheetsQuestStartTip;
    }

    public void setSpreadSheetsQuestStartTip(GSpreadSheetsQuestStartTip spreadSheetsQuestStartTip) {
        this.spreadSheetsQuestStartTip = spreadSheetsQuestStartTip;
    }

    public GSpreadSheetsQuestEndTip getSpreadSheetsQuestEndTip() {
        return spreadSheetsQuestEndTip;
    }

    public void setSpreadSheetsQuestEndTip(GSpreadSheetsQuestEndTip spreadSheetsQuestEndTip) {
        this.spreadSheetsQuestEndTip = spreadSheetsQuestEndTip;
    }

}
