/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class Npc extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    private String name;
    private String nameRu;
    @ManyToOne(fetch = FetchType.EAGER)
    private Location location;
    @OneToMany(mappedBy = "npc", cascade = CascadeType.PERSIST)
    private List<Subtitle> subtitles;
    @OneToMany(mappedBy = "npc", cascade = CascadeType.PERSIST)
    private List<Topic> topics;
    @OneToMany(mappedBy = "npc", cascade = CascadeType.PERSIST)
    private List<Greeting> greetings;
    @Enumerated(EnumType.STRING)
    private NPC_SEX sex;
    @ManyToMany(mappedBy = "npcs")
    private List<Quest> quests;
    private BigDecimal progress;
    private Boolean hasNewTranslations;
    @ManyToMany
    private Set<SysAccount> translators;

    public Npc() {
    }

    public Npc(String name, String nameRu, Location location) {
        this.name = name;
        this.nameRu = nameRu;
        this.location = location;
        this.subtitles = new ArrayList<>();
        this.topics = new ArrayList<>();
        this.greetings = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public List<Greeting> getGreetings() {
        return greetings;
    }

    public void setGreetings(List<Greeting> greetings) {
        this.greetings = greetings;
    }

    public NPC_SEX getSex() {
        return sex;
    }

    public void setSex(NPC_SEX sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        String result = null;
        String locationName = "";
        if (location != null) {
            if (location.getNameRu() != null) {
                locationName = location.getNameRu();
            } else if (location.getName() != null) {
                locationName = location.getName();
            }
        }
        if (nameRu == null) {
            result = name;
        } else if (name == null) {
            result = nameRu;
        } else {
            result = name + "/" + nameRu;
        }
        if (sex != null) {
            result += "(" + sex.toString().substring(0, 1) + ")";
        }
        if (progress != null) {
            String r = progress.multiply(BigDecimal.valueOf(100L).setScale(2, RoundingMode.HALF_DOWN)).setScale(0, RoundingMode.HALF_UP).toString() + "%";
            result += "(" + r + ")";
        }
        return result + "(" + locationName + ")";
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }

    public Boolean getHasNewTranslations() {
        return hasNewTranslations;
    }

    public void setHasNewTranslations(Boolean hasNewTranslations) {
        this.hasNewTranslations = hasNewTranslations;
    }

    public Set<SysAccount> getTranslators() {
        return translators;
    }

    public void setTranslators(Set<SysAccount> translators) {
        this.translators = translators;
    }

}
