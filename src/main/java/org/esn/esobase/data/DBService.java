/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.Greeting;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.NPC_SEX;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.SysAccountRole;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.Topic;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
public class DBService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void createRoles() {
        List<SysAccountRole> roles = new ArrayList<>();
        roles.add(new SysAccountRole(1L, "ROLE_USER", "Вход в систему"));
        roles.add(new SysAccountRole(2L, "ROLE_ADMIN", "Администрирование"));
        roles.add(new SysAccountRole(3L, "ROLE_TRANSLATE", "Перевод"));
        roles.add(new SysAccountRole(4L, "ROLE_APPROVE", "Вычитка"));
        roles.add(new SysAccountRole(5L, "ROLE_DIRECT_ACCESS", "Прямое редактирование таблиц"));
        for (SysAccountRole role : roles) {
            SysAccountRole foundRole = em.find(SysAccountRole.class, role.getId());
            if (foundRole == null) {
                em.persist(role);
            }
        }
    }

    @Transactional
    public void createDefaultAdminUser() {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(SysAccount.class);
        crit.add(Restrictions.eq("login", "admin"));
        SysAccount adminAccount = (SysAccount) crit.uniqueResult();
        if (adminAccount == null) {
            String password = "admin";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            SysAccount newAdminAccount = new SysAccount();
            newAdminAccount.setLogin("admin");
            newAdminAccount.setIsBlocked(Boolean.FALSE);
            newAdminAccount.setPassword(hashedPassword);
            Set<SysAccountRole> roles = new HashSet<>();
            roles.add(new SysAccountRole(1L));
            roles.add(new SysAccountRole(2L));
            newAdminAccount.setRoles(roles);
            em.persist(newAdminAccount);
        }

    }

    @Transactional
    public void saveEntity(Object entity) {
        em.merge(entity);
    }

    @Transactional
    public SysAccount getAccount(String login) {
        SysAccount result = null;
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(SysAccount.class);
        crit.add(Restrictions.eq("login", login));
        crit.setFetchMode("roles", FetchMode.JOIN);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        result = (SysAccount) crit.uniqueResult();
        return result;
    }

    @Transactional
    public void fillLocationsAndNpcs() {
        Session session = (Session) em.getDelegate();
        Criteria locCrit = session.createCriteria(Location.class);
        List<Location> locations = locCrit.list();
        for (Location location : locations) {
            Criteria gsLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
            if (location.getName() != null && !location.getName().isEmpty()) {
                gsLocationCrit.add(Restrictions.eq("textEn", location.getName()));
            } else if (location.getNameRu() != null && !location.getNameRu().isEmpty()) {
                gsLocationCrit.add(Restrictions.eq("textRu", location.getNameRu()));
            }
            List<GSpreadSheetsLocationName> list = gsLocationCrit.list();
            for (GSpreadSheetsLocationName loc : list) {
                location.setName(loc.getTextEn());
                location.setNameRu(loc.getTextRu());
            }
            em.merge(location);
        }
        Criteria npcCrit = session.createCriteria(Npc.class);
        List<Npc> npcs = npcCrit.list();
        for (Npc npc : npcs) {
            Criteria gsNpcCrit = session.createCriteria(GSpreadSheetsNpcName.class);
            if (npc.getName() != null && !npc.getName().isEmpty()) {
                gsNpcCrit.add(Restrictions.eq("textEn", npc.getName()));
            } else if (npc.getNameRu() != null && !npc.getNameRu().isEmpty()) {
                gsNpcCrit.add(Restrictions.eq("textRu", npc.getNameRu()));
            }
            List<GSpreadSheetsNpcName> npcList = gsNpcCrit.list();
            for (GSpreadSheetsNpcName gnpc : npcList) {
                if (npc.getSex() == null || npc.getSex() == NPC_SEX.U) {
                    npc.setSex(gnpc.getSex());
                }
                npc.setName(gnpc.getTextEn());
                npc.setNameRu(gnpc.getTextRu());
                em.merge(npc);
            }
        }
    }

    @Transactional
    public void importFromLua(List<Location> locations) {
        Session session = (Session) em.getDelegate();

        for (Location location : locations) {
            Criteria gsLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
            if (location.getName() != null && !location.getName().isEmpty()) {
                gsLocationCrit.add(Restrictions.eq("textEn", location.getName()));
            } else if (location.getNameRu() != null && !location.getNameRu().isEmpty()) {
                gsLocationCrit.add(Restrictions.eq("textRu", location.getNameRu()));
            }
            List<GSpreadSheetsLocationName> list = gsLocationCrit.list();
            for (GSpreadSheetsLocationName loc : list) {
                location.setName(loc.getTextEn());
                location.setNameRu(loc.getTextRu());
                break;
            }
            for (Npc npc : location.getNpcs()) {
                Criteria gsNpcCrit = session.createCriteria(GSpreadSheetsNpcName.class);
                if (npc.getName() != null && !npc.getName().isEmpty()) {
                    gsNpcCrit.add(Restrictions.eq("textEn", npc.getName()));
                } else if (npc.getNameRu() != null && !npc.getNameRu().isEmpty()) {
                    gsNpcCrit.add(Restrictions.eq("textRu", npc.getNameRu()));
                }
                List<GSpreadSheetsNpcName> npcList = gsNpcCrit.list();
                for (GSpreadSheetsNpcName gnpc : npcList) {
                    if ((npc.getSex() == null) || (npc.getSex() == NPC_SEX.U)) {
                        npc.setName(gnpc.getTextEn());
                        npc.setNameRu(gnpc.getTextRu());
                        npc.setSex(gnpc.getSex());
                    }
                    break;
                }
            }
        }

        int persistCounter = 0;
        for (Location location : locations) {
            try {
                Criteria locationCriteria = session.createCriteria(Location.class);
                if (location.getName() != null) {
                    locationCriteria.add(Restrictions.eq("name", location.getName()));

                } else {
                    locationCriteria.add(Restrictions.eq("nameRu", location.getNameRu()));
                }

                Location locationResult = (Location) locationCriteria.uniqueResult();
                if (locationResult == null) {
                    persistCounter++;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Persist location. Total persists: {0}", Integer.toString(persistCounter));

                    em.persist(location);
                } else {
                    location.setId(locationResult.getId());
                    for (Npc npc : location.getNpcs()) {
                        Criteria npcCriteria = session.createCriteria(Npc.class);
                        if (npc.getName() != null) {
                            npcCriteria.add(Restrictions.eq("name", npc.getName()));
                        } else {
                            npcCriteria.add(Restrictions.eq("nameRu", npc.getNameRu()));
                        }
                        npcCriteria.add(Restrictions.eq("location", npc.getLocation()));
                        npcCriteria.setMaxResults(1);
                        Npc npcResult = (Npc) npcCriteria.uniqueResult();
                        if (npcResult == null) {
                            persistCounter++;
                            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Persist npc. Total persists: {0}", Integer.toString(persistCounter));

                            em.persist(npc);
                        } else {
                            npc.setId(npcResult.getId());
                            for (Subtitle subtitle : npc.getSubtitles()) {
                                Criteria subtitleCriteria = session.createCriteria(Subtitle.class);
                                subtitleCriteria.add(Restrictions.eq("npc", npc));
                                subtitleCriteria.setMaxResults(1);
                                if (subtitle.getText() != null) {
                                    subtitleCriteria.add(Restrictions.eq("text", subtitle.getText()));
                                } else {
                                    subtitleCriteria.add(Restrictions.eq("textRu", subtitle.getTextRu()));
                                }
                                Subtitle subtitleResult = (Subtitle) subtitleCriteria.uniqueResult();
                                if (subtitleResult == null) {
                                    persistCounter++;
                                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Persist subtitle. Total persists: {0}", Integer.toString(persistCounter));
                                    if (subtitle.getText() != null) {
                                        subtitle.setExtNpcPhrase(getNpcPharse(subtitle.getText()));
                                    }
                                    em.persist(subtitle);
                                }
                            }
                            for (Greeting greeting : npc.getGreetings()) {
                                Criteria greetingCriteria = session.createCriteria(Greeting.class);
                                greetingCriteria.add(Restrictions.eq("npc", npc));
                                greetingCriteria.setMaxResults(1);
                                if (greeting.getText() != null) {
                                    greetingCriteria.add(Restrictions.eq("text", greeting.getText()));
                                }
                                Greeting greetingResult = (Greeting) greetingCriteria.uniqueResult();
                                if (greetingResult == null) {
                                    persistCounter++;
                                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Persist greeting. Total persists: {0}", Integer.toString(persistCounter));
                                    if (greeting.getText() != null) {
                                        greeting.setExtNpcPhrase(getNpcPharse(greeting.getText()));
                                    }
                                    em.persist(greeting);
                                }
                            }
                            for (Topic topic : npc.getTopics()) {
                                Criteria topicCriteria = session.createCriteria(Topic.class);
                                topicCriteria.add(Restrictions.eq("npc", npc));
                                topicCriteria.setMaxResults(1);
                                if (topic.getPlayerText() != null) {
                                    topicCriteria.add(Restrictions.eq("playerText", topic.getPlayerText()));
                                } else {
                                    topicCriteria.add(Restrictions.eq("playerTextRu", topic.getPlayerTextRu()));
                                }
                                if (topic.getNpcText() != null) {
                                    topicCriteria.add(Restrictions.eq("npcText", topic.getNpcText()));
                                } else {
                                    topicCriteria.add(Restrictions.eq("npcTextRu", topic.getNpcTextRu()));
                                }
                                Topic topicResult = (Topic) topicCriteria.uniqueResult();
                                if (topicResult == null) {
                                    persistCounter++;
                                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Persist topic. Total persists: {0}", Integer.toString(persistCounter));
                                    if (topic.getNpcText() != null) {
                                        topic.setExtNpcPhrase(getNpcPharse(topic.getNpcText()));
                                    }
                                    if (topic.getPlayerText() != null) {
                                        topic.setExtPlayerPhrase(getPlayerPharse(topic.getPlayerText()));
                                    }
                                    em.persist(topic);
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(DBService.class.getName()).log(Level.OFF, null, ex);
            }
        }
        Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Finisted persist actions. Total persists: {0}", Integer.toString(persistCounter));

    }

    @Transactional(readOnly = true)
    public HierarchicalContainer getLocationsTree(HierarchicalContainer hc) {
        if (hc == null) {
            hc = new HierarchicalContainer();
            hc.addContainerProperty("type", String.class, null);
            hc.addContainerProperty("text", String.class, null);
            hc.addContainerProperty("textRaw", String.class, null);
            hc.addContainerProperty("textRu", String.class, null);
            hc.addContainerProperty("textRawRu", String.class, null);

        }
        hc.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Location.class);
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Location> list = crit.list();
        for (Location location : list) {
            Item locationItem = hc.addItem(location);
            locationItem.getItemProperty("type").setValue("Локация");
            locationItem.getItemProperty("text").setValue(location.getName());
            locationItem.getItemProperty("textRu").setValue(location.getNameRu());
            for (Npc npc : location.getNpcs()) {
                Item npcItem = hc.addItem(npc);
                npcItem.getItemProperty("type").setValue("NPC");
                npcItem.getItemProperty("text").setValue(npc.getName());
                npcItem.getItemProperty("textRu").setValue(npc.getNameRu());
                hc.setParent(npc, location);
                for (Greeting greeting : npc.getGreetings()) {
                    Item greetingItem = hc.addItem(greeting);
                    greetingItem.getItemProperty("type").setValue("Приветствие");
                    greetingItem.getItemProperty("text").setValue(greeting.getText());
                    greetingItem.getItemProperty("textRu").setValue(greeting.getTextRu());
                    if (greeting.getExtNpcPhrase() != null) {
                        greetingItem.getItemProperty("textRaw").setValue(greeting.getExtNpcPhrase().getTextEn());
                        greetingItem.getItemProperty("textRawRu").setValue(greeting.getExtNpcPhrase().getTextRu());
                    }
                    hc.setParent(greeting, npc);
                    hc.setChildrenAllowed(greeting, false);
                }
                for (Subtitle subtitle : npc.getSubtitles()) {
                    Item subtitleItem = hc.addItem(subtitle);
                    subtitleItem.getItemProperty("type").setValue("Субтитры");
                    subtitleItem.getItemProperty("text").setValue(subtitle.getText());
                    subtitleItem.getItemProperty("textRu").setValue(subtitle.getTextRu());
                    if (subtitle.getExtNpcPhrase() != null) {
                        subtitleItem.getItemProperty("textRaw").setValue(subtitle.getExtNpcPhrase().getTextEn());
                        subtitleItem.getItemProperty("textRawRu").setValue(subtitle.getExtNpcPhrase().getTextRu());
                    }
                    hc.setParent(subtitle, npc);
                    hc.setChildrenAllowed(subtitle, false);
                }
                for (Topic topic : npc.getTopics()) {
                    Item topicPlayerItem = hc.addItem(topic);
                    topicPlayerItem.getItemProperty("type").setValue("Фраза игрока");
                    topicPlayerItem.getItemProperty("text").setValue(topic.getPlayerText());
                    topicPlayerItem.getItemProperty("textRu").setValue(topic.getPlayerTextRu());
                    if (topic.getExtPlayerPhrase() != null) {
                        topicPlayerItem.getItemProperty("textRaw").setValue(topic.getExtPlayerPhrase().getTextEn());
                        topicPlayerItem.getItemProperty("textRawRu").setValue(topic.getExtPlayerPhrase().getTextRu());
                    }
                    hc.setParent(topic, npc);
                    hc.setChildrenAllowed(topic, false);
                    NpcTopic npcTopic = new NpcTopic(topic);
                    Item topicNpcItem = hc.addItem(npcTopic);
                    topicNpcItem.getItemProperty("type").setValue("Фраза NPC");
                    topicNpcItem.getItemProperty("text").setValue(topic.getNpcText());
                    topicNpcItem.getItemProperty("textRu").setValue(topic.getNpcTextRu());
                    if (topic.getExtNpcPhrase() != null) {
                        topicNpcItem.getItemProperty("textRaw").setValue(topic.getExtNpcPhrase().getTextEn());
                        topicNpcItem.getItemProperty("textRawRu").setValue(topic.getExtNpcPhrase().getTextRu());
                    }
                    hc.setParent(npcTopic, npc);
                    hc.setChildrenAllowed(npcTopic, false);
                }
            }

        }
        return hc;
    }

    public class NpcTopic {

        private final Topic topic;

        public NpcTopic(Topic topic) {
            this.topic = topic;
        }

        public Topic getTopic() {
            return topic;
        }

    }

    @Transactional
    public void loadPlayerPhrasesFromSpreadSheet(List<GSpreadSheetsPlayerPhrase> phrases) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
        Map<String, GSpreadSheetsPlayerPhrase> phrasesMap = new HashMap<>();
        List<GSpreadSheetsPlayerPhrase> allPhrases = crit.list();
        for (GSpreadSheetsPlayerPhrase phrase : allPhrases) {
            phrasesMap.put(phrase.getTextEn(), phrase);
        }
        int total = phrases.size();
        int count = 0;
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "phrase {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsPlayerPhrase result = phrasesMap.get(phrase.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (phrase.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(phrase.getWeight()))) {
                    isMerge = true;
                    result.setWeight(phrase.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for phrase: {0}", phrase.getTextEn());
                }
                if (!result.getRowNum().equals(phrase.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for phrase: {0}", phrase.getTextEn());
                    result.setRowNum(phrase.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            GSpreadSheetsPlayerPhrase result = phrasesMap.get(phrase.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting phrase for rowNum {0}", phrase.getRowNum());
                em.persist(phrase);
            }
        }

        Map<String, GSpreadSheetsPlayerPhrase> spreadSheetPhrasesMap = new HashMap<>();
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            spreadSheetPhrasesMap.put(phrase.getTextEn(), phrase);
        }
        for (GSpreadSheetsPlayerPhrase phrase : allPhrases) {
            GSpreadSheetsPlayerPhrase result = spreadSheetPhrasesMap.get(phrase.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing phrase rownum={0} :{1}", new Object[]{phrase.getRowNum(), phrase.getTextEn()});
                Criteria playerTopicCrit = session.createCriteria(Topic.class);
                playerTopicCrit.add(Restrictions.eq("extPlayerPhrase", phrase));
                List<Topic> topics = playerTopicCrit.list();
                for (Topic t : topics) {
                    t.setExtPlayerPhrase(null);
                    em.merge(t);
                }
                em.remove(phrase);
            }
        }

    }

    @Transactional
    public void loadLocationNamesFromSpreadSheet(List<GSpreadSheetsLocationName> locations) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsLocationName.class);
        Map<String, GSpreadSheetsLocationName> locationsMap = new HashMap<>();
        List<GSpreadSheetsLocationName> allLocations = crit.list();
        for (GSpreadSheetsLocationName location : allLocations) {
            locationsMap.put(location.getTextEn(), location);
        }
        int total = locations.size();
        int count = 0;
        for (GSpreadSheetsLocationName location : locations) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "location {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsLocationName result = locationsMap.get(location.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (location.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(location.getWeight()))) {
                    isMerge = true;
                    result.setWeight(location.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for location: {0}", location.getTextEn());
                }
                if (!result.getRowNum().equals(location.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for location: {0}", location.getTextEn());
                    result.setRowNum(location.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsLocationName location : locations) {
            GSpreadSheetsLocationName result = locationsMap.get(location.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting location for rowNum {0}", location.getRowNum());
                em.persist(location);
            }
        }

        Map<String, GSpreadSheetsLocationName> spreadSheetLocationsMap = new HashMap<>();
        for (GSpreadSheetsLocationName location : locations) {
            spreadSheetLocationsMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsLocationName location : allLocations) {
            GSpreadSheetsLocationName result = spreadSheetLocationsMap.get(location.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing locations rownum={0} :{1}", new Object[]{location.getRowNum(), location.getTextEn()});
                em.remove(location);
            }
        }

    }

    @Transactional
    public void loadNpcNamesFromSpreadSheet(List<GSpreadSheetsNpcName> npcs) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNpcName.class);
        Map<String, GSpreadSheetsNpcName> npcsMap = new HashMap<>();
        List<GSpreadSheetsNpcName> allNpcs = crit.list();
        for (GSpreadSheetsNpcName npc : allNpcs) {
            npcsMap.put(npc.getTextEn().toLowerCase(), npc);
        }
        int total = npcs.size();
        int count = 0;
        for (GSpreadSheetsNpcName npc : npcs) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "npc {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            String textEn = npc.getTextEn();
            switch (npc.getSex()) {
                case F:
                    textEn += "^F";
                    break;
                case M:
                    textEn += "^M";
                    break;
                case N:
                    textEn += "^N";
                    break;
            }
            GSpreadSheetsNpcName result = npcsMap.get(textEn.toLowerCase());
            if (result != null) {
                boolean isMerge = false;
                if (npc.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(npc.getWeight()))) {
                    isMerge = true;
                    result.setWeight(npc.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for npc: {0}", npc.getTextEn());
                }
                if (!result.getRowNum().equals(npc.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for npc: {0}", npc.getTextEn());
                    result.setRowNum(npc.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsNpcName npc : npcs) {
            GSpreadSheetsNpcName result = npcsMap.get(npc.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting npc for rowNum {0}", npc.getRowNum());
                em.persist(npc);
            }
        }

        Map<String, GSpreadSheetsNpcName> spreadSheetNpcsMap = new HashMap<>();
        for (GSpreadSheetsNpcName npc : npcs) {
            spreadSheetNpcsMap.put(npc.getTextEn(), npc);
        }
        for (GSpreadSheetsNpcName npc : allNpcs) {
            GSpreadSheetsNpcName result = spreadSheetNpcsMap.get(npc.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing npc rownum={0} :{1}", new Object[]{npc.getRowNum(), npc.getTextEn()});
                em.remove(npc);
            }
        }

    }

    @Transactional
    public void loadNpcPhrasesFromSpreadSheet(List<GSpreadSheetsNpcPhrase> phrases) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
        Map<String, GSpreadSheetsNpcPhrase> phrasesMap = new HashMap<>();
        List<GSpreadSheetsNpcPhrase> allPhrases = crit.list();
        for (GSpreadSheetsNpcPhrase phrase : allPhrases) {
            phrasesMap.put(phrase.getTextEn(), phrase);
        }
        int total = phrases.size();
        int count = 0;
        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "phrase {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsNpcPhrase result = phrasesMap.get(phrase.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (phrase.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(phrase.getWeight()))) {
                    isMerge = true;
                    result.setWeight(phrase.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for phrase: {0}", phrase.getTextEn());
                }
                if (!result.getRowNum().equals(phrase.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for phrase: {0}", phrase.getTextEn());
                    result.setRowNum(phrase.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }

        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            GSpreadSheetsNpcPhrase result = phrasesMap.get(phrase.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).info("inserting phrase for rowNum " + phrase.getRowNum());
                em.persist(phrase);
            }
        }
        Map<String, GSpreadSheetsNpcPhrase> spreadSheetPhrasesMap = new HashMap<>();
        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            spreadSheetPhrasesMap.put(phrase.getTextEn(), phrase);
        }
        for (GSpreadSheetsNpcPhrase phrase : allPhrases) {
            GSpreadSheetsNpcPhrase result = spreadSheetPhrasesMap.get(phrase.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing phrase rownum={0} :{1}", new Object[]{phrase.getRowNum(), phrase.getTextEn()});
                Criteria npcTopicCrit = session.createCriteria(Topic.class);
                npcTopicCrit.add(Restrictions.eq("extNpcPhrase", phrase));
                List<Topic> topics = npcTopicCrit.list();
                for (Topic t : topics) {
                    t.setExtNpcPhrase(null);
                    em.merge(t);
                }
                Criteria subtitleCrit = session.createCriteria(Subtitle.class);
                subtitleCrit.add(Restrictions.eq("extNpcPhrase", phrase));
                List<Subtitle> subtitles = npcTopicCrit.list();
                for (Subtitle s : subtitles) {
                    s.setExtNpcPhrase(null);
                    em.merge(s);
                }
                Criteria greetingCrit = session.createCriteria(Greeting.class);
                greetingCrit.add(Restrictions.eq("extNpcPhrase", phrase));
                List<Greeting> greetings = greetingCrit.list();
                for (Greeting g : greetings) {
                    g.setExtNpcPhrase(null);
                    em.merge(g);
                }
                em.remove(phrase);
            }
        }

    }

    @Transactional
    public HierarchicalContainer getNpcPhrasesDiff(List<GSpreadSheetsNpcPhrase> phrases, HierarchicalContainer hc) {
        if (hc == null) {
            hc = new HierarchicalContainer();
            hc.addContainerProperty("shText", String.class, null);
            hc.addContainerProperty("shNic", String.class, null);
            hc.addContainerProperty("shDate", Date.class, null);
            hc.addContainerProperty("dbText", String.class, null);
            hc.addContainerProperty("dbNic", String.class, null);
            hc.addContainerProperty("dbDate", Date.class, null);
        }
        hc.removeAllItems();
        List<NpcPhraseDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
        Map<Long, GSpreadSheetsNpcPhrase> phrasesMap = new HashMap<>();
        List<GSpreadSheetsNpcPhrase> allPhrases = crit.list();
        for (GSpreadSheetsNpcPhrase phrase : allPhrases) {
            phrasesMap.put(phrase.getRowNum(), phrase);
        }
        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            GSpreadSheetsNpcPhrase result = phrasesMap.get(phrase.getRowNum());
            if (result != null) {
                if (phrase.getChangeTime() != null && result.getChangeTime() != null && phrase.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new NpcPhraseDiff(phrase, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (phrase.getChangeTime() != null && result.getChangeTime() != null && phrase.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new NpcPhraseDiff(phrase, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && phrase.getChangeTime() == null) {
                    diffs.add(new NpcPhraseDiff(phrase, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && phrase.getChangeTime() != null) {
                    diffs.add(new NpcPhraseDiff(phrase, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && phrase.getChangeTime() == null && (phrase.getTextRu() != null) && (result.getTextRu() != null) && !phrase.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new NpcPhraseDiff(phrase, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (NpcPhraseDiff diff : diffs) {
            Item item = hc.addItem(diff);
            item.getItemProperty("shText").setValue(diff.getSpreadsheetsPhrase().getTextRu());
            item.getItemProperty("shNic").setValue(diff.getSpreadsheetsPhrase().getTranslator());
            item.getItemProperty("shDate").setValue(diff.getSpreadsheetsPhrase().getChangeTime());
            item.getItemProperty("dbText").setValue(diff.getDbPhrase().getTextRu());
            item.getItemProperty("dbNic").setValue(diff.getDbPhrase().getTranslator());
            item.getItemProperty("dbDate").setValue(diff.getDbPhrase().getChangeTime());
            item.getItemProperty("syncType").setValue(diff.getSyncType().toString());
            hc.setChildrenAllowed(item, false);
        }
        return hc;
    }

    @Transactional
    public HierarchicalContainer getNpcnamessDiff(List<GSpreadSheetsNpcName> names, HierarchicalContainer hc) {
        if (hc == null) {
            hc = new HierarchicalContainer();
            hc.addContainerProperty("shText", String.class, null);
            hc.addContainerProperty("shNic", String.class, null);
            hc.addContainerProperty("shDate", Date.class, null);
            hc.addContainerProperty("dbText", String.class, null);
            hc.addContainerProperty("dbNic", String.class, null);
            hc.addContainerProperty("dbDate", Date.class, null);
        }
        hc.removeAllItems();
        List<NpcNameDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNpcName.class);
        Map<Long, GSpreadSheetsNpcName> namesMap = new HashMap<>();
        List<GSpreadSheetsNpcName> allNames = crit.list();
        for (GSpreadSheetsNpcName name : allNames) {
            namesMap.put(name.getRowNum(), name);
        }
        for (GSpreadSheetsNpcName name : names) {
            GSpreadSheetsNpcName result = namesMap.get(name.getRowNum());
            if (result != null) {
                if (name.getChangeTime() != null && result.getChangeTime() != null && name.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new NpcNameDiff(name, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (name.getChangeTime() != null && result.getChangeTime() != null && name.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new NpcNameDiff(name, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && name.getChangeTime() == null) {
                    diffs.add(new NpcNameDiff(name, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && name.getChangeTime() != null) {
                    diffs.add(new NpcNameDiff(name, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && name.getChangeTime() == null && (name.getTextRu() != null) && (result.getTextRu() != null) && !name.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new NpcNameDiff(name, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (NpcNameDiff diff : diffs) {
            Item item = hc.addItem(diff);
            item.getItemProperty("shText").setValue(diff.getSpreadsheetsName().getTextRu());
            item.getItemProperty("shNic").setValue(diff.getSpreadsheetsName().getTranslator());
            item.getItemProperty("shDate").setValue(diff.getSpreadsheetsName().getChangeTime());
            item.getItemProperty("dbText").setValue(diff.getDbName().getTextRu());
            item.getItemProperty("dbNic").setValue(diff.getDbName().getTranslator());
            item.getItemProperty("dbDate").setValue(diff.getDbName().getChangeTime());
            item.getItemProperty("syncType").setValue(diff.getSyncType().toString());
            hc.setChildrenAllowed(item, false);
        }
        return hc;
    }

    @Transactional
    public HierarchicalContainer getLocationNamesDiff(List<GSpreadSheetsLocationName> names, HierarchicalContainer hc) {
        if (hc == null) {
            hc = new HierarchicalContainer();
            hc.addContainerProperty("shText", String.class, null);
            hc.addContainerProperty("shNic", String.class, null);
            hc.addContainerProperty("shDate", Date.class, null);
            hc.addContainerProperty("dbText", String.class, null);
            hc.addContainerProperty("dbNic", String.class, null);
            hc.addContainerProperty("dbDate", Date.class, null);
        }
        hc.removeAllItems();
        List<LocationsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsLocationName.class);
        Map<Long, GSpreadSheetsLocationName> namesMap = new HashMap<>();
        List<GSpreadSheetsLocationName> allNames = crit.list();
        for (GSpreadSheetsLocationName name : allNames) {
            namesMap.put(name.getRowNum(), name);
        }
        for (GSpreadSheetsLocationName name : names) {
            GSpreadSheetsLocationName result = namesMap.get(name.getRowNum());
            if (result != null) {
                if (name.getChangeTime() != null && result.getChangeTime() != null && name.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new LocationsDiff(name, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (name.getChangeTime() != null && result.getChangeTime() != null && name.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new LocationsDiff(name, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && name.getChangeTime() == null) {
                    diffs.add(new LocationsDiff(name, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && name.getChangeTime() != null) {
                    diffs.add(new LocationsDiff(name, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && name.getChangeTime() == null && (name.getTextRu() != null) && (result.getTextRu() != null) && !name.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new LocationsDiff(name, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (LocationsDiff diff : diffs) {
            Item item = hc.addItem(diff);
            item.getItemProperty("shText").setValue(diff.getSpreadsheetsName().getTextRu());
            item.getItemProperty("shNic").setValue(diff.getSpreadsheetsName().getTranslator());
            item.getItemProperty("shDate").setValue(diff.getSpreadsheetsName().getChangeTime());
            item.getItemProperty("dbText").setValue(diff.getDbName().getTextRu());
            item.getItemProperty("dbNic").setValue(diff.getDbName().getTranslator());
            item.getItemProperty("dbDate").setValue(diff.getDbName().getChangeTime());
            item.getItemProperty("syncType").setValue(diff.getSyncType().toString());
            hc.setChildrenAllowed(item, false);
        }
        return hc;
    }

    @Transactional
    public HierarchicalContainer getPlayerPhrasesDiff(List<GSpreadSheetsPlayerPhrase> phrases, HierarchicalContainer hc) {
        hc.removeAllItems();
        List<PlayerPhraseDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
        Map<Long, GSpreadSheetsPlayerPhrase> phrasesMap = new HashMap<>();
        List<GSpreadSheetsPlayerPhrase> allPhrases = crit.list();
        for (GSpreadSheetsPlayerPhrase phrase : allPhrases) {
            phrasesMap.put(phrase.getRowNum(), phrase);
        }
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            GSpreadSheetsPlayerPhrase result = phrasesMap.get(phrase.getRowNum());
            if (result != null) {
                if (phrase.getChangeTime() != null && result.getChangeTime() != null && phrase.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new PlayerPhraseDiff(phrase, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (phrase.getChangeTime() != null && result.getChangeTime() != null && phrase.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new PlayerPhraseDiff(phrase, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && phrase.getChangeTime() == null) {
                    diffs.add(new PlayerPhraseDiff(phrase, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && phrase.getChangeTime() != null) {
                    diffs.add(new PlayerPhraseDiff(phrase, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && phrase.getChangeTime() == null && (phrase.getTextRu() != null) && (result.getTextRu() != null) && !phrase.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new PlayerPhraseDiff(phrase, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (PlayerPhraseDiff diff : diffs) {
            Item item = hc.addItem(diff);
            item.getItemProperty("shText").setValue(diff.getSpreadsheetsPhrase().getTextRu());
            item.getItemProperty("shNic").setValue(diff.getSpreadsheetsPhrase().getTranslator());
            item.getItemProperty("shDate").setValue(diff.getSpreadsheetsPhrase().getChangeTime());
            item.getItemProperty("dbText").setValue(diff.getDbPhrase().getTextRu());
            item.getItemProperty("dbNic").setValue(diff.getDbPhrase().getTranslator());
            item.getItemProperty("dbDate").setValue(diff.getDbPhrase().getChangeTime());
            item.getItemProperty("syncType").setValue(diff.getSyncType().toString());
            hc.setChildrenAllowed(item, false);
        }
        return hc;
    }

    @Transactional
    public void assignTopicToPhrase(Topic topic) {
        Session session = (Session) em.getDelegate();
        GSpreadSheetsPlayerPhrase playerPhrase = null;
        if (topic.getPlayerText() != null && !topic.getPlayerText().isEmpty()) {
            Criteria exactPlayerPhraseCrit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
            exactPlayerPhraseCrit.add(Restrictions.eq("textEn", topic.getPlayerText().replace("\n", "$")));
            exactPlayerPhraseCrit.setMaxResults(1);
            playerPhrase = (GSpreadSheetsPlayerPhrase) exactPlayerPhraseCrit.uniqueResult();
        }
        if (playerPhrase == null && topic.getPlayerTextRu() != null && !topic.getPlayerTextRu().isEmpty()) {
            Criteria exactPlayerPhraseCrit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
            String enText = topic.getPlayerTextRu().replace("Óàeæäeîèe ", "").replace("Óâpoça ", "");
            exactPlayerPhraseCrit.add(Restrictions.or(Restrictions.eq("textRu", topic.getPlayerTextRu().replace("\n", "$")), Restrictions.eq("textEn", enText.replace("\n", "$")), Restrictions.eq("textRu", enText.replace("\n", "$"))));
            exactPlayerPhraseCrit.add(Restrictions.eq("textRu", topic.getPlayerTextRu().replace("\n", "$")));
            exactPlayerPhraseCrit.setMaxResults(1);
            playerPhrase = (GSpreadSheetsPlayerPhrase) exactPlayerPhraseCrit.uniqueResult();
        }
        if (playerPhrase == null && ((topic.getPlayerText() != null && !topic.getPlayerText().isEmpty()) || (topic.getPlayerTextRu() != null && !topic.getPlayerTextRu().isEmpty()))) {
            List<GSpreadSheetsPlayerPhrase> foundPhrases = new ArrayList<>();
            Criteria crit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
            List<GSpreadSheetsPlayerPhrase> list = crit.list();
            for (GSpreadSheetsPlayerPhrase phrase : list) {
                if (topic.getPlayerText() != null && !topic.getPlayerText().isEmpty()) {
                    if (getMatch(phrase.getTextEn(), topic.getPlayerText())) {
                        foundPhrases.add(phrase);
                    }
                }
                if (topic.getPlayerTextRu() != null && !topic.getPlayerTextRu().isEmpty()) {
                    if (getMatch(phrase.getTextRu(), topic.getPlayerTextRu())) {
                        foundPhrases.add(phrase);
                    }
                }

            }
            for (GSpreadSheetsPlayerPhrase foundPhrase : foundPhrases) {
                if (playerPhrase == null) {
                    playerPhrase = foundPhrase;
                } else if (foundPhrase.getTextEn().length() > playerPhrase.getTextEn().length()) {
                    playerPhrase = foundPhrase;
                }
            }
        }
        if (playerPhrase != null) {
            topic.setExtPlayerPhrase(playerPhrase);
            em.merge(topic);
        }

        GSpreadSheetsNpcPhrase npcPhrase = null;
        if (topic.getNpcText() != null && !topic.getNpcText().isEmpty()) {
            Criteria exactNpcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            exactNpcPhraseCrit.add(Restrictions.eq("textEn", topic.getNpcText().replace("\n", "$")));
            exactNpcPhraseCrit.setMaxResults(1);
            npcPhrase = (GSpreadSheetsNpcPhrase) exactNpcPhraseCrit.uniqueResult();
        }
        if (npcPhrase == null && topic.getNpcTextRu() != null && !topic.getNpcTextRu().isEmpty()) {
            Criteria exactNpcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            exactNpcPhraseCrit.add(Restrictions.eq("textRu", topic.getNpcTextRu().replace("\n", "$")));
            exactNpcPhraseCrit.setMaxResults(1);
            npcPhrase = (GSpreadSheetsNpcPhrase) exactNpcPhraseCrit.uniqueResult();
        }
        if (npcPhrase == null && ((topic.getNpcText() != null && !topic.getNpcText().isEmpty()) || (topic.getNpcTextRu() != null && !topic.getNpcTextRu().isEmpty()))) {
            Criteria npcCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            List<GSpreadSheetsNpcPhrase> foundPhrases = new ArrayList<>();
            List<GSpreadSheetsNpcPhrase> npcList = npcCrit.list();
            for (GSpreadSheetsNpcPhrase phrase : npcList) {
                if (topic.getNpcText() != null && !topic.getNpcText().isEmpty()) {
                    if (getMatch(phrase.getTextEn(), topic.getNpcText())) {
                        foundPhrases.add(phrase);
                    }
                }
                if (topic.getNpcTextRu() != null && !topic.getNpcTextRu().isEmpty()) {
                    if (getMatch(phrase.getTextRu(), topic.getNpcTextRu())) {
                        foundPhrases.add(phrase);
                    }
                }

            }
            for (GSpreadSheetsNpcPhrase foundPhrase : foundPhrases) {
                if (npcPhrase == null) {
                    npcPhrase = foundPhrase;
                } else if (foundPhrase.getTextEn().length() > npcPhrase.getTextEn().length()) {
                    npcPhrase = foundPhrase;
                }
            }
        }

        if (npcPhrase != null) {
            topic.setExtNpcPhrase(npcPhrase);
            em.merge(topic);
        }

    }

    @Transactional
    public void assignToSpreadSheetPhrases() {
        int total = 0;
        int counter = 0;
        int foundCounter = 0;
        Session session = (Session) em.getDelegate();
        Criteria subtitleCrit = session.createCriteria(Subtitle.class);
        //subtitleCrit.setFirstResult(0);
        //subtitleCrit.setMaxResults(100);
        subtitleCrit.add(Restrictions.isNotNull("text"));
        subtitleCrit.add(Restrictions.isNull("extNpcPhrase"));
        List<Subtitle> subtitleList = subtitleCrit.list();
        total = subtitleList.size();
        counter = 0;
        foundCounter = 0;
        for (Subtitle s : subtitleList) {
            counter++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "subtitle {0}/{1}", new Object[]{counter, total});
            if (s.getText() != null && !s.getText().isEmpty()) {
                GSpreadSheetsNpcPhrase phrase = getNpcPharse(s.getText());
                if (phrase != null) {
                    foundCounter++;
                    s.setExtNpcPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}", new Object[]{Integer.toString(counter), total, foundCounter});
                    em.merge(s);
                } else {
                    assignSubtitleToPhrase(s);
                }
            }
        }

        Criteria greetingCrit = session.createCriteria(Greeting.class);
        greetingCrit.add(Restrictions.isNull("extNpcPhrase"));
        //greetingCrit.setFirstResult(0);
        //greetingCrit.setMaxResults(100);
        greetingCrit.add(Restrictions.isNotNull("text"));
        List<Greeting> greetingList = greetingCrit.list();
        total = greetingList.size();
        counter = 0;
        foundCounter = 0;
        for (Greeting g : greetingList) {
            counter++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "greeting {0}/{1}", new Object[]{counter, total});
            if (g.getText() != null && !g.getText().isEmpty()) {
                GSpreadSheetsNpcPhrase phrase = getNpcPharse(g.getText());
                if (phrase != null) {
                    foundCounter++;
                    g.setExtNpcPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}", new Object[]{Integer.toString(counter), total, foundCounter});
                    em.merge(g);
                } else {
                    assignGreetingToPhrase(g);
                }
            }
        }

        Criteria npcTopicCrit = session.createCriteria(Topic.class);
        npcTopicCrit.add(Restrictions.isNull("extNpcPhrase"));
        //npcTopicCrit.setFirstResult(0);
        //npcTopicCrit.setMaxResults(100);
        npcTopicCrit.add(Restrictions.isNotNull("npcText"));
        npcTopicCrit.add(Restrictions.not(Restrictions.eq("npcText", "")));
        List<Topic> npcTopicList = npcTopicCrit.list();
        total = npcTopicList.size();
        counter = 0;
        foundCounter = 0;
        for (Topic t : npcTopicList) {
            counter++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "npc topic {0}/{1}", new Object[]{counter, total});
            if (t.getNpcText() != null && !t.getNpcText().isEmpty()) {
                GSpreadSheetsNpcPhrase phrase = getNpcPharse(t.getNpcText());
                if (phrase != null) {
                    foundCounter++;
                    t.setExtNpcPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}", new Object[]{Integer.toString(counter), total, foundCounter});
                    em.merge(t);
                } else {
                    assignTopicToPhrase(t);
                }
            }
        }

        Criteria playerTopicCrit = session.createCriteria(Topic.class);
        playerTopicCrit.add(Restrictions.isNull("extPlayerPhrase"));
        //playerTopicCrit.setFirstResult(0);
        //playerTopicCrit.setMaxResults(100);
        playerTopicCrit.add(Restrictions.isNotNull("playerText"));
        List<Topic> playerTopicList = playerTopicCrit.list();
        total = playerTopicList.size();
        counter = 0;
        foundCounter = 0;
        for (Topic t : playerTopicList) {
            counter++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "player topic {0}/{1}", new Object[]{counter, total});
            if (t.getPlayerText() != null && !t.getPlayerText().isEmpty()) {
                GSpreadSheetsPlayerPhrase phrase = getPlayerPharse(t.getPlayerText());
                if (phrase != null) {
                    foundCounter++;
                    t.setExtPlayerPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}", new Object[]{Integer.toString(counter), total, foundCounter});
                    em.merge(t);
                } else {
                    assignTopicToPhrase(t);
                }
            }
        }

    }

    private GSpreadSheetsNpcPhrase getNpcPharse(String text) {
        GSpreadSheetsNpcPhrase result = null;
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
        crit.add(Restrictions.eq("textEn", text.replace("\n", "$")));
        List<GSpreadSheetsNpcPhrase> list = crit.list();
        for (GSpreadSheetsNpcPhrase p : list) {
            result = p;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "exact match");
            break;
        }
        if (result == null) {
            String regexp = text.replace("\n", "$").replace(")", "\\)").replace("(", "\\(").replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]").replace("*", "\\*").replace(".", "\\.").replace("?", "\\?").replace(":", "\\:").replace("!", "\\!");
            regexp = regexp.replaceAll("([\\wÀÁÂÄÆÇÈÉÊÌÍÎÏÒÓÑŒÙÚÅŸÖËÔÜàáâäæçèéêìíîïòóñœùúûåÿöëôü\\-\\']+)", "($1|<<.*>>)");
            regexp = "\\A" + regexp + "\\Z";
            Query q = em.createNativeQuery("select id,texten from gspreadsheetsnpcphrase where texten ~ :regexp");
            q.setParameter("regexp", regexp);
            List<Object[]> resultList = q.getResultList();
            for (Object[] row : resultList) {
                result = new GSpreadSheetsNpcPhrase();
                result.setId(((BigInteger) row[0]).longValue());
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "successfull match {0} for {1}", new Object[]{row[1], text});
                break;
            }
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "nothing found with " + regexp);
            }
        }

        return result;
    }

    private GSpreadSheetsPlayerPhrase getPlayerPharse(String text) {
        GSpreadSheetsPlayerPhrase result = null;
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
        crit.add(Restrictions.eq("textEn", text.replace("\n", "$")));
        List<GSpreadSheetsPlayerPhrase> list = crit.list();
        for (GSpreadSheetsPlayerPhrase p : list) {
            result = p;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "exact match");
            break;
        }
        if (result == null) {
            String regexp = text.replaceAll("^Persuade", "[Persuade]").replaceAll("^Intimidate", "[Intimidate]").replaceAll("^Lie", "[Lie]").replace("\n", "$").replace(")", "\\)").replace("(", "\\(").replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]").replace("*", "\\*").replace(".", "\\.").replace("?", "\\?").replace(":", "\\:").replace("!", "\\!");
            regexp = regexp.replaceAll("([\\wÀÁÂÄÆÇÈÉÊÌÍÎÏÒÓÑŒÙÚÅŸÖËÔÜàáâäæçèéêìíîïòóñœùúûåÿöëôü\\-\\']+)", "($1|<<.*>>)");
            regexp = "\\A" + regexp + "\\Z";
            Query q = em.createNativeQuery("select id,texten from gspreadsheetsplayerphrase where texten ~ :regexp");
            q.setParameter("regexp", regexp);
            List<Object[]> resultList = q.getResultList();
            for (Object[] row : resultList) {
                result = new GSpreadSheetsPlayerPhrase();
                result.setId(((BigInteger) row[0]).longValue());
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "successfull match {0} for {1}", new Object[]{row[1], text});
                break;
            }
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "nothing found with " + regexp);
            }
        }

        return result;
    }

    @Transactional
    public void assignSubtitleToPhrase(Subtitle subtitle) {
        Session session = (Session) em.getDelegate();
        GSpreadSheetsNpcPhrase npcPhrase = null;
        if (subtitle.getText() != null && !subtitle.getText().isEmpty()) {
            Criteria exactNpcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            exactNpcPhraseCrit.add(Restrictions.eq("textEn", subtitle.getText().replace("\n", "$")));
            exactNpcPhraseCrit.setMaxResults(1);
            npcPhrase = (GSpreadSheetsNpcPhrase) exactNpcPhraseCrit.uniqueResult();
        }
        if (npcPhrase == null && subtitle.getTextRu() != null && !subtitle.getTextRu().isEmpty()) {
            Criteria exactNpcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            exactNpcPhraseCrit.add(Restrictions.eq("textRu", subtitle.getTextRu().replace("\n", "$")));
            exactNpcPhraseCrit.setMaxResults(1);
            npcPhrase = (GSpreadSheetsNpcPhrase) exactNpcPhraseCrit.uniqueResult();
        }
        if (npcPhrase == null) {
            List<GSpreadSheetsNpcPhrase> foundPhrases = new ArrayList<>();
            Criteria npcCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            List<GSpreadSheetsNpcPhrase> npcList = npcCrit.list();
            for (GSpreadSheetsNpcPhrase phrase : npcList) {
                if (subtitle.getText() != null && !subtitle.getText().isEmpty()) {
                    if (getMatch(phrase.getTextEn(), subtitle.getText())) {
                        foundPhrases.add(phrase);
                    }
                }
                if (npcPhrase == null && subtitle.getTextRu() != null && !subtitle.getTextRu().isEmpty()) {
                    if (getMatch(phrase.getTextRu(), subtitle.getTextRu())) {
                        foundPhrases.add(phrase);
                    }
                }
            }
            for (GSpreadSheetsNpcPhrase foundPhrase : foundPhrases) {
                if (npcPhrase == null) {
                    npcPhrase = foundPhrase;
                } else if (foundPhrase.getTextEn().length() > npcPhrase.getTextEn().length()) {
                    npcPhrase = foundPhrase;
                }
            }
        }
        if (npcPhrase != null) {
            subtitle.setExtNpcPhrase(npcPhrase);
            em.merge(subtitle);
        }

    }

    @Transactional
    public void assignGreetingToPhrase(Greeting greeting) {
        Session session = (Session) em.getDelegate();
        GSpreadSheetsNpcPhrase npcPhrase = null;
        if (greeting.getText() != null && !greeting.getText().isEmpty()) {
            Criteria exactNpcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            exactNpcPhraseCrit.add(Restrictions.eq("textEn", greeting.getText().replace("\n", "$")));
            exactNpcPhraseCrit.setMaxResults(1);
            npcPhrase = (GSpreadSheetsNpcPhrase) exactNpcPhraseCrit.uniqueResult();
        }
        if (npcPhrase == null && greeting.getTextRu() != null && !greeting.getTextRu().isEmpty()) {
            Criteria exactNpcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            exactNpcPhraseCrit.add(Restrictions.eq("textRu", greeting.getTextRu().replace("\n", "$")));
            exactNpcPhraseCrit.setMaxResults(1);
            npcPhrase = (GSpreadSheetsNpcPhrase) exactNpcPhraseCrit.uniqueResult();
        }
        if (npcPhrase == null) {
            List<GSpreadSheetsNpcPhrase> foundPhrases = new ArrayList<>();
            Criteria npcCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            List<GSpreadSheetsNpcPhrase> npcList = npcCrit.list();
            for (GSpreadSheetsNpcPhrase phrase : npcList) {
                if (greeting.getText() != null && !greeting.getText().isEmpty()) {
                    if (getMatch(phrase.getTextEn(), greeting.getText())) {
                        foundPhrases.add(phrase);
                    }
                }
                if (npcPhrase == null && greeting.getTextRu() != null && !greeting.getTextRu().isEmpty()) {
                    if (getMatch(phrase.getTextRu(), greeting.getTextRu())) {
                        foundPhrases.add(phrase);
                    }
                }
            }
            for (GSpreadSheetsNpcPhrase foundPhrase : foundPhrases) {
                if (npcPhrase == null) {
                    npcPhrase = foundPhrase;
                } else if (foundPhrase.getTextEn().length() > npcPhrase.getTextEn().length()) {
                    npcPhrase = foundPhrase;
                }
            }
        }
        if (npcPhrase != null) {
            greeting.setExtNpcPhrase(npcPhrase);
            em.merge(greeting);
        }
    }

    private boolean getMatch(String string1, String string2) {
        if (string1.equals(string2.replace("\n", "$"))) {
            return true;
        }
        //String patternString = string1.replace("$", "\n").replace("[", "\\[").replace("]", "\\]").replace("*", "\\*").replace(".", "\\.").replace("?", "\\?").replaceAll("<<[а-яА-ЯёЁ\\w\\s\\d\\)\\(\\{\\}\\/]*>>", ".*");
        String patternString = string1.replace("$", "\n").replace(")", "\\)").replace("(", "\\(").replace("{", "\\{").replace("}", "\\}").replace("[", "\\[").replace("]", "\\]").replace("*", "\\*").replace(".", "\\.").replace("?", "\\?").replace(":", "\\:").replace("!", "\\!").replaceAll("<<[^<>]*>>", ".*");
        patternString = "\\A" + patternString + "\\Z";
        if (!patternString.equals("\\A.*\\Z") && !patternString.equals("\\A.*?\\Z") && !patternString.equals("\\A.*\\.\\Z") && !patternString.equals("\\A.*\\!\\Z") && !patternString.equals("\\A|cff6600WARNING RANGE\\Z") && !patternString.equals("\\A|cff0000MAX RANGE\\Z") && !patternString.equals("\\A|cffe600ENTER RANGE\\Z") && !patternString.equals("\\A.*\\?\\Z") && !patternString.equals("\\A.*\\!\\Z")) {
            /*Logger.getLogger(DBService.class.getName()).log(Level.INFO, "phrase {0}", phrase.getRowNum());
             Logger.getLogger(DBService.class.getName()).log(Level.INFO, "source is {0}", topic.getPlayerTextRu());
             Logger.getLogger(DBService.class.getName()).log(Level.INFO, "pattern is {0}", patternString);*/
            Pattern pattern = Pattern.compile(patternString);

            Matcher matcher = pattern.matcher(string2);
            if (matcher.find()) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Successful match: " + patternString + " " + string1 + " " + string2);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public BeanItemContainer<Topic> getNpcTopics(Npc npc, BeanItemContainer<Topic> container, boolean withNewTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Topic.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extPlayerPhrase", FetchMode.JOIN);
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("playerTranslations", FetchMode.SELECT);
        crit.setFetchMode("npcTranslations", FetchMode.SELECT);
        if (withNewTranslations) {
            crit.createAlias("playerTranslations", "playerTranslations", JoinType.LEFT_OUTER_JOIN);
            crit.createAlias("npcTranslations", "npcTranslations", JoinType.LEFT_OUTER_JOIN);
            crit.add(Restrictions.or(
                    Restrictions.eq("playerTranslations.status", TRANSLATE_STATUS.NEW),
                    Restrictions.eq("npcTranslations.status", TRANSLATE_STATUS.NEW)
            )
            );
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Topic> list = crit.list();
        container.addAll(list);
        return container;
    }

    @Transactional
    public BeanItemContainer<Greeting> getNpcGreetings(Npc npc, BeanItemContainer<Greeting> container, boolean withNewTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Greeting.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("translations", FetchMode.SELECT);
        if (withNewTranslations) {
            crit.add(Restrictions.sizeGt("translations", 0));
            crit.createAlias("translations", "translations");
            crit.add(Restrictions.eq("translations.status", TRANSLATE_STATUS.NEW));
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Greeting> list = crit.list();

        container.addAll(list);
        return container;
    }

    @Transactional
    public BeanItemContainer<Subtitle> getNpcSubtitles(Npc npc, BeanItemContainer<Subtitle> container, boolean withNewTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Subtitle.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("translations", FetchMode.SELECT);
        if (withNewTranslations) {
            crit.add(Restrictions.sizeGt("translations", 0));
            crit.createAlias("translations", "translations");
            crit.add(Restrictions.eq("translations.status", TRANSLATE_STATUS.NEW));
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Subtitle> list = crit.list();
        container.addAll(list);
        return container;
    }

    @Transactional
    public BeanItemContainer<Npc> getNpcs(BeanItemContainer<Npc> container, boolean withNewTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();

        /*if (withNewTranslations) {
         List<Npc> npcWithTranslations = new ArrayList<>();
         Criteria translationsCrit = session.createCriteria(TranslatedText.class);
         translationsCrit.add(Restrictions.eq("status", TRANSLATE_STATUS.NEW));
         List<TranslatedText> translationsList = translationsCrit.list();
         for (TranslatedText translatedText : translationsList) {
         if (translatedText.getGreeting() != null) {
         npcWithTranslations.add(translatedText.getGreeting().getNpc());
         } else if (translatedText.getSubtitle() != null) {
         npcWithTranslations.add(translatedText.getSubtitle().getNpc());
         } else if (translatedText.getNpcTopic() != null) {
         npcWithTranslations.add(translatedText.getNpcTopic().getNpc());
         } else if (translatedText.getPlayerTopic() != null) {
         npcWithTranslations.add(translatedText.getPlayerTopic().getNpc());
         }
         }
         container.addAll(npcWithTranslations);
         } else {*/
        Criteria crit = session.createCriteria(Npc.class);
        crit.setFetchMode("location", FetchMode.JOIN);
        if (withNewTranslations) {
            crit.add(Restrictions.eq("hasNewTranslations", true));
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Npc> list = crit.list();
        container.addAll(list);
        //}

        return container;
    }

    @Transactional
    public BeanItemContainer loadBeanItems(BeanItemContainer container) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(container.getBeanType());
        container.addAll(crit.list());
        return container;
    }

    @Transactional
    public void saveEntity(DAO entity) {
        if (entity.getId() != null) {
            em.merge(entity);
        } else {
            em.persist(entity);
        }
    }

    @Transactional
    public void saveTranslatedTextDirty(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.DIRTY);
        if (entity.getId() == null) {
            em.persist(entity);
        } else if (entity.getText() == null || entity.getText().isEmpty()) {
            em.remove(em.find(TranslatedText.class, entity.getId()));
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    public void saveTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.NEW);
        if (entity.getId() == null) {
            em.persist(entity);
        } else if (entity.getText() == null || entity.getText().isEmpty()) {
            em.remove(em.find(TranslatedText.class, entity.getId()));
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    public void savePlayerPhrases(List<GSpreadSheetsPlayerPhrase> phrases) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            Criteria crit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
            crit.add(Restrictions.eq("rowNum", phrase.getRowNum()));
            GSpreadSheetsPlayerPhrase result = (GSpreadSheetsPlayerPhrase) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(phrase.getChangeTime());
                result.setTextRu(phrase.getTextRu());
                result.setTranslator(phrase.getTranslator());
                em.merge(result);
            } else {
                em.persist(phrase);
            }
        }
    }

    @Transactional
    public void saveNpcPhrases(List<GSpreadSheetsNpcPhrase> phrases) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            Criteria crit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
            crit.add(Restrictions.eq("rowNum", phrase.getRowNum()));
            GSpreadSheetsNpcPhrase result = (GSpreadSheetsNpcPhrase) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(phrase.getChangeTime());
                result.setTextRu(phrase.getTextRu());
                result.setTranslator(phrase.getTranslator());
                em.merge(result);
            } else {
                em.persist(phrase);
            }
        }
    }

    @Transactional
    public void saveNpcnames(List<GSpreadSheetsNpcName> names) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsNpcName name : names) {
            Criteria crit = session.createCriteria(GSpreadSheetsNpcName.class);
            crit.add(Restrictions.eq("rowNum", name.getRowNum()));
            GSpreadSheetsNpcName result = (GSpreadSheetsNpcName) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(name.getChangeTime());
                result.setTextRu(name.getTextRu());
                result.setTranslator(name.getTranslator());
                em.merge(result);
            } else {
                em.persist(name);
            }
            Criteria npcNameCrit = session.createCriteria(Npc.class);
            npcNameCrit.add(Restrictions.eq("name", name.getTextEn()));
            List<Npc> npcs = npcNameCrit.list();
            for (Npc npc : npcs) {
                if (npc.getSex() != null && npc.getSex() != NPC_SEX.N && name.getSex() != null && name.getSex() != NPC_SEX.N) {
                    npc.setNameRu(name.getTextRu());
                    npc.setSex(name.getSex());
                    em.merge(npc);
                }
                if (npc.getSex() == null || npc.getSex() == NPC_SEX.N) {
                    npc.setNameRu(name.getTextRu());
                    npc.setSex(name.getSex());
                    em.merge(npc);
                }
            }
        }
    }

    @Transactional
    public void saveLocationNames(List<GSpreadSheetsLocationName> names) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsLocationName name : names) {
            Criteria crit = session.createCriteria(GSpreadSheetsLocationName.class);
            crit.add(Restrictions.eq("rowNum", name.getRowNum()));
            GSpreadSheetsLocationName result = (GSpreadSheetsLocationName) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(name.getChangeTime());
                result.setTextRu(name.getTextRu());
                result.setTranslator(name.getTranslator());
                em.merge(result);
            } else {
                em.persist(name);
            }
        }
    }

    @Transactional
    public void rejectTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.REJECTED);
        em.merge(entity);
    }

    @Transactional
    public void acceptTranslatedText(TranslatedText entity) {

        if (entity.getGreeting() != null) {
            Greeting greeting = em.find(Greeting.class, entity.getGreeting().getId());
            GSpreadSheetsNpcPhrase npcPhrase = greeting.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText().replace("\n", "$"));
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                em.merge(entity);
            }

        }
        if (entity.getSubtitle() != null) {
            Subtitle subtitle = em.find(Subtitle.class, entity.getSubtitle().getId());
            GSpreadSheetsNpcPhrase npcPhrase = subtitle.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText().replace("\n", "$"));
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                em.merge(entity);
            }

        }
        if (entity.getNpcTopic() != null) {
            Topic topic = em.find(Topic.class, entity.getNpcTopic().getId());
            GSpreadSheetsNpcPhrase npcPhrase = topic.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText().replace("\n", "$"));
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                em.merge(entity);
            }

        }
        if (entity.getPlayerTopic() != null) {
            Topic topic = em.find(Topic.class, entity.getPlayerTopic().getId());
            GSpreadSheetsPlayerPhrase playerPhrase = topic.getExtPlayerPhrase();
            if (playerPhrase != null) {
                playerPhrase.setTextRu(entity.getText().replace("\n", "$"));
                playerPhrase.setTranslator(entity.getAuthor().getLogin());
                playerPhrase.setChangeTime(new Date());
                em.merge(playerPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                em.merge(entity);
            }

        }
    }

    @Transactional
    public void gatherQuestStatistics() {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Quest.class);
        List<Quest> list = crit.list();
        for (Quest q : list) {
            int totalPhases = 0;
            int translatedPhrases = 0;
            for (Npc npc : q.getNpcs()) {
                int npcTotalPhases = 0;
                int npcTranslatedPhrases = 0;
                for (Topic t : npc.getTopics()) {
                    GSpreadSheetsNpcPhrase extNpcPhrase = t.getExtNpcPhrase();
                    if (extNpcPhrase != null) {
                        totalPhases++;
                        npcTotalPhases++;
                        if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                            npcTranslatedPhrases++;
                        }
                    }
                    GSpreadSheetsPlayerPhrase extPlayerPhrase = t.getExtPlayerPhrase();
                    if (extPlayerPhrase != null) {
                        totalPhases++;
                        npcTotalPhases++;
                        if (extPlayerPhrase.getTranslator() != null && !extPlayerPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                            npcTranslatedPhrases++;
                        }
                    }
                }
                for (Subtitle s : npc.getSubtitles()) {
                    GSpreadSheetsNpcPhrase extNpcPhrase = s.getExtNpcPhrase();
                    if (extNpcPhrase != null) {
                        totalPhases++;
                        npcTotalPhases++;
                        if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                            npcTranslatedPhrases++;
                        }
                    }
                }
                for (Greeting g : npc.getGreetings()) {
                    GSpreadSheetsNpcPhrase extNpcPhrase = g.getExtNpcPhrase();
                    if (extNpcPhrase != null) {
                        totalPhases++;
                        npcTotalPhases++;
                        if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                            npcTranslatedPhrases++;
                        }
                    }
                }
                float npcR = 0;
                if (npcTotalPhases > 0) {
                    npcR = (float) npcTranslatedPhrases / npcTotalPhases;

                }
                if (npcR > 0) {
                    npc.setProgress(new BigDecimal(npcR).setScale(2, RoundingMode.UP));
                } else {
                    npc.setProgress(BigDecimal.ZERO);
                }
                em.merge(npc);
            }
            float r = 0;
            if (totalPhases > 0) {
                r = (float) translatedPhrases / totalPhases;

            }
            if (r > 0) {
                q.setProgress(new BigDecimal(r).setScale(2, RoundingMode.UP));
            } else {
                q.setProgress(BigDecimal.ZERO);
            }

            em.merge(q);
        }

        Query updateNpcQuery = em.createQuery("update Npc set hasNewTranslations=false");
        updateNpcQuery.executeUpdate();
        Criteria npcCrit = session.createCriteria(TranslatedText.class);
        npcCrit.add(Restrictions.eq("status", TRANSLATE_STATUS.NEW));
        List<TranslatedText> list1 = npcCrit.list();
        for (TranslatedText t : list1) {
            if (t.getGreeting() != null) {
                Npc npc = t.getGreeting().getNpc();
                npc.setHasNewTranslations(Boolean.TRUE);
                em.merge(npc);
            }
            if (t.getSubtitle() != null) {
                Npc npc = t.getSubtitle().getNpc();
                npc.setHasNewTranslations(Boolean.TRUE);
                em.merge(npc);
            }
            if (t.getNpcTopic() != null) {
                Npc npc = t.getNpcTopic().getNpc();
                npc.setHasNewTranslations(Boolean.TRUE);
                em.merge(npc);
            }
            if (t.getPlayerTopic() != null) {
                Npc npc = t.getPlayerTopic().getNpc();
                npc.setHasNewTranslations(Boolean.TRUE);
                em.merge(npc);
            }
        }

    }

    @Transactional
    public void calculateQuestProgressByNpc(Npc n) {
        Npc np = em.find(Npc.class, n.getId());
        for (Quest q : np.getQuests()) {
            int totalPhases = 0;
            int translatedPhrases = 0;
            for (Npc npc : q.getNpcs()) {
                for (Topic t : npc.getTopics()) {
                    GSpreadSheetsNpcPhrase extNpcPhrase = t.getExtNpcPhrase();
                    if (extNpcPhrase != null) {
                        totalPhases++;
                        if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                        }
                    }
                    GSpreadSheetsPlayerPhrase extPlayerPhrase = t.getExtPlayerPhrase();
                    if (extPlayerPhrase != null) {
                        totalPhases++;
                        if (extPlayerPhrase.getTranslator() != null && !extPlayerPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                        }
                    }
                }
                for (Subtitle s : npc.getSubtitles()) {
                    GSpreadSheetsNpcPhrase extNpcPhrase = s.getExtNpcPhrase();
                    if (extNpcPhrase != null) {
                        totalPhases++;
                        if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                        }
                    }
                }
                for (Greeting g : npc.getGreetings()) {
                    GSpreadSheetsNpcPhrase extNpcPhrase = g.getExtNpcPhrase();
                    if (extNpcPhrase != null) {
                        totalPhases++;
                        if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                            translatedPhrases++;
                        }
                    }
                }
            }
            float r = 0;
            if (totalPhases > 0) {
                r = (float) translatedPhrases / totalPhases;

            }
            if (r > 0) {
                q.setProgress(new BigDecimal(r).setScale(2, RoundingMode.UP));
            } else {
                q.setProgress(BigDecimal.ZERO);
            }

            em.merge(q);
        }
    }

    @Transactional
    public void calculateNpcProgress(Npc n) {
        int totalPhases = 0;
        int translatedPhrases = 0;
        Npc npc = em.find(Npc.class, n.getId());
        for (Topic t : npc.getTopics()) {
            GSpreadSheetsNpcPhrase extNpcPhrase = t.getExtNpcPhrase();
            if (extNpcPhrase != null) {
                totalPhases++;
                if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                    translatedPhrases++;
                }
            }
            GSpreadSheetsPlayerPhrase extPlayerPhrase = t.getExtPlayerPhrase();
            if (extPlayerPhrase != null) {
                totalPhases++;
                if (extPlayerPhrase.getTranslator() != null && !extPlayerPhrase.getTranslator().isEmpty()) {
                    translatedPhrases++;
                }
            }
        }
        for (Subtitle s : npc.getSubtitles()) {
            GSpreadSheetsNpcPhrase extNpcPhrase = s.getExtNpcPhrase();
            if (extNpcPhrase != null) {
                totalPhases++;
                if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                    translatedPhrases++;
                }
            }
        }
        for (Greeting g : npc.getGreetings()) {
            GSpreadSheetsNpcPhrase extNpcPhrase = g.getExtNpcPhrase();
            if (extNpcPhrase != null) {
                totalPhases++;
                if (extNpcPhrase.getTranslator() != null && !extNpcPhrase.getTranslator().isEmpty()) {
                    translatedPhrases++;
                }
            }

            float r = 0;
            if (totalPhases > 0) {
                r = (float) translatedPhrases / totalPhases;

            }
            if (r > 0) {
                n.setProgress(new BigDecimal(r).setScale(2, RoundingMode.UP));
            } else {
                n.setProgress(BigDecimal.ZERO);
            }
            em.merge(n);
        }

    }

    @Transactional
    public HierarchicalContainer searchInCatalogs(String search, HierarchicalContainer hc) {
        hc.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria locationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
        locationCrit.add(Restrictions.or(Restrictions.ilike("textEn", "%" + search + "%"), Restrictions.ilike("textRu", "%" + search + "%")));
        List<GSpreadSheetsLocationName> locationList = locationCrit.list();
        for (GSpreadSheetsLocationName loc : locationList) {
            Item item = hc.addItem(loc);
            item.getItemProperty("textEn").setValue(loc.getTextEn());
            item.getItemProperty("textRu").setValue(loc.getTextRu());
            item.getItemProperty("translator").setValue(loc.getTranslator());
            item.getItemProperty("catalogType").setValue("Локация");
        }
        Criteria npcCrit = session.createCriteria(GSpreadSheetsNpcName.class);
        npcCrit.add(Restrictions.or(Restrictions.ilike("textEn", "%" + search + "%"), Restrictions.ilike("textRu", "%" + search + "%")));
        List<GSpreadSheetsNpcName> npcList = npcCrit.list();
        for (GSpreadSheetsNpcName npc : npcList) {
            Item item = hc.addItem(npc);
            item.getItemProperty("textEn").setValue(npc.getTextEn());
            item.getItemProperty("textRu").setValue(npc.getTextRu());
            item.getItemProperty("translator").setValue(npc.getTranslator());
            item.getItemProperty("catalogType").setValue("NPC");
        }
        Criteria npcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
        npcPhraseCrit.add(Restrictions.or(Restrictions.ilike("textEn", "%" + search + "%"), Restrictions.ilike("textRu", "%" + search + "%")));
        List<GSpreadSheetsNpcPhrase> npcPhraseList = npcPhraseCrit.list();
        for (GSpreadSheetsNpcPhrase gSpreadSheetsNpcPhrase : npcPhraseList) {
            Item item = hc.addItem(gSpreadSheetsNpcPhrase);
            item.getItemProperty("textEn").setValue(gSpreadSheetsNpcPhrase.getTextEn());
            item.getItemProperty("textRu").setValue(gSpreadSheetsNpcPhrase.getTextRu());
            item.getItemProperty("translator").setValue(gSpreadSheetsNpcPhrase.getTranslator());
            item.getItemProperty("catalogType").setValue("Фраза NPC");
        }
        Criteria playerPhraseCrit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
        playerPhraseCrit.add(Restrictions.or(Restrictions.ilike("textEn", "%" + search + "%"), Restrictions.ilike("textRu", "%" + search + "%")));
        List<GSpreadSheetsPlayerPhrase> playerPhraseList = playerPhraseCrit.list();
        for (GSpreadSheetsPlayerPhrase gSpreadSheetsPlayerPhrase : playerPhraseList) {
            Item item = hc.addItem(gSpreadSheetsPlayerPhrase);
            item.getItemProperty("textEn").setValue(gSpreadSheetsPlayerPhrase.getTextEn());
            item.getItemProperty("textRu").setValue(gSpreadSheetsPlayerPhrase.getTextRu());
            item.getItemProperty("translator").setValue(gSpreadSheetsPlayerPhrase.getTranslator());
            item.getItemProperty("catalogType").setValue("Фраза игрока");
        }

        return hc;
    }

    @Transactional
    public void updateUserPassword(SysAccount account, String newPassword) {
        SysAccount a = em.find(SysAccount.class, account.getId());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(newPassword);
        a.setPassword(hashedPassword);
        em.merge(a);
    }

    @Transactional
    public JPAContainer getJPAContainerContainerForClass(Class c) {
        return JPAContainerFactory.makeBatchable(c, em);
    }

    @Transactional
    public void commitTableEntityItem(EntityItem item) {
        if ((item.getEntity() instanceof GSpreadSheetsNpcName) || (item.getEntity() instanceof GSpreadSheetsLocationName) || (item.getEntity() instanceof GSpreadSheetsNpcPhrase) || (item.getEntity() instanceof GSpreadSheetsPlayerPhrase)) {
            item.getItemProperty("changeTime").setValue(new Date());
            item.getItemProperty("translator").setValue(SpringSecurityHelper.getSysAccount().getLogin());
        }
        em.merge(item.getEntity());
    }

    @Transactional
    public void updateNpcHasTranslated(Npc n) {
        Npc npc = em.find(Npc.class, n.getId());
        boolean hasNewTranslations = false;
        for (Topic t : npc.getTopics()) {
            for (TranslatedText tr : t.getNpcTranslations()) {
                if (tr.getStatus() == TRANSLATE_STATUS.NEW) {
                    hasNewTranslations = true;
                    break;
                }
            }
            if (!hasNewTranslations) {
                for (TranslatedText tr : t.getPlayerTranslations()) {
                    if (tr.getStatus() == TRANSLATE_STATUS.NEW) {
                        hasNewTranslations = true;
                        break;
                    }
                }
            }
            if (hasNewTranslations) {
                break;
            }

        }
        if (!hasNewTranslations) {
            for (Greeting g : npc.getGreetings()) {
                for (TranslatedText tr : g.getTranslations()) {
                    if (tr.getStatus() == TRANSLATE_STATUS.NEW) {
                        hasNewTranslations = true;
                        break;
                    }
                }
                if (hasNewTranslations) {
                    break;
                }
            }
        }
        if (!hasNewTranslations) {
            for (Subtitle s : npc.getSubtitles()) {
                for (TranslatedText tr : s.getTranslations()) {
                    if (tr.getStatus() == TRANSLATE_STATUS.NEW) {
                        hasNewTranslations = true;
                        break;
                    }
                }
                if (hasNewTranslations) {
                    break;
                }
            }
        }
        npc.setHasNewTranslations(hasNewTranslations);
        em.merge(npc);
    }

    @Transactional
    public void updateNpcHasTranslated(TranslatedText t) {
        if (t.getGreeting() != null) {
            updateNpcHasTranslated(t.getGreeting().getNpc());
        }
        if (t.getSubtitle() != null) {
            updateNpcHasTranslated(t.getSubtitle().getNpc());
        }
        if (t.getNpcTopic() != null) {
            updateNpcHasTranslated(t.getNpcTopic().getNpc());
        }
        if (t.getPlayerTopic() != null) {
            updateNpcHasTranslated(t.getPlayerTopic().getNpc());
        }
    }
}
