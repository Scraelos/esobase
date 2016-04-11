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
import org.esn.esobase.model.EsoRawString;
import org.esn.esobase.model.GSpreadSheetsActivator;
import org.esn.esobase.model.GSpreadSheetsItemDescription;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsJournalEntry;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.Greeting;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.NPC_SEX;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.SysAccountRole;
import org.esn.esobase.model.SystemProperty;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.Topic;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
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
        roles.add(new SysAccountRole(6L, "ROLE_DIRECT_ACCESS_NPC_NAMES", "Прямое редактирование имён NPC"));
        roles.add(new SysAccountRole(7L, "ROLE_DIRECT_ACCESS_LOCATION_NAMES", "Прямое редактирование названий Локаций"));
        roles.add(new SysAccountRole(8L, "ROLE_DIRECT_ACCESS_NPC_PHRASES", "Прямое редактирование реплик NPC"));
        roles.add(new SysAccountRole(9L, "ROLE_DIRECT_ACCESS_PLAYER_PHRASES", "Прямое редактирование реплик игрока"));
        roles.add(new SysAccountRole(10L, "ROLE_DIRECT_ACCESS_QUEST_NAMES", "Прямое редактирование названий квестов"));
        roles.add(new SysAccountRole(11L, "ROLE_DIRECT_ACCESS_QUEST_DESCRIPTIONS", "Прямое редактирование описаний квестов"));
        roles.add(new SysAccountRole(12L, "ROLE_MANAGE_USERS", "Управление пользователями"));
        roles.add(new SysAccountRole(13L, "ROLE_DIRECT_ACCESS_ACTIVATORS", "Прямое редактирование активаторов"));
        roles.add(new SysAccountRole(14L, "ROLE_DIRECT_ACCESS_JOURNAL_ENTRIES", "Прямое редактирование записей журнала"));
        roles.add(new SysAccountRole(15L, "ROLE_DIRECT_ACCESS_ITEM_NAMES", "Прямое редактирование названий предметов"));
        roles.add(new SysAccountRole(16L, "ROLE_DIRECT_ACCESS_ITEM_DESCRIPTIONS", "Прямое редактирование описаний предметов"));
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
        Criteria questCrit = session.createCriteria(Quest.class);
        List<Quest> quests = questCrit.list();
        for (Quest q : quests) {
            Criteria gsQuestCrit = session.createCriteria(GSpreadSheetsQuestName.class);
            if (q.getName() != null && !q.getName().isEmpty()) {
                gsQuestCrit.add(Restrictions.ilike("textEn", q.getName()));
            }
            List<GSpreadSheetsQuestName> questList = gsQuestCrit.list();
            for (GSpreadSheetsQuestName gnpc : questList) {
                q.setName(gnpc.getTextEn());
                q.setNameRu(gnpc.getTextRu());
                em.merge(q);
            }
        }

    }

    @Transactional
    public void importFromLua(List<Location> locations) {
        Session session = (Session) em.getDelegate();

        for (Location location : locations) {
            Criteria gsLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
            if (location.getName() != null && !location.getName().isEmpty()) {
                gsLocationCrit.add(Restrictions.ilike("textEn", location.getName()));
            } else if (location.getNameRu() != null && !location.getNameRu().isEmpty()) {
                gsLocationCrit.add(Restrictions.ilike("textRu", location.getNameRu()));
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
                    gsNpcCrit.add(Restrictions.ilike("textEn", npc.getName()));
                } else if (npc.getNameRu() != null && !npc.getNameRu().isEmpty()) {
                    gsNpcCrit.add(Restrictions.ilike("textRu", npc.getNameRu()));
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
                    locationCriteria.add(Restrictions.ilike("name", location.getName()));
                } else {
                    locationCriteria.add(Restrictions.ilike("nameRu", location.getNameRu()));
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
                            npcCriteria.add(Restrictions.ilike("name", npc.getName()));
                        } else {
                            npcCriteria.add(Restrictions.ilike("nameRu", npc.getNameRu()));
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
                                    subtitleCriteria.add(Restrictions.ilike("text", subtitle.getText()));
                                } else {
                                    subtitleCriteria.add(Restrictions.ilike("textRu", subtitle.getTextRu()));
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
                                    topicCriteria.add(Restrictions.ilike("playerText", topic.getPlayerText()));
                                } else {
                                    topicCriteria.add(Restrictions.ilike("playerTextRu", topic.getPlayerTextRu()));
                                }
                                if (topic.getNpcText() != null) {
                                    topicCriteria.add(Restrictions.ilike("npcText", topic.getNpcText()));
                                } else {
                                    topicCriteria.add(Restrictions.ilike("npcTextRu", topic.getNpcTextRu()));
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
    public void loadQuestNamesFromSpreadSheet(List<GSpreadSheetsQuestName> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsQuestName.class);
        Map<String, GSpreadSheetsQuestName> itemMap = new HashMap<>();
        List<GSpreadSheetsQuestName> allItems = crit.list();
        for (GSpreadSheetsQuestName item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsQuestName item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "quest name {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsQuestName result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for quest name: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for quest name: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsQuestName item : items) {
            GSpreadSheetsQuestName result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting quest name for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsQuestName> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsQuestName location : items) {
            spreadSheetItemMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsQuestName item : allItems) {
            GSpreadSheetsQuestName result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing quest name rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadQuestDesciptionsFromSpreadSheet(List<GSpreadSheetsQuestDescription> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsQuestDescription.class);
        Map<String, GSpreadSheetsQuestDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsQuestDescription> allItems = crit.list();
        for (GSpreadSheetsQuestDescription item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsQuestDescription item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "quest description {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsQuestDescription result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for quest description: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for quest description: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsQuestDescription item : items) {
            GSpreadSheetsQuestDescription result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting quest description for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsQuestDescription> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsQuestDescription location : items) {
            spreadSheetItemMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsQuestDescription item : allItems) {
            GSpreadSheetsQuestDescription result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing quest description rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadItemNamesFromSpreadSheet(List<GSpreadSheetsItemName> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsItemName.class);
        Map<String, GSpreadSheetsItemName> itemMap = new HashMap<>();
        List<GSpreadSheetsItemName> allItems = crit.list();
        for (GSpreadSheetsItemName item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsItemName item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "item name {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsItemName result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for item name: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for item name: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsItemName item : items) {
            GSpreadSheetsItemName result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting item name for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsItemName> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsItemName location : items) {
            spreadSheetItemMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsItemName item : allItems) {
            GSpreadSheetsItemName result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing item name rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadItemDesciptionsFromSpreadSheet(List<GSpreadSheetsItemDescription> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsItemDescription.class);
        Map<String, GSpreadSheetsItemDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsItemDescription> allItems = crit.list();
        for (GSpreadSheetsItemDescription item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsItemDescription item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "item description {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsItemDescription result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for item description: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for item description: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsItemDescription item : items) {
            GSpreadSheetsItemDescription result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting item description for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsItemDescription> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsItemDescription location : items) {
            spreadSheetItemMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsItemDescription item : allItems) {
            GSpreadSheetsItemDescription result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing item description rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadActivatorsFromSpreadSheet(List<GSpreadSheetsActivator> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsActivator.class);
        Map<String, GSpreadSheetsActivator> itemMap = new HashMap<>();
        List<GSpreadSheetsActivator> allItems = crit.list();
        for (GSpreadSheetsActivator item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsActivator item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "activator {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsActivator result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for activator: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for activator: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsActivator item : items) {
            GSpreadSheetsActivator result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting activator for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsActivator> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsActivator location : items) {
            spreadSheetItemMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsActivator item : allItems) {
            GSpreadSheetsActivator result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing activator rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadJournalEntriesFromSpreadSheet(List<GSpreadSheetsJournalEntry> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsJournalEntry.class);
        Map<String, GSpreadSheetsJournalEntry> itemMap = new HashMap<>();
        List<GSpreadSheetsJournalEntry> allItems = crit.list();
        for (GSpreadSheetsJournalEntry item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsJournalEntry item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "journal entry {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsJournalEntry result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for journal entry: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for journal entry: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsJournalEntry item : items) {
            GSpreadSheetsJournalEntry result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting journal entry for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsJournalEntry> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsJournalEntry location : items) {
            spreadSheetItemMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsJournalEntry item : allItems) {
            GSpreadSheetsJournalEntry result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing journal entry rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
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
    public HierarchicalContainer getQuestNamesDiff(List<GSpreadSheetsQuestName> items, HierarchicalContainer hc) {
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
        List<QuestNamesDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsQuestName.class);
        Map<Long, GSpreadSheetsQuestName> itemMap = new HashMap<>();
        List<GSpreadSheetsQuestName> allItems = crit.list();
        for (GSpreadSheetsQuestName item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsQuestName item : items) {
            GSpreadSheetsQuestName result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new QuestNamesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new QuestNamesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new QuestNamesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new QuestNamesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new QuestNamesDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (QuestNamesDiff diff : diffs) {
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
    public HierarchicalContainer getQuestDescriptionsDiff(List<GSpreadSheetsQuestDescription> items, HierarchicalContainer hc) {
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
        List<QuestDescriptionsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsQuestDescription.class);
        Map<Long, GSpreadSheetsQuestDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsQuestDescription> allItems = crit.list();
        for (GSpreadSheetsQuestDescription item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsQuestDescription item : items) {
            GSpreadSheetsQuestDescription result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new QuestDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new QuestDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new QuestDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new QuestDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new QuestDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (QuestDescriptionsDiff diff : diffs) {
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
    public HierarchicalContainer getItemNamesDiff(List<GSpreadSheetsItemName> items, HierarchicalContainer hc) {
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
        List<ItemNamesDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsItemName.class);
        Map<Long, GSpreadSheetsItemName> itemMap = new HashMap<>();
        List<GSpreadSheetsItemName> allItems = crit.list();
        for (GSpreadSheetsItemName item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsItemName item : items) {
            GSpreadSheetsItemName result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new ItemNamesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new ItemNamesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new ItemNamesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new ItemNamesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new ItemNamesDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (ItemNamesDiff diff : diffs) {
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
    public HierarchicalContainer getItemDescriptionsDiff(List<GSpreadSheetsItemDescription> items, HierarchicalContainer hc) {
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
        List<ItemDescriptionsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsItemDescription.class);
        Map<Long, GSpreadSheetsItemDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsItemDescription> allItems = crit.list();
        for (GSpreadSheetsItemDescription item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsItemDescription item : items) {
            GSpreadSheetsItemDescription result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new ItemDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new ItemDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new ItemDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new ItemDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new ItemDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (ItemDescriptionsDiff diff : diffs) {
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
    public HierarchicalContainer getActivatorsDiff(List<GSpreadSheetsActivator> items, HierarchicalContainer hc) {
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
        List<ActivatorsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsActivator.class);
        Map<Long, GSpreadSheetsActivator> itemMap = new HashMap<>();
        List<GSpreadSheetsActivator> allItems = crit.list();
        for (GSpreadSheetsActivator item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsActivator item : items) {
            GSpreadSheetsActivator result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new ActivatorsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new ActivatorsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new ActivatorsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new ActivatorsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new ActivatorsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (ActivatorsDiff diff : diffs) {
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
    public HierarchicalContainer getJournalEntriesDiff(List<GSpreadSheetsJournalEntry> items, HierarchicalContainer hc) {
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
        List<JournalEntriesDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsJournalEntry.class);
        Map<Long, GSpreadSheetsJournalEntry> itemMap = new HashMap<>();
        List<GSpreadSheetsJournalEntry> allItems = crit.list();
        for (GSpreadSheetsJournalEntry item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsJournalEntry item : items) {
            GSpreadSheetsJournalEntry result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new JournalEntriesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new JournalEntriesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new JournalEntriesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new JournalEntriesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new JournalEntriesDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (JournalEntriesDiff diff : diffs) {
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
                    Query fullTextQuery = em.createNativeQuery("select id from gspreadsheetsnpcphrase where texten @@ :textEn");
                    fullTextQuery.setParameter("textEn", g.getText());
                    List resultList = fullTextQuery.getResultList();
                    if (resultList != null && resultList.size() > 0) {
                        Object firstRow = resultList.get(0);
                        BigInteger phraseId = (BigInteger) firstRow;
                        phrase = em.find(GSpreadSheetsNpcPhrase.class, Long.valueOf(phraseId.longValue()));
                        foundCounter++;
                        g.setExtNpcPhrase(phrase);
                        Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}", new Object[]{Integer.toString(counter), total, foundCounter});
                        em.merge(g);
                    }
                    if (phrase == null) {
                        assignGreetingToPhrase(g);
                    }
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
                    Query fullTextQuery = em.createNativeQuery("select id from gspreadsheetsnpcphrase where texten @@ :textEn");
                    fullTextQuery.setParameter("textEn", t.getNpcText());
                    List resultList = fullTextQuery.getResultList();
                    if (resultList != null && resultList.size() > 0) {
                        Object firstRow = resultList.get(0);
                        BigInteger phraseId = (BigInteger) firstRow;
                        phrase = em.find(GSpreadSheetsNpcPhrase.class, Long.valueOf(phraseId.longValue()));
                        foundCounter++;
                        t.setExtNpcPhrase(phrase);
                        Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}", new Object[]{Integer.toString(counter), total, foundCounter});
                        em.merge(t);
                    }
                    if (phrase == null) {
                        assignTopicToPhrase(t);
                    }
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
                    Query fullTextQuery = em.createNativeQuery("select id from gspreadsheetsplayerphrase where texten @@ :textEn");
                    fullTextQuery.setParameter("textEn", t.getPlayerText());
                    List resultList = fullTextQuery.getResultList();
                    if (resultList != null && resultList.size() > 0) {
                        Object firstRow = resultList.get(0);
                        BigInteger phraseId = (BigInteger) firstRow;
                        phrase = em.find(GSpreadSheetsPlayerPhrase.class, Long.valueOf(phraseId.longValue()));
                        foundCounter++;
                        t.setExtPlayerPhrase(phrase);
                        Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}", new Object[]{Integer.toString(counter), total, foundCounter});
                        em.merge(t);
                    }
                    if (phrase == null) {
                        assignTopicToPhrase(t);
                    }
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
    public BeanItemContainer<Topic> getNpcTopics(Npc npc, BeanItemContainer<Topic> container, boolean withNewTranslations, SysAccount translator) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Topic.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extPlayerPhrase", FetchMode.JOIN);
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("playerTranslations", FetchMode.SELECT);
        crit.setFetchMode("npcTranslations", FetchMode.SELECT);
        if (withNewTranslations || (translator != null)) {
            crit.createAlias("playerTranslations", "playerTranslations", JoinType.LEFT_OUTER_JOIN);
            crit.createAlias("npcTranslations", "npcTranslations", JoinType.LEFT_OUTER_JOIN);
        }
        if (withNewTranslations) {
            crit.add(Restrictions.or(
                    Restrictions.eq("playerTranslations.status", TRANSLATE_STATUS.NEW),
                    Restrictions.eq("npcTranslations.status", TRANSLATE_STATUS.NEW)
            )
            );
        }
        if (translator != null) {
            crit.add(Restrictions.or(
                    Restrictions.eq("playerTranslations.author", translator),
                    Restrictions.eq("npcTranslations.author", translator)
            )
            );
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Topic> list = crit.list();
        container.addAll(list);
        return container;
    }

    @Transactional
    public BeanItemContainer<Greeting> getNpcGreetings(Npc npc, BeanItemContainer<Greeting> container, boolean withNewTranslations, SysAccount translator) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Greeting.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("translations", FetchMode.SELECT);
        if (withNewTranslations || (translator != null)) {
            crit.createAlias("translations", "translations");
        }
        if (withNewTranslations) {
            crit.add(Restrictions.sizeGt("translations", 0));
            crit.add(Restrictions.eq("translations.status", TRANSLATE_STATUS.NEW));
        }
        if (translator != null) {
            crit.add(Restrictions.eq("translations.author", translator));
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Greeting> list = crit.list();

        container.addAll(list);
        return container;
    }

    @Transactional
    public BeanItemContainer<Subtitle> getNpcSubtitles(Npc npc, BeanItemContainer<Subtitle> container, boolean withNewTranslations, SysAccount translator) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Subtitle.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("translations", FetchMode.SELECT);
        if (withNewTranslations || (translator != null)) {
            crit.createAlias("translations", "translations");
        }
        if (withNewTranslations) {
            crit.add(Restrictions.sizeGt("translations", 0));
            crit.add(Restrictions.eq("translations.status", TRANSLATE_STATUS.NEW));
        }
        if (translator != null) {
            crit.add(Restrictions.eq("translations.author", translator));
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Subtitle> list = crit.list();
        container.addAll(list);
        return container;
    }

    @Transactional
    public BeanItemContainer<Npc> getNpcs(BeanItemContainer<Npc> container, boolean withNewTranslations, SysAccount translator) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Npc.class);
        crit.setFetchMode("location", FetchMode.JOIN);
        if (withNewTranslations) {
            crit.add(Restrictions.eq("hasNewTranslations", true));
        }
        if (translator != null) {
            crit.createAlias("translators", "tr");
            crit.add(Restrictions.eq("tr.id", translator.getId()));
        }
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<Npc> list = crit.list();
        container.addAll(list);
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
            entity.setCreateTime(new Date());
            em.persist(entity);
        } else if (entity.getText() == null || entity.getText().isEmpty()) {
            em.remove(em.find(TranslatedText.class, entity.getId()));
        } else {
            entity.setChangeTime(new Date());
            em.merge(entity);
        }
    }

    @Transactional
    public void saveTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.NEW);
        if (entity.getId() == null) {
            entity.setCreateTime(new Date());
            em.persist(entity);
        } else if (entity.getText() == null || entity.getText().isEmpty()) {
            em.remove(em.find(TranslatedText.class, entity.getId()));
        } else {
            entity.setChangeTime(new Date());
            em.merge(entity);
        }
        Npc npc = null;
        if (entity.getSubtitle() != null) {
            npc = entity.getSubtitle().getNpc();
        } else if (entity.getGreeting() != null) {
            npc = entity.getGreeting().getNpc();
        } else if (entity.getPlayerTopic() != null) {
            npc = entity.getPlayerTopic().getNpc();
        } else if (entity.getNpcTopic() != null) {
            npc = entity.getNpcTopic().getNpc();
        }
        if (npc != null) {
            updateNpcHasTranslated(npc);
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
            npcNameCrit.add(Restrictions.ilike("name", name.getTextEn()));
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
            Criteria locationsCrit = session.createCriteria(Location.class);
            locationsCrit.add(Restrictions.ilike("name", name.getTextEn()));
            List<Location> list = locationsCrit.list();
            for (Location l : list) {
                l.setNameRu(name.getTextRu());
                em.merge(l);
            }
        }
    }

    @Transactional
    public void saveQuestNames(List<GSpreadSheetsQuestName> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsQuestName item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsQuestName.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsQuestName result = (GSpreadSheetsQuestName) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(item.getChangeTime());
                result.setTextRu(item.getTextRu());
                result.setTranslator(item.getTranslator());
                em.merge(result);
            } else {
                em.persist(item);
            }
            Criteria questsCrit = session.createCriteria(Quest.class);
            questsCrit.add(Restrictions.ilike("name", item.getTextEn()));
            List<Quest> list = questsCrit.list();
            for (Quest q : list) {
                q.setName(item.getTextEn());
                q.setNameRu(item.getTextRu());
                em.merge(q);
            }
        }
    }

    @Transactional
    public void saveQuestDescriptions(List<GSpreadSheetsQuestDescription> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsQuestDescription item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsQuestDescription.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsQuestDescription result = (GSpreadSheetsQuestDescription) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(item.getChangeTime());
                result.setTextRu(item.getTextRu());
                result.setTranslator(item.getTranslator());
                em.merge(result);
            } else {
                em.persist(item);
            }
        }
    }

    @Transactional
    public void saveItemNames(List<GSpreadSheetsItemName> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsItemName item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsItemName.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsItemName result = (GSpreadSheetsItemName) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(item.getChangeTime());
                result.setTextRu(item.getTextRu());
                result.setTranslator(item.getTranslator());
                em.merge(result);
            } else {
                em.persist(item);
            }
        }
    }

    @Transactional
    public void saveItemDescriptions(List<GSpreadSheetsItemDescription> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsItemDescription item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsItemDescription.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsItemDescription result = (GSpreadSheetsItemDescription) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(item.getChangeTime());
                result.setTextRu(item.getTextRu());
                result.setTranslator(item.getTranslator());
                em.merge(result);
            } else {
                em.persist(item);
            }
        }
    }

    @Transactional
    public void saveActivators(List<GSpreadSheetsActivator> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsActivator item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsActivator.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsActivator result = (GSpreadSheetsActivator) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(item.getChangeTime());
                result.setTextRu(item.getTextRu());
                result.setTranslator(item.getTranslator());
                em.merge(result);
            } else {
                em.persist(item);
            }
        }
    }

    @Transactional
    public void saveJournalEntries(List<GSpreadSheetsJournalEntry> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsJournalEntry item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsJournalEntry.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsJournalEntry result = (GSpreadSheetsJournalEntry) crit.uniqueResult();
            if (result != null) {
                result.setChangeTime(item.getChangeTime());
                result.setTextRu(item.getTextRu());
                result.setTranslator(item.getTranslator());
                em.merge(result);
            } else {
                em.persist(item);
            }
        }
    }

    @Transactional
    public void rejectTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.REJECTED);
        em.merge(entity);
        Npc npc = null;
        if (entity.getSubtitle() != null) {
            npc = entity.getSubtitle().getNpc();
        } else if (entity.getGreeting() != null) {
            npc = entity.getGreeting().getNpc();
        } else if (entity.getPlayerTopic() != null) {
            npc = entity.getPlayerTopic().getNpc();
        } else if (entity.getNpcTopic() != null) {
            npc = entity.getNpcTopic().getNpc();
        }
        if (npc != null) {
            updateNpcHasTranslated(npc);
        }
    }

    @Transactional
    public void acceptTranslatedText(TranslatedText entity) {

        Npc npc = null;
        if (entity.getGreeting() != null) {
            npc = entity.getGreeting().getNpc();
            Greeting greeting = em.find(Greeting.class, entity.getGreeting().getId());
            GSpreadSheetsNpcPhrase npcPhrase = greeting.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText().replace("\n", "$"));
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                entity.setApptovedTime(new Date());
                entity.setApprovedBy(SpringSecurityHelper.getSysAccount());
                em.merge(entity);
            }

        }
        if (entity.getSubtitle() != null) {
            npc = entity.getSubtitle().getNpc();
            Subtitle subtitle = em.find(Subtitle.class, entity.getSubtitle().getId());
            GSpreadSheetsNpcPhrase npcPhrase = subtitle.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText().replace("\n", "$"));
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                entity.setApptovedTime(new Date());
                entity.setApprovedBy(SpringSecurityHelper.getSysAccount());
                em.merge(entity);
            }

        }
        if (entity.getNpcTopic() != null) {
            npc = entity.getNpcTopic().getNpc();
            Topic topic = em.find(Topic.class, entity.getNpcTopic().getId());
            GSpreadSheetsNpcPhrase npcPhrase = topic.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText().replace("\n", "$"));
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                entity.setApptovedTime(new Date());
                entity.setApprovedBy(SpringSecurityHelper.getSysAccount());
                em.merge(entity);
            }

        }
        if (entity.getPlayerTopic() != null) {
            npc = entity.getPlayerTopic().getNpc();
            Topic topic = em.find(Topic.class, entity.getPlayerTopic().getId());
            GSpreadSheetsPlayerPhrase playerPhrase = topic.getExtPlayerPhrase();
            if (playerPhrase != null) {
                playerPhrase.setTextRu(entity.getText().replace("\n", "$"));
                playerPhrase.setTranslator(entity.getAuthor().getLogin());
                playerPhrase.setChangeTime(new Date());
                em.merge(playerPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                entity.setApptovedTime(new Date());
                entity.setApprovedBy(SpringSecurityHelper.getSysAccount());
                em.merge(entity);
            }

        }
        if (npc != null) {
            calculateNpcProgress(npc);
            updateNpcHasTranslated(npc);
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
        //npcCrit.add(Restrictions.eq("status", TRANSLATE_STATUS.NEW));
        List<TranslatedText> list1 = npcCrit.list();
        for (TranslatedText t : list1) {
            Npc npc = null;
            if (t.getGreeting() != null) {
                npc = t.getGreeting().getNpc();
            }
            if (t.getSubtitle() != null) {
                npc = t.getSubtitle().getNpc();
            }
            if (t.getNpcTopic() != null) {
                npc = t.getNpcTopic().getNpc();
            }
            if (t.getPlayerTopic() != null) {
                npc = t.getPlayerTopic().getNpc();
            }
            if (npc != null) {
                Set<SysAccount> translators = npc.getTranslators();
                if (translators == null) {
                    translators = new HashSet<>();
                }
                if (!translators.contains(t.getAuthor())) {
                    translators.add(t.getAuthor());
                }
                npc.setTranslators(translators);
                if (t.getStatus() == TRANSLATE_STATUS.NEW) {
                    npc.setHasNewTranslations(Boolean.TRUE);
                }
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
        Npc npc=em.find(Npc.class, n.getId());
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
        Integer weightValue = null;
        List<Criterion> searchTermitems = new ArrayList<>();
        try {
            weightValue = new Integer(search);
            searchTermitems.add(Restrictions.eq("weight", weightValue));
        } catch (NumberFormatException ex) {

        }
        searchTermitems.add(Restrictions.ilike("textEn", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("textRu", search, MatchMode.ANYWHERE));
        Disjunction searchTerms = Restrictions.or(searchTermitems.toArray(new Criterion[searchTermitems.size()]));
        if (weightValue != null) {

        }
        Session session = (Session) em.getDelegate();

        Criteria npcCrit = session.createCriteria(GSpreadSheetsNpcName.class);
        npcCrit.add(searchTerms);
        List<GSpreadSheetsNpcName> npcList = npcCrit.list();
        for (GSpreadSheetsNpcName npc : npcList) {
            Item item = hc.addItem(npc);
            item.getItemProperty("textEn").setValue(npc.getTextEn());
            item.getItemProperty("textRu").setValue(npc.getTextRu());
            item.getItemProperty("weight").setValue(npc.getWeight());
            item.getItemProperty("translator").setValue(npc.getTranslator());
            item.getItemProperty("catalogType").setValue("NPC");
        }
        Criteria locationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
        locationCrit.add(searchTerms);
        List<GSpreadSheetsLocationName> locationList = locationCrit.list();
        for (GSpreadSheetsLocationName loc : locationList) {
            Item item = hc.addItem(loc);
            item.getItemProperty("textEn").setValue(loc.getTextEn());
            item.getItemProperty("textRu").setValue(loc.getTextRu());
            item.getItemProperty("weight").setValue(loc.getWeight());
            item.getItemProperty("translator").setValue(loc.getTranslator());
            item.getItemProperty("catalogType").setValue("Локация");
        }
        Criteria activatorCrit = session.createCriteria(GSpreadSheetsActivator.class);
        activatorCrit.add(searchTerms);
        List<GSpreadSheetsActivator> activatorList = activatorCrit.list();
        for (GSpreadSheetsActivator loc : activatorList) {
            Item item = hc.addItem(loc);
            item.getItemProperty("textEn").setValue(loc.getTextEn());
            item.getItemProperty("textRu").setValue(loc.getTextRu());
            item.getItemProperty("weight").setValue(loc.getWeight());
            item.getItemProperty("translator").setValue(loc.getTranslator());
            item.getItemProperty("catalogType").setValue("Активатор");
        }

        Criteria itemNameCrit = session.createCriteria(GSpreadSheetsItemName.class);
        itemNameCrit.add(searchTerms);
        List<GSpreadSheetsItemName> itemNameList = itemNameCrit.list();
        for (GSpreadSheetsItemName row : itemNameList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
            item.getItemProperty("weight").setValue(row.getWeight());
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Название предмета");
        }

        Criteria itemDescriptionCrit = session.createCriteria(GSpreadSheetsItemDescription.class);
        itemDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsItemDescription> itemDescriptionList = itemDescriptionCrit.list();
        for (GSpreadSheetsItemDescription row : itemDescriptionList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
            item.getItemProperty("weight").setValue(row.getWeight());
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Описание предмета");
        }

        Criteria questNameCrit = session.createCriteria(GSpreadSheetsQuestName.class);
        questNameCrit.add(searchTerms);
        List<GSpreadSheetsQuestName> questNameList = questNameCrit.list();
        for (GSpreadSheetsQuestName row : questNameList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
            item.getItemProperty("weight").setValue(row.getWeight());
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Название квеста");
        }

        Criteria questDescriptionCrit = session.createCriteria(GSpreadSheetsQuestDescription.class);
        questDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsQuestDescription> questDescriptionList = questDescriptionCrit.list();
        for (GSpreadSheetsQuestDescription row : questDescriptionList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
            item.getItemProperty("weight").setValue(row.getWeight());
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Описание квеста");
        }

        Criteria journalEntryCrit = session.createCriteria(GSpreadSheetsJournalEntry.class);
        journalEntryCrit.add(searchTerms);
        List<GSpreadSheetsJournalEntry> journalEntryList = journalEntryCrit.list();
        for (GSpreadSheetsJournalEntry row : journalEntryList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
            item.getItemProperty("weight").setValue(row.getWeight());
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Запись в журнале");
        }

        Criteria npcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
        npcPhraseCrit.add(searchTerms);
        List<GSpreadSheetsNpcPhrase> npcPhraseList = npcPhraseCrit.list();
        for (GSpreadSheetsNpcPhrase gSpreadSheetsNpcPhrase : npcPhraseList) {
            Item item = hc.addItem(gSpreadSheetsNpcPhrase);
            item.getItemProperty("textEn").setValue(gSpreadSheetsNpcPhrase.getTextEn());
            item.getItemProperty("textRu").setValue(gSpreadSheetsNpcPhrase.getTextRu());
            item.getItemProperty("weight").setValue(gSpreadSheetsNpcPhrase.getWeight());
            item.getItemProperty("translator").setValue(gSpreadSheetsNpcPhrase.getTranslator());
            item.getItemProperty("catalogType").setValue("Фраза NPC");
        }
        Criteria playerPhraseCrit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
        playerPhraseCrit.add(searchTerms);
        List<GSpreadSheetsPlayerPhrase> playerPhraseList = playerPhraseCrit.list();
        for (GSpreadSheetsPlayerPhrase gSpreadSheetsPlayerPhrase : playerPhraseList) {
            Item item = hc.addItem(gSpreadSheetsPlayerPhrase);
            item.getItemProperty("textEn").setValue(gSpreadSheetsPlayerPhrase.getTextEn());
            item.getItemProperty("textRu").setValue(gSpreadSheetsPlayerPhrase.getTextRu());
            item.getItemProperty("weight").setValue(gSpreadSheetsPlayerPhrase.getWeight());
            item.getItemProperty("translator").setValue(gSpreadSheetsPlayerPhrase.getTranslator());
            item.getItemProperty("catalogType").setValue("Фраза игрока");
        }

        return hc;
    }

    @Transactional
    public HierarchicalContainer searchInRawStrings(String search, HierarchicalContainer hc) {
        hc.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(EsoRawString.class);
        List<Criterion> searchTermitems = new ArrayList<>();
        searchTermitems.add(Restrictions.ilike("textEn", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("textDe", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("textFr", search, MatchMode.ANYWHERE));
        Disjunction searchTerms = Restrictions.or(searchTermitems.toArray(new Criterion[searchTermitems.size()]));
        crit.add(searchTerms);
        List<EsoRawString> rows = crit.list();
        for (EsoRawString row : rows) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textFr").setValue(row.getTextFr());
            item.getItemProperty("textDe").setValue(row.getTextDe());
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
        if ((item.getEntity() instanceof GSpreadSheetsNpcName) || (item.getEntity() instanceof GSpreadSheetsLocationName) || (item.getEntity() instanceof GSpreadSheetsNpcPhrase) || (item.getEntity() instanceof GSpreadSheetsPlayerPhrase) || (item.getEntity() instanceof GSpreadSheetsQuestName) || (item.getEntity() instanceof GSpreadSheetsQuestDescription) || (item.getEntity() instanceof GSpreadSheetsActivator) || (item.getEntity() instanceof GSpreadSheetsJournalEntry) || (item.getEntity() instanceof GSpreadSheetsItemName) || (item.getEntity() instanceof GSpreadSheetsItemDescription)) {
            item.getItemProperty("changeTime").setValue(new Date());
            item.getItemProperty("translator").setValue(SpringSecurityHelper.getSysAccount().getLogin());
            em.merge(item.getEntity());
            if (item.getEntity() instanceof GSpreadSheetsLocationName) {
                GSpreadSheetsLocationName locationName = (GSpreadSheetsLocationName) item.getEntity();
                Session session = (Session) em.getDelegate();
                Criteria crit = session.createCriteria(Location.class);
                crit.add(Restrictions.ilike("name", locationName.getTextEn()));
                List<Location> list = crit.list();
                for (Location l : list) {
                    l.setNameRu(locationName.getTextRu());
                    em.merge(l);
                }
            }
            if (item.getEntity() instanceof GSpreadSheetsQuestName) {
                GSpreadSheetsQuestName questName = (GSpreadSheetsQuestName) item.getEntity();
                Session session = (Session) em.getDelegate();
                Criteria crit = session.createCriteria(Quest.class);
                crit.add(Restrictions.ilike("name", questName.getTextEn()));
                List<Quest> list = crit.list();
                for (Quest q : list) {
                    q.setNameRu(questName.getTextRu());
                    em.merge(q);
                }
            }
            if (item.getEntity() instanceof GSpreadSheetsNpcName) {
                GSpreadSheetsNpcName npcName = (GSpreadSheetsNpcName) item.getEntity();
                Session session = (Session) em.getDelegate();
                Criteria crit = session.createCriteria(Npc.class);
                crit.add(Restrictions.ilike("name", npcName.getTextEn()));
                List<Npc> list = crit.list();
                for (Npc n : list) {
                    if (n.getSex() == null || n.getSex() == NPC_SEX.U) {
                        n.setSex(npcName.getSex());
                    }
                    n.setName(npcName.getTextEn());
                    n.setNameRu(npcName.getTextRu());
                    em.merge(n);
                }
            }
        }
    }

    @Transactional
    public void updateNpcHasTranslated(Npc n) {
        boolean hasNewTranslations = false;
        Npc npc=em.find(Npc.class, n.getId());
        Set<SysAccount> translators = new HashSet<>();
        for (Topic t : npc.getTopics()) {
            for (TranslatedText tt : t.getPlayerTranslations()) {
                translators.add(tt.getAuthor());
                if (tt.getStatus() == TRANSLATE_STATUS.NEW) {
                    hasNewTranslations = true;
                }
            }
            for (TranslatedText tt : t.getNpcTranslations()) {
                translators.add(tt.getAuthor());
                if (tt.getStatus() == TRANSLATE_STATUS.NEW) {
                    hasNewTranslations = true;
                }
            }
        }
        for (Greeting g : npc.getGreetings()) {
            for (TranslatedText tt : g.getTranslations()) {
                translators.add(tt.getAuthor());
                if (tt.getStatus() == TRANSLATE_STATUS.NEW) {
                    hasNewTranslations = true;
                }
            }
        }
        for (Subtitle s : npc.getSubtitles()) {
            for (TranslatedText tt : s.getTranslations()) {
                translators.add(tt.getAuthor());
                if (tt.getStatus() == TRANSLATE_STATUS.NEW) {
                    hasNewTranslations = true;
                }
            }
        }
        n.setTranslators(translators);
        n.setHasNewTranslations(hasNewTranslations);
        em.merge(n);
    }

    public HierarchicalContainer getStatistics() {
        HierarchicalContainer result = new HierarchicalContainer();
        result.addContainerProperty("name", String.class, null);
        result.addContainerProperty("value", String.class, null);
        Query gdpreadsheetsStatsQuery = em.createNativeQuery("select 'Перевод названий локаций', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetslocationname where texten!=textru union all select null as translated,count(*) as total from gspreadsheetslocationname) as qres union all\n"
                + "select 'Перевод активаторов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsactivator where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsactivator) as qres union all\n"
                + "select 'Перевод имён NPC', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsnpcname where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsnpcname) as qres union all\n"
                + "select 'Перевод названий квестов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsquestname where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsquestname) as qres union all\n"
                + "select 'Перевод описаний квестов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsquestdescription where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsquestdescription) as qres union all\n"
                + "select 'Перевод названий предметов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsitemname where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsitemname) as qres union all\n"
                + "select 'Перевод описаний предметов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsitemdescription where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsitemdescription) as qres union all\n"
                + "select 'Перевод записей в журнале', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsjournalentry where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsjournalentry) as qres union all\n"
                + "select 'Перевод реплик NPC', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsnpcphrase where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsnpcphrase) as qres union all\n"
                + "select 'Перевод реплик игрока', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsplayerphrase where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsplayerphrase) as qres;");
        List<Object[]> gspreadsheetResult = gdpreadsheetsStatsQuery.getResultList();
        for (Object[] row : gspreadsheetResult) {
            Item item = result.addItem(row);
            item.getItemProperty("name").setValue(row[0]);
            BigDecimal translated = (BigDecimal) row[1];
            BigDecimal total = (BigDecimal) row[2];
            BigDecimal progress = translated.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L)).setScale(2, RoundingMode.HALF_UP);
            String progressString = progress.toString() + "%";
            item.getItemProperty("value").setValue(progressString);
        }
        Query newTranslationsQuery = em.createNativeQuery("select count(*) from translatedtext where status='NEW'");
        newTranslationsQuery.setMaxResults(1);
        BigInteger newTranslationCount = (BigInteger) newTranslationsQuery.getSingleResult();
        Item item = result.addItem(newTranslationCount);
        item.getItemProperty("name").setValue("Строк на вычитку");
        item.getItemProperty("value").setValue(Long.toString(newTranslationCount.longValue()));
        return result;
    }

    @Transactional
    public boolean getIsAutoSynchronizationEnabled() {
        boolean result = false;
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(SystemProperty.class);
        crit.add(Restrictions.eq("name", "autoSync"));
        SystemProperty property = (SystemProperty) crit.uniqueResult();
        if (property == null) {
            property = new SystemProperty();
            property.setName("autoSync");
            property.setValue("false");
            em.persist(property);
        } else {
            result = Boolean.valueOf(property.getValue());
        }
        return result;
    }

    @Transactional
    public void setIsAutoSynchronizationEnabled(boolean isEnabled) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(SystemProperty.class);
        crit.add(Restrictions.eq("name", "autoSync"));
        SystemProperty property = (SystemProperty) crit.uniqueResult();
        if (property == null) {
            property = new SystemProperty();
            property.setName("autoSync");
            property.setValue(Boolean.toString(isEnabled));
            em.persist(property);
        } else {
            property.setValue(Boolean.toString(isEnabled));
            em.merge(property);
        }
    }

    @Transactional
    public void insertEnRawStrings(List<Object[]> rows) {
        for (Object[] row : rows) {
            Query q = em.createNativeQuery("insert into esorawstring (id,aid,bid,cid,texten) values (nextval('hibernate_sequence'),:aid,:bid,:cid,:texten)");
            q.setParameter("aid", row[0]);
            q.setParameter("bid", row[1]);
            q.setParameter("cid", row[2]);
            q.setParameter("texten", row[3]);
            q.executeUpdate();
        }
    }

    @Transactional
    public void updateFrRawStrings(List<Object[]> rows) {
        for (Object[] row : rows) {
            Query q = em.createNativeQuery("update esorawstring set textfr=:textfr where aid=:aid and bid=:bid and cid=:cid");
            q.setParameter("aid", row[0]);
            q.setParameter("bid", row[1]);
            q.setParameter("cid", row[2]);
            q.setParameter("textfr", row[3]);
            q.executeUpdate();
        }
    }

    @Transactional
    public void updateDeRawStrings(List<Object[]> rows) {
        for (Object[] row : rows) {
            Query q = em.createNativeQuery("update esorawstring set textde=:textde where aid=:aid and bid=:bid and cid=:cid");
            q.setParameter("aid", row[0]);
            q.setParameter("bid", row[1]);
            q.setParameter("cid", row[2]);
            q.setParameter("textde", row[3]);
            q.executeUpdate();
        }
    }

}
