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
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
public class Location extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(unique = true)
    private String name;
    private String nameRu;
    @OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Npc> npcs;
    @OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Quest> quests;
    private BigDecimal progress;
    @ManyToOne(fetch = FetchType.LAZY)
    private GSpreadSheetsLocationName sheetsLocationName;
    @ManyToOne(fetch = FetchType.EAGER)
    private Location parentLocation;
    @OneToMany(mappedBy = "parentLocation")
    private List<Location> subLocations;

    public Location() {
    }

    public Location(String name, String nameRu) {
        this.name = name;
        this.nameRu = nameRu;
        this.npcs = new ArrayList<>();
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

    public List<Npc> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<Npc> npcs) {
        this.npcs = npcs;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    public GSpreadSheetsLocationName getSheetsLocationName() {
        return sheetsLocationName;
    }

    public void setSheetsLocationName(GSpreadSheetsLocationName sheetsLocationName) {
        this.sheetsLocationName = sheetsLocationName;
    }

    public Location getParentLocation() {
        return parentLocation;
    }

    public void setParentLocation(Location parentLocation) {
        this.parentLocation = parentLocation;
    }

    public List<Location> getSubLocations() {
        return subLocations;
    }

    public void setSubLocations(List<Location> subLocations) {
        this.subLocations = subLocations;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
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
