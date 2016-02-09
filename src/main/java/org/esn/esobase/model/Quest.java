/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Npc> npcs;
    private BigDecimal progress;

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

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
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
            result+="("+r+")";
        }
        return result;
    }

}
