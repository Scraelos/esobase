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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.esn.esobase.model.lib.DAO;

/**
 *
 * @author scraelos
 */
@Entity
public class QuestItem extends DAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @OneToOne
    private GSpreadSheetsItemName name;
    @ManyToOne
    private GSpreadSheetsItemDescription description;
    @ManyToMany(mappedBy = "items")
    private Set<Quest> quests;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Set<Quest> getQuests() {
        return quests;
    }

    public void setQuests(Set<Quest> quests) {
        this.quests = quests;
    }

    public GSpreadSheetsItemName getName() {
        return name;
    }

    public void setName(GSpreadSheetsItemName name) {
        this.name = name;
    }

    public GSpreadSheetsItemDescription getDescription() {
        return description;
    }

    public void setDescription(GSpreadSheetsItemDescription description) {
        this.description = description;
    }

}
