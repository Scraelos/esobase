/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.esn.esobase.model.BookText;
import org.esn.esobase.model.GSpreadSheetsAbilityDescription;
import org.esn.esobase.model.GSpreadSheetsAchievement;
import org.esn.esobase.model.GSpreadSheetsAchievementDescription;
import org.esn.esobase.model.GSpreadSheetsActivator;
import org.esn.esobase.model.GSpreadSheetsCollectible;
import org.esn.esobase.model.GSpreadSheetsCollectibleDescription;
import org.esn.esobase.model.GSpreadSheetsItemDescription;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsJournalEntry;
import org.esn.esobase.model.GSpreadSheetsLoadscreen;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNote;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestDirection;
import org.esn.esobase.model.GSpreadSheetsQuestEndTip;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.GSpreadSheetsQuestStartTip;
import org.esn.esobase.model.TesDictionary;
import org.esn.esobase.model.TranslatedEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
@Service
public class DictionaryService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<TranslatedEntity> search(String text) {
        List<TranslatedEntity> result = new ArrayList<>();
        List<Class> searchEntities = new ArrayList<>();
        searchEntities.add(GSpreadSheetsNpcName.class);
        searchEntities.add(GSpreadSheetsActivator.class);
        searchEntities.add(GSpreadSheetsLocationName.class);
        searchEntities.add(GSpreadSheetsLoadscreen.class);
        searchEntities.add(GSpreadSheetsNpcPhrase.class);
        searchEntities.add(GSpreadSheetsPlayerPhrase.class);
        searchEntities.add(GSpreadSheetsItemName.class);
        searchEntities.add(GSpreadSheetsItemDescription.class);
        searchEntities.add(GSpreadSheetsQuestName.class);
        searchEntities.add(GSpreadSheetsQuestDescription.class);
        searchEntities.add(GSpreadSheetsJournalEntry.class);
        searchEntities.add(GSpreadSheetsQuestDirection.class);
        searchEntities.add(GSpreadSheetsQuestStartTip.class);
        searchEntities.add(GSpreadSheetsQuestEndTip.class);
        searchEntities.add(GSpreadSheetsAchievement.class);
        searchEntities.add(GSpreadSheetsAchievementDescription.class);
        searchEntities.add(GSpreadSheetsCollectible.class);
        searchEntities.add(GSpreadSheetsCollectibleDescription.class);
        searchEntities.add(GSpreadSheetsAbilityDescription.class);
        searchEntities.add(GSpreadSheetsNote.class);
        searchEntities.add(BookText.class);
        searchEntities.add(TesDictionary.class);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        for (Class sc : searchEntities) {
            CriteriaQuery cq = cb.createQuery(sc);
            Root root = cq.from(sc);
            Predicate where = null;
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.or(cb.like(cb.lower(root.get("textEn")), text.toLowerCase()), cb.like(cb.lower(root.get("textRu")), text.toLowerCase())));
            if (!predicates.isEmpty() && predicates.size() > 1) {
                where = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            } else if (!predicates.isEmpty()) {
                where = predicates.get(0);
            }
            cq.distinct(true);
            cq.where(where);
            List<TranslatedEntity> resultList = em.createQuery(cq).getResultList();
            result.addAll(resultList);
        }

        return result;
    }

    @Transactional
    public void insertDictionaryStrings(List<Object[]> rows) {
        for (Object[] row : rows) {

            Query insertQ = em.createNativeQuery("insert into tesdictionary (id,texten,textru,description,game) values (nextval('hibernate_sequence'),:texten,:textru,:description,:game)");
            insertQ.setParameter("texten", row[0]);
            insertQ.setParameter("textru", row[1]);
            insertQ.setParameter("description", row[2]);
            insertQ.setParameter("game", row[3]);
            insertQ.executeUpdate();

        }
    }

    @Transactional
    public void cleanupDictionaryStrings() {
        Query updateQ = em.createNativeQuery("delete from tesdictionary");
        updateQ.executeUpdate();
    }

}
