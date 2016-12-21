/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.esn.esobase.data.diffs.ActivatorsDiff;
import org.esn.esobase.data.diffs.NpcNameDiff;
import org.esn.esobase.data.diffs.NpcPhraseDiff;
import org.esn.esobase.data.diffs.LocationsDiff;
import org.esn.esobase.data.diffs.QuestDescriptionsDiff;
import org.esn.esobase.data.diffs.QuestDirectionsDiff;
import org.esn.esobase.data.diffs.ItemNamesDiff;
import org.esn.esobase.data.diffs.QuestNamesDiff;
import org.esn.esobase.data.diffs.ItemDescriptionsDiff;
import org.esn.esobase.data.diffs.JournalEntriesDiff;
import org.esn.esobase.data.diffs.PlayerPhraseDiff;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.TypedQuery;
import org.esn.esobase.data.diffs.AbilityDescriptionsDiff;
import org.esn.esobase.data.diffs.AchievementDescriptionsDiff;
import org.esn.esobase.data.diffs.AchievementsDiff;
import org.esn.esobase.data.diffs.NotesDiff;
import org.esn.esobase.model.EsoInterfaceVariable;
import org.esn.esobase.model.EsoRawString;
import org.esn.esobase.model.GSpreadSheetsAbilityDescription;
import org.esn.esobase.model.GSpreadSheetsAchievement;
import org.esn.esobase.model.GSpreadSheetsAchievementDescription;
import org.esn.esobase.model.GSpreadSheetsActivator;
import org.esn.esobase.model.GSpreadSheetsItemDescription;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.GSpreadSheetsJournalEntry;
import org.esn.esobase.model.GSpreadSheetsLocationName;
import org.esn.esobase.model.GSpreadSheetsNote;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.GSpreadSheetsNpcPhrase;
import org.esn.esobase.model.GSpreadSheetsPlayerPhrase;
import org.esn.esobase.model.GSpreadSheetsQuestDescription;
import org.esn.esobase.model.GSpreadSheetsQuestDirection;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.Greeting;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.NPC_SEX;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.SpellerWord;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.SysAccountRole;
import org.esn.esobase.model.SystemProperty;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.Topic;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.EsnDecoder;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
public class DBService {

    @PersistenceContext
    private EntityManager em;
    private static final Logger LOG = Logger.getLogger(DBService.class.getName());

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
        roles.add(new SysAccountRole(17L, "ROLE_DIRECT_ACCESS_QUEST_DIRECTIONS", "Прямое редактирование целей заданий"));
        roles.add(new SysAccountRole(18L, "ROLE_SPELL_CHECK", "Проверка орфографии"));
        roles.add(new SysAccountRole(19L, "ROLE_DIRECT_ACCESS_INTERFACE_VARIABLES", "Прямое редактирование строк интерфейса"));
        roles.add(new SysAccountRole(20L, "ROLE_DIRECT_ACCESS_ACHIEVEMENTS", "Прямое редактирование достижений"));
        roles.add(new SysAccountRole(21L, "ROLE_DIRECT_ACCESS_ACHIEVEMENT_DESCRIPTIONS", "Прямое редактирование описаний достижений"));
        roles.add(new SysAccountRole(22L, "ROLE_DIRECT_ACCESS_NOTES", "Прямое редактирование записок"));
        roles.add(new SysAccountRole(23L, "ROLE_DIRECT_ACCESS_ABILITY_DESCRIPTIONS", "Прямое редактирование описаний способностей"));
        roles.add(new SysAccountRole(24L, "ROLE_PREAPPROVE", "Проверка правильности перевода"));
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
    public void loadQuestDirectionsFromSpreadSheet(List<GSpreadSheetsQuestDirection> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsQuestDirection.class);
        Map<String, GSpreadSheetsQuestDirection> itemMap = new HashMap<>();
        List<GSpreadSheetsQuestDirection> allItems = crit.list();
        for (GSpreadSheetsQuestDirection item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsQuestDirection item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "quest direction {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsQuestDirection result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for quest direction: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for quest direction: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsQuestDirection item : items) {
            GSpreadSheetsQuestDirection result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting quest direction for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsQuestDirection> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsQuestDirection location : items) {
            spreadSheetItemMap.put(location.getTextEn(), location);
        }
        for (GSpreadSheetsQuestDirection item : allItems) {
            GSpreadSheetsQuestDirection result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing quest direction rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
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
    public void loadAchievementsFromSpreadSheet(List<GSpreadSheetsAchievement> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsAchievement.class);
        Map<String, GSpreadSheetsAchievement> itemMap = new HashMap<>();
        List<GSpreadSheetsAchievement> allItems = crit.list();
        for (GSpreadSheetsAchievement item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsAchievement item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "achievement {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsAchievement result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for achievement: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for achievement: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsAchievement item : items) {
            GSpreadSheetsAchievement result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting achievement for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsAchievement> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsAchievement item : items) {
            spreadSheetItemMap.put(item.getTextEn(), item);
        }
        for (GSpreadSheetsAchievement item : allItems) {
            GSpreadSheetsAchievement result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing achievement rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadAchievementDescriptionsFromSpreadSheet(List<GSpreadSheetsAchievementDescription> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsAchievementDescription.class);
        Map<String, GSpreadSheetsAchievementDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsAchievementDescription> allItems = crit.list();
        for (GSpreadSheetsAchievementDescription item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsAchievementDescription item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "achievement description {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsAchievementDescription result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for achievement description: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for achievement description: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsAchievementDescription item : items) {
            GSpreadSheetsAchievementDescription result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting achievement description for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsAchievementDescription> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsAchievementDescription item : items) {
            spreadSheetItemMap.put(item.getTextEn(), item);
        }
        for (GSpreadSheetsAchievementDescription item : allItems) {
            GSpreadSheetsAchievementDescription result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing achievement description rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadNotesFromSpreadSheet(List<GSpreadSheetsNote> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNote.class);
        Map<String, GSpreadSheetsNote> itemMap = new HashMap<>();
        List<GSpreadSheetsNote> allItems = crit.list();
        for (GSpreadSheetsNote item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsNote item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "note {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsNote result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for note: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for note: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsNote item : items) {
            GSpreadSheetsNote result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting note for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsNote> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsNote item : items) {
            spreadSheetItemMap.put(item.getTextEn(), item);
        }
        for (GSpreadSheetsNote item : allItems) {
            GSpreadSheetsNote result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing note rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadAbilityDescriptionsFromSpreadSheet(List<GSpreadSheetsAbilityDescription> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsAbilityDescription.class);
        Map<String, GSpreadSheetsAbilityDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsAbilityDescription> allItems = crit.list();
        for (GSpreadSheetsAbilityDescription item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsAbilityDescription item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "ability description {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsAbilityDescription result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for ability description: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for ability description: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsAbilityDescription item : items) {
            GSpreadSheetsAbilityDescription result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting ability description for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsAbilityDescription> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsAbilityDescription item : items) {
            spreadSheetItemMap.put(item.getTextEn(), item);
        }
        for (GSpreadSheetsAbilityDescription item : allItems) {
            GSpreadSheetsAbilityDescription result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing ability description rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
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
            String textEn = npc.getTextEn();
            switch (npc.getSex()) {
                case F:
                    textEn += "^F";
                    break;
                case f:
                    textEn += "^f";
                    break;
                case M:
                    textEn += "^M";
                    break;
                case m:
                    textEn += "^m";
                    break;
                case N:
                    textEn += "^N";
                    break;
                case n:
                    textEn += "^n";
                    break;
            }
            npcsMap.put(textEn, npc);
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
                case f:
                    textEn += "^f";
                    break;
                case M:
                    textEn += "^M";
                    break;
                case m:
                    textEn += "^m";
                    break;
                case N:
                    textEn += "^N";
                    break;
                case n:
                    textEn += "^n";
                    break;
            }
            GSpreadSheetsNpcName result = npcsMap.get(textEn);
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
                if (result.getSex() != npc.getSex()) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "sex changed for npc: {0}", npc.getTextEn());
                    result.setSex(npc.getSex());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsNpcName npc : npcs) {
            String textEn = npc.getTextEn();
            switch (npc.getSex()) {
                case F:
                    textEn += "^F";
                    break;
                case f:
                    textEn += "^f";
                    break;
                case M:
                    textEn += "^M";
                    break;
                case m:
                    textEn += "^m";
                    break;
                case N:
                    textEn += "^N";
                    break;
                case n:
                    textEn += "^n";
                    break;
            }
            GSpreadSheetsNpcName result = npcsMap.get(textEn);
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting npc for rowNum {0}", npc.getRowNum());
                em.persist(npc);
            }
        }

        Map<String, GSpreadSheetsNpcName> spreadSheetNpcsMap = new HashMap<>();
        for (GSpreadSheetsNpcName npc : npcs) {
            String textEn = npc.getTextEn();
            switch (npc.getSex()) {
                case F:
                    textEn += "^F";
                    break;
                case f:
                    textEn += "^f";
                    break;
                case M:
                    textEn += "^M";
                    break;
                case m:
                    textEn += "^m";
                    break;
                case N:
                    textEn += "^N";
                    break;
                case n:
                    textEn += "^n";
                    break;
            }
            spreadSheetNpcsMap.put(textEn, npc);
        }
        for (GSpreadSheetsNpcName npc : allNpcs) {
            String textEn = npc.getTextEn();
            switch (npc.getSex()) {
                case F:
                    textEn += "^F";
                    break;
                case f:
                    textEn += "^f";
                    break;
                case M:
                    textEn += "^M";
                    break;
                case m:
                    textEn += "^m";
                    break;
                case N:
                    textEn += "^N";
                    break;
                case n:
                    textEn += "^n";
                    break;
            }
            GSpreadSheetsNpcName result = spreadSheetNpcsMap.get(textEn);
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
                List<Subtitle> subtitles = subtitleCrit.list();
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
    public HierarchicalContainer getNpcPhrasesDiff(List<GSpreadSheetsNpcPhrase> phrases, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!phrase.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(phrase.getRowNum(), phrase.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getNpcnamessDiff(List<GSpreadSheetsNpcName> names, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!name.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(name.getRowNum(), name.getTextEn(), result.getTextEn());
                }
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
                } else if (name.getSex() != result.getSex()) {
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
    public HierarchicalContainer getLocationNamesDiff(List<GSpreadSheetsLocationName> names, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!name.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(name.getRowNum(), name.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getQuestNamesDiff(List<GSpreadSheetsQuestName> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getQuestDescriptionsDiff(List<GSpreadSheetsQuestDescription> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getQuestDirectionsDiff(List<GSpreadSheetsQuestDirection> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<QuestDirectionsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsQuestDirection.class);
        Map<Long, GSpreadSheetsQuestDirection> itemMap = new HashMap<>();
        List<GSpreadSheetsQuestDirection> allItems = crit.list();
        for (GSpreadSheetsQuestDirection item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsQuestDirection item : items) {
            GSpreadSheetsQuestDirection result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new QuestDirectionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new QuestDirectionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new QuestDirectionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new QuestDirectionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new QuestDirectionsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (QuestDirectionsDiff diff : diffs) {
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
    public HierarchicalContainer getItemNamesDiff(List<GSpreadSheetsItemName> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getItemDescriptionsDiff(List<GSpreadSheetsItemDescription> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getActivatorsDiff(List<GSpreadSheetsActivator> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getAchievementsDiff(List<GSpreadSheetsAchievement> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<AchievementsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsAchievement.class);
        Map<Long, GSpreadSheetsAchievement> itemMap = new HashMap<>();
        List<GSpreadSheetsAchievement> allItems = crit.list();
        for (GSpreadSheetsAchievement item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsAchievement item : items) {
            GSpreadSheetsAchievement result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new AchievementsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new AchievementsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new AchievementsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new AchievementsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new AchievementsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (AchievementsDiff diff : diffs) {
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
    public HierarchicalContainer getAchievementDescriptionsDiff(List<GSpreadSheetsAchievementDescription> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<AchievementDescriptionsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsAchievementDescription.class);
        Map<Long, GSpreadSheetsAchievementDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsAchievementDescription> allItems = crit.list();
        for (GSpreadSheetsAchievementDescription item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsAchievementDescription item : items) {
            GSpreadSheetsAchievementDescription result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new AchievementDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new AchievementDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new AchievementDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new AchievementDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new AchievementDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (AchievementDescriptionsDiff diff : diffs) {
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
    public HierarchicalContainer getNotesDiff(List<GSpreadSheetsNote> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<NotesDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNote.class);
        Map<Long, GSpreadSheetsNote> itemMap = new HashMap<>();
        List<GSpreadSheetsNote> allItems = crit.list();
        for (GSpreadSheetsNote item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsNote item : items) {
            GSpreadSheetsNote result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new NotesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new NotesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new NotesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new NotesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new NotesDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (NotesDiff diff : diffs) {
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
    public HierarchicalContainer getAbilityDescriptionsDiff(List<GSpreadSheetsAbilityDescription> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<AbilityDescriptionsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsAbilityDescription.class);
        Map<Long, GSpreadSheetsAbilityDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsAbilityDescription> allItems = crit.list();
        for (GSpreadSheetsAbilityDescription item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsAbilityDescription item : items) {
            GSpreadSheetsAbilityDescription result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new AbilityDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new AbilityDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new AbilityDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new AbilityDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new AbilityDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (AbilityDescriptionsDiff diff : diffs) {
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
    public HierarchicalContainer getJournalEntriesDiff(List<GSpreadSheetsJournalEntry> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
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
    public HierarchicalContainer getPlayerPhrasesDiff(List<GSpreadSheetsPlayerPhrase> phrases, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
                if (!phrase.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(phrase.getRowNum(), phrase.getTextEn(), result.getTextEn());
                }
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
                if (phrase == null) {
                    phrase = getPlayerPharse(t.getPlayerText().replace("Intimidate ", "").replace("Persuade ", ""));
                }
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
    public BeanItemContainer<Topic> getNpcTopics(Npc npc, BeanItemContainer<Topic> container, TRANSLATE_STATUS translateStatus, SysAccount translator, boolean noTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Topic.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extPlayerPhrase", FetchMode.JOIN);
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("playerTranslations", FetchMode.SELECT);
        crit.setFetchMode("npcTranslations", FetchMode.SELECT);
        if (noTranslations) {
            crit.createAlias("extPlayerPhrase", "extPlayerPhrase");
            crit.createAlias("extNpcPhrase", "extNpcPhrase");
            crit.add(Restrictions.or(
                    Restrictions.eqProperty("extPlayerPhrase.textEn", "extPlayerPhrase.textRu"),
                    Restrictions.eqProperty("extNpcPhrase.textEn", "extNpcPhrase.textRu")
            )
            );
        }
        if (translateStatus != null || (translator != null)) {
            crit.createAlias("playerTranslations", "playerTranslations", JoinType.LEFT_OUTER_JOIN);
            crit.createAlias("npcTranslations", "npcTranslations", JoinType.LEFT_OUTER_JOIN);
        }
        if (translateStatus != null) {
            crit.add(Restrictions.or(
                    Restrictions.eq("playerTranslations.status", translateStatus),
                    Restrictions.eq("npcTranslations.status", translateStatus)
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
    public BeanItemContainer<Greeting> getNpcGreetings(Npc npc, BeanItemContainer<Greeting> container, TRANSLATE_STATUS translateStatus, SysAccount translator, boolean noTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Greeting.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("translations", FetchMode.SELECT);
        if (noTranslations) {
            crit.createAlias("extNpcPhrase", "extNpcPhrase");
            crit.add(
                    Restrictions.eqProperty("extNpcPhrase.textEn", "extNpcPhrase.textRu")
            );
        }
        if (translateStatus != null || (translator != null)) {
            crit.createAlias("translations", "translations");
        }
        if (translateStatus != null) {
            crit.add(Restrictions.sizeGt("translations", 0));
            crit.add(Restrictions.eq("translations.status", translateStatus));
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
    public BeanItemContainer<Subtitle> getNpcSubtitles(Npc npc, BeanItemContainer<Subtitle> container, TRANSLATE_STATUS translateStatus, SysAccount translator, boolean noTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Subtitle.class);
        crit.add(Restrictions.eq("npc", npc));
        crit.setFetchMode("extNpcPhrase", FetchMode.JOIN);
        crit.setFetchMode("translations", FetchMode.SELECT);
        if (noTranslations) {
            crit.createAlias("extNpcPhrase", "extNpcPhrase");
            crit.add(
                    Restrictions.eqProperty("extNpcPhrase.textEn", "extNpcPhrase.textRu")
            );
        }
        if (translateStatus != null || (translator != null)) {
            crit.createAlias("translations", "translations");
        }
        if (translateStatus != null) {
            crit.add(Restrictions.sizeGt("translations", 0));
            crit.add(Restrictions.eq("translations.status", translateStatus));
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
    public BeanItemContainer<Npc> getNpcs(BeanItemContainer<Npc> container, TRANSLATE_STATUS translateStatus, SysAccount translator, boolean noTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Npc.class);
        crit.setFetchMode("location", FetchMode.JOIN);
        if (translateStatus != null && translator != null) {
            Query q = em.createNativeQuery("select npc_id from (select t.npc_id from translatedtext tt join topic t on tt.npctopic_id=t.id where tt.status=:translateStatus and tt.author_id=:authorId\n"
                    + "union all select t.npc_id from translatedtext tt join topic t on tt.playertopic_id=t.id where tt.status=:translateStatus and tt.author_id=:authorId\n"
                    + "union all select t.npc_id from translatedtext tt join greeting t on tt.greeting_id=t.id where tt.status=:translateStatus and tt.author_id=:authorId\n"
                    + "union all select t.npc_id from translatedtext tt join subtitle t on tt.subtitle_id=t.id where tt.status=:translateStatus and tt.author_id=:authorId) as rr group by npc_id");
            q.setParameter("authorId", translator.getId());
            q.setParameter("translateStatus", translateStatus.name());
            List<BigInteger> resultList = q.getResultList();
            List<Long> longResultList = new ArrayList();
            for (BigInteger id : resultList) {
                longResultList.add(id.longValue());
            }
            if (longResultList.isEmpty()) {
                longResultList.add(0L);
            }
            crit.add(
                    Restrictions.in("id", longResultList)
            );
        } else if (translateStatus != null) {
            Query q = em.createNativeQuery("select npc_id from (select t.npc_id from translatedtext tt join topic t on tt.npctopic_id=t.id where tt.status=:translateStatus\n"
                    + "union all select t.npc_id from translatedtext tt join topic t on tt.playertopic_id=t.id where tt.status=:translateStatus\n"
                    + "union all select t.npc_id from translatedtext tt join greeting t on tt.greeting_id=t.id where tt.status=:translateStatus\n"
                    + "union all select t.npc_id from translatedtext tt join subtitle t on tt.subtitle_id=t.id where tt.status=:translateStatus) as rr group by npc_id");
            q.setParameter("translateStatus", translateStatus.name());
            List<BigInteger> resultList = q.getResultList();
            List<Long> longResultList = new ArrayList();
            for (BigInteger id : resultList) {
                longResultList.add(id.longValue());
            }
            if (longResultList.isEmpty()) {
                longResultList.add(0L);
            }
            crit.add(
                    Restrictions.in("id", longResultList)
            );
        } else if (translator != null) {
            Query q = em.createNativeQuery("select npc_id from (select t.npc_id from translatedtext tt join topic t on tt.npctopic_id=t.id where tt.author_id=:authorId\n"
                    + "union all select t.npc_id from translatedtext tt join topic t on tt.playertopic_id=t.id where tt.author_id=:authorId\n"
                    + "union all select t.npc_id from translatedtext tt join greeting t on tt.greeting_id=t.id where tt.author_id=:authorId\n"
                    + "union all select t.npc_id from translatedtext tt join subtitle t on tt.subtitle_id=t.id where tt.author_id=:authorId) as rr group by npc_id");
            q.setParameter("authorId", translator.getId());
            List<BigInteger> resultList = q.getResultList();
            List<Long> longResultList = new ArrayList();
            for (BigInteger id : resultList) {
                longResultList.add(id.longValue());
            }
            if (longResultList.isEmpty()) {
                longResultList.add(0L);
            }
            crit.add(
                    Restrictions.in("id", longResultList)
            );
        }
        if (noTranslations) {
            crit.add(Restrictions.lt("progress", BigDecimal.ONE));
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
                result.setSex(name.getSex());
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
    public void saveQuestDirections(List<GSpreadSheetsQuestDirection> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsQuestDirection item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsQuestDirection.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsQuestDirection result = (GSpreadSheetsQuestDirection) crit.uniqueResult();
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
    public void saveAchievements(List<GSpreadSheetsAchievement> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsAchievement item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsAchievement.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsAchievement result = (GSpreadSheetsAchievement) crit.uniqueResult();
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
    public void saveAchievementDescriptions(List<GSpreadSheetsAchievementDescription> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsAchievementDescription item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsAchievementDescription.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsAchievementDescription result = (GSpreadSheetsAchievementDescription) crit.uniqueResult();
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
    public void saveNotes(List<GSpreadSheetsNote> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsNote item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsNote.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsNote result = (GSpreadSheetsNote) crit.uniqueResult();
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
    public void saveAbilityDescriptions(List<GSpreadSheetsAbilityDescription> items) {
        Session session = (Session) em.getDelegate();
        for (GSpreadSheetsAbilityDescription item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsAbilityDescription.class);
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsAbilityDescription result = (GSpreadSheetsAbilityDescription) crit.uniqueResult();
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
    public void preAcceptTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.PREACCEPTED);
        em.merge(entity);
    }

    @Transactional
    public void rejectTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.REJECTED);
        em.merge(entity);
    }

    @Transactional
    public void acceptTranslatedText(TranslatedText entity) {
        Session session = (Session) em.getDelegate();
        entity.setText(entity.getText().trim().replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("\n", "$"));
        Npc npc = null;
        boolean isSucceeded = false;
        if (entity.getGreeting() != null) {
            npc = entity.getGreeting().getNpc();
            Greeting greeting = em.find(Greeting.class, entity.getGreeting().getId());
            GSpreadSheetsNpcPhrase npcPhrase = greeting.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText());
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
                entity.setApptovedTime(new Date());
                entity.setApprovedBy(SpringSecurityHelper.getSysAccount());
                em.merge(entity);
            }

        } else if (entity.getSubtitle() != null) {
            npc = entity.getSubtitle().getNpc();
            Subtitle subtitle = em.find(Subtitle.class, entity.getSubtitle().getId());
            GSpreadSheetsNpcPhrase npcPhrase = subtitle.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText());
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                isSucceeded = true;
            }

        } else if (entity.getNpcTopic() != null) {
            npc = entity.getNpcTopic().getNpc();
            Topic topic = em.find(Topic.class, entity.getNpcTopic().getId());
            GSpreadSheetsNpcPhrase npcPhrase = topic.getExtNpcPhrase();
            if (npcPhrase != null) {
                npcPhrase.setTextRu(entity.getText());
                npcPhrase.setTranslator(entity.getAuthor().getLogin());
                npcPhrase.setChangeTime(new Date());
                em.merge(npcPhrase);
                isSucceeded = true;
            }

        } else if (entity.getPlayerTopic() != null) {
            npc = entity.getPlayerTopic().getNpc();
            Topic topic = em.find(Topic.class, entity.getPlayerTopic().getId());
            GSpreadSheetsPlayerPhrase playerPhrase = topic.getExtPlayerPhrase();
            if (playerPhrase != null) {
                playerPhrase.setTextRu(entity.getText());
                playerPhrase.setTranslator(entity.getAuthor().getLogin());
                playerPhrase.setChangeTime(new Date());
                em.merge(playerPhrase);
                isSucceeded = true;
            }

        } else if (entity.getSpreadSheetsActivator() != null) {
            GSpreadSheetsActivator gs = em.find(GSpreadSheetsActivator.class, entity.getSpreadSheetsActivator().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsAchievement() != null) {
            GSpreadSheetsAchievement gs = em.find(GSpreadSheetsAchievement.class, entity.getSpreadSheetsAchievement().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsAchievementDescription() != null) {
            GSpreadSheetsAchievementDescription gs = em.find(GSpreadSheetsAchievementDescription.class, entity.getSpreadSheetsAchievementDescription().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsNote() != null) {
            GSpreadSheetsNote gs = em.find(GSpreadSheetsNote.class, entity.getSpreadSheetsNote().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSheetsAbilityDescription() != null) {
            GSpreadSheetsAbilityDescription gs = em.find(GSpreadSheetsAbilityDescription.class, entity.getSheetsAbilityDescription().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsItemDescription() != null) {
            GSpreadSheetsItemDescription gs = em.find(GSpreadSheetsItemDescription.class, entity.getSpreadSheetsItemDescription().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsItemName() != null) {
            GSpreadSheetsItemName gs = em.find(GSpreadSheetsItemName.class, entity.getSpreadSheetsItemName().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsJournalEntry() != null) {
            GSpreadSheetsJournalEntry gs = em.find(GSpreadSheetsJournalEntry.class, entity.getSpreadSheetsJournalEntry().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsLocationName() != null) {
            GSpreadSheetsLocationName gs = em.find(GSpreadSheetsLocationName.class, entity.getSpreadSheetsLocationName().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            Criteria locationCriteria = session.createCriteria(Location.class);
            locationCriteria.add(Restrictions.ilike("name", gs.getTextEn()));
            List<Location> locations = locationCriteria.list();
            for (Location l : locations) {
                l.setNameRu(gs.getTextRu());
                em.merge(l);
            }
            isSucceeded = true;
        } else if (entity.getSpreadSheetsNpcName() != null) {
            GSpreadSheetsNpcName gs = em.find(GSpreadSheetsNpcName.class, entity.getSpreadSheetsNpcName().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            Criteria npcCriteria = session.createCriteria(Npc.class);
            npcCriteria.add(Restrictions.ilike("name", gs.getTextEn()));
            List<Npc> npcs = npcCriteria.list();
            for (Npc n : npcs) {
                n.setNameRu(gs.getTextRu());
                em.merge(n);
            }
            isSucceeded = true;
        } else if (entity.getSpreadSheetsNpcPhrase() != null) {
            GSpreadSheetsNpcPhrase gs = em.find(GSpreadSheetsNpcPhrase.class, entity.getSpreadSheetsNpcPhrase().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsPlayerPhrase() != null) {
            GSpreadSheetsPlayerPhrase gs = em.find(GSpreadSheetsPlayerPhrase.class, entity.getSpreadSheetsPlayerPhrase().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsQuestDescription() != null) {
            GSpreadSheetsQuestDescription gs = em.find(GSpreadSheetsQuestDescription.class, entity.getSpreadSheetsQuestDescription().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsQuestDirection() != null) {
            GSpreadSheetsQuestDirection gs = em.find(GSpreadSheetsQuestDirection.class, entity.getSpreadSheetsQuestDirection().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSpreadSheetsQuestName() != null) {
            GSpreadSheetsQuestName gs = em.find(GSpreadSheetsQuestName.class, entity.getSpreadSheetsQuestName().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            Criteria questCriteria = session.createCriteria(Quest.class);
            questCriteria.add(Restrictions.ilike("name", gs.getTextEn()));
            List<Quest> quests = questCriteria.list();
            for (Quest q : quests) {
                q.setNameRu(gs.getTextRu());
                em.merge(q);
            }
            isSucceeded = true;
        } else if (entity.getEsoInterfaceVariable() != null) {
            EsoInterfaceVariable gs = em.find(EsoInterfaceVariable.class, entity.getEsoInterfaceVariable().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        }
        if (isSucceeded) {
            entity.setStatus(TRANSLATE_STATUS.ACCEPTED);
            entity.setApptovedTime(new Date());
            entity.setApprovedBy(SpringSecurityHelper.getSysAccount());
            em.merge(entity);
        }
        if (npc != null) {
            calculateNpcProgress(npc);
            calculateLocationProgress(npc.getLocation());
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

        }
        float r = 0;
        if (totalPhases > 0) {
            r = (float) translatedPhrases / totalPhases;
        }
        if (r > 0) {
            n.setProgress(new BigDecimal(r).setScale(2, RoundingMode.UP));
        } else if (totalPhases == 0) {
            n.setProgress(BigDecimal.ONE);
        } else {
            n.setProgress(BigDecimal.ZERO);
        }
        em.merge(n);
    }

    @Transactional
    public void calculateLocationProgress(Location l) {
        BigDecimal totalProgress = BigDecimal.ZERO;
        Location loc = em.find(Location.class, l.getId());
        for (Npc npc : loc.getNpcs()) {
            if (npc.getProgress() != null) {
                totalProgress = totalProgress.add(npc.getProgress());
            }
        }
        float r = 0;
        BigDecimal averageProgress = totalProgress.divide(new BigDecimal(loc.getNpcs().size()), 2, RoundingMode.UP);
        loc.setProgress(averageProgress);
        em.merge(loc);
    }

    @Transactional
    public List<Location> getLocations() {
        List<Location> result = null;
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Location.class);
        result = crit.list();
        return result;
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

        Criteria questDirectionCrit = session.createCriteria(GSpreadSheetsQuestDirection.class);
        questDirectionCrit.add(searchTerms);
        List<GSpreadSheetsQuestDirection> questDirectionList = questDirectionCrit.list();
        for (GSpreadSheetsQuestDirection row : questDirectionList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
            item.getItemProperty("weight").setValue(row.getWeight());
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Цель квеста");
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
        Criteria achievementCrit = session.createCriteria(GSpreadSheetsAchievement.class);
        achievementCrit.add(searchTerms);
        List<GSpreadSheetsAchievement> achievementList = achievementCrit.list();
        for (GSpreadSheetsAchievement i : achievementList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("weight").setValue(i.getWeight());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Достижение");
        }
        Criteria achievementDescriptionCrit = session.createCriteria(GSpreadSheetsAchievementDescription.class);
        achievementDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsAchievementDescription> achievementDescriptionList = achievementDescriptionCrit.list();
        for (GSpreadSheetsAchievementDescription i : achievementDescriptionList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("weight").setValue(i.getWeight());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Описание достижения");
        }
        Criteria noteCrit = session.createCriteria(GSpreadSheetsNote.class);
        noteCrit.add(searchTerms);
        List<GSpreadSheetsNote> noteList = noteCrit.list();
        for (GSpreadSheetsNote i : noteList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("weight").setValue(i.getWeight());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Письмо");
        }
        Criteria abilityDescriptionCrit = session.createCriteria(GSpreadSheetsAbilityDescription.class);
        abilityDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsAbilityDescription> abilityDescriptionList = abilityDescriptionCrit.list();
        for (GSpreadSheetsAbilityDescription i : abilityDescriptionList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("weight").setValue(i.getWeight());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Описание способности");
        }
        Criteria esoInterfaceVariableCrit = session.createCriteria(EsoInterfaceVariable.class);
        searchTermitems = new ArrayList<>();
        searchTermitems.add(Restrictions.ilike("textEn", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("textRu", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
        searchTerms = Restrictions.or(searchTermitems.toArray(new Criterion[searchTermitems.size()]));
        esoInterfaceVariableCrit.add(searchTerms);
        List<EsoInterfaceVariable> esoInterfaceVariableList = esoInterfaceVariableCrit.list();
        for (EsoInterfaceVariable i : esoInterfaceVariableList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Строка интерфейса");
        }

        return hc;
    }

    @Transactional
    public HierarchicalContainer getTextForSpellCheck(Date startDate, Date endDate, HierarchicalContainer hc) {
        hc.removeAllItems();
        List<Criterion> searchTermitems = new ArrayList<>();
        if (startDate == null) {
            searchTermitems.add(Restrictions.and(Restrictions.neProperty("textEn", "textRu"), Restrictions.isNull("changeTime")));
        } else {
            searchTermitems.add(Restrictions.and(Restrictions.neProperty("textEn", "textRu"), Restrictions.ge("changeTime", startDate), Restrictions.le("changeTime", endDate)));
        }

        Disjunction searchTerms = Restrictions.or(searchTermitems.toArray(new Criterion[searchTermitems.size()]));
        Session session = (Session) em.getDelegate();

        Criteria itemDescriptionCrit = session.createCriteria(GSpreadSheetsItemDescription.class);
        itemDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsItemDescription> itemDescriptionList = itemDescriptionCrit.list();
        for (GSpreadSheetsItemDescription row : itemDescriptionList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
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
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Описание квеста");
        }

        Criteria questDirectionCrit = session.createCriteria(GSpreadSheetsQuestDirection.class);
        questDirectionCrit.add(searchTerms);
        List<GSpreadSheetsQuestDirection> questDirectionList = questDirectionCrit.list();
        for (GSpreadSheetsQuestDirection row : questDirectionList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
            item.getItemProperty("translator").setValue(row.getTranslator());
            item.getItemProperty("catalogType").setValue("Цель квеста");
        }

        Criteria journalEntryCrit = session.createCriteria(GSpreadSheetsJournalEntry.class);
        journalEntryCrit.add(searchTerms);
        List<GSpreadSheetsJournalEntry> journalEntryList = journalEntryCrit.list();
        for (GSpreadSheetsJournalEntry row : journalEntryList) {
            Item item = hc.addItem(row);
            item.getItemProperty("textEn").setValue(row.getTextEn());
            item.getItemProperty("textRu").setValue(row.getTextRu());
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
        if ((item.getEntity() instanceof GSpreadSheetsNpcName) || (item.getEntity() instanceof GSpreadSheetsLocationName) || (item.getEntity() instanceof GSpreadSheetsNpcPhrase) || (item.getEntity() instanceof GSpreadSheetsPlayerPhrase) || (item.getEntity() instanceof GSpreadSheetsQuestName) || (item.getEntity() instanceof GSpreadSheetsQuestDescription) || (item.getEntity() instanceof GSpreadSheetsActivator) || (item.getEntity() instanceof GSpreadSheetsJournalEntry) || (item.getEntity() instanceof GSpreadSheetsItemName) || (item.getEntity() instanceof GSpreadSheetsItemDescription) || (item.getEntity() instanceof GSpreadSheetsQuestDirection) || (item.getEntity() instanceof EsoInterfaceVariable) || (item.getEntity() instanceof GSpreadSheetsAchievement) || (item.getEntity() instanceof GSpreadSheetsAchievementDescription) || (item.getEntity() instanceof GSpreadSheetsNote) || (item.getEntity() instanceof GSpreadSheetsAbilityDescription)) {
            item.getItemProperty("changeTime").setValue(new Date());
            item.getItemProperty("translator").setValue(SpringSecurityHelper.getSysAccount().getLogin());
            String textRu = (String) item.getItemProperty("textRu").getValue();
            textRu = textRu.trim().replace("\n", "$").replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("  ", " ");
            item.getItemProperty("textRu").setValue(textRu);
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
    public void commitTableEntityItem(Object itemId, String textRu) {
        if ((itemId instanceof GSpreadSheetsNpcName) || (itemId instanceof GSpreadSheetsLocationName) || (itemId instanceof GSpreadSheetsNpcPhrase) || (itemId instanceof GSpreadSheetsPlayerPhrase) || (itemId instanceof GSpreadSheetsQuestName) || (itemId instanceof GSpreadSheetsQuestDescription) || (itemId instanceof GSpreadSheetsActivator) || (itemId instanceof GSpreadSheetsJournalEntry) || (itemId instanceof GSpreadSheetsItemName) || (itemId instanceof GSpreadSheetsItemDescription) || (itemId instanceof GSpreadSheetsQuestDirection) || (itemId instanceof GSpreadSheetsAchievement) || (itemId instanceof GSpreadSheetsAchievementDescription) || (itemId instanceof GSpreadSheetsNote) || (itemId instanceof GSpreadSheetsAbilityDescription)) {
            if (itemId instanceof GSpreadSheetsNpcName) {
                ((GSpreadSheetsNpcName) itemId).setChangeTime(new Date());
                ((GSpreadSheetsNpcName) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
                ((GSpreadSheetsNpcName) itemId).setTextRu(textRu);
            } else if (itemId instanceof GSpreadSheetsLocationName) {
                ((GSpreadSheetsLocationName) itemId).setChangeTime(new Date());
                ((GSpreadSheetsLocationName) itemId).setTextRu(textRu);
                ((GSpreadSheetsLocationName) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsPlayerPhrase) {
                ((GSpreadSheetsPlayerPhrase) itemId).setChangeTime(new Date());
                ((GSpreadSheetsPlayerPhrase) itemId).setTextRu(textRu);
                ((GSpreadSheetsPlayerPhrase) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsNpcPhrase) {
                ((GSpreadSheetsNpcPhrase) itemId).setChangeTime(new Date());
                ((GSpreadSheetsNpcPhrase) itemId).setTextRu(textRu);
                ((GSpreadSheetsNpcPhrase) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsQuestName) {
                ((GSpreadSheetsQuestName) itemId).setChangeTime(new Date());
                ((GSpreadSheetsQuestName) itemId).setTextRu(textRu);
                ((GSpreadSheetsQuestName) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsQuestDescription) {
                ((GSpreadSheetsQuestDescription) itemId).setChangeTime(new Date());
                ((GSpreadSheetsQuestDescription) itemId).setTextRu(textRu);
                ((GSpreadSheetsQuestDescription) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsQuestDirection) {
                ((GSpreadSheetsQuestDirection) itemId).setChangeTime(new Date());
                ((GSpreadSheetsQuestDirection) itemId).setTextRu(textRu);
                ((GSpreadSheetsQuestDirection) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsActivator) {
                ((GSpreadSheetsActivator) itemId).setChangeTime(new Date());
                ((GSpreadSheetsActivator) itemId).setTextRu(textRu);
                ((GSpreadSheetsActivator) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsAchievement) {
                ((GSpreadSheetsAchievement) itemId).setChangeTime(new Date());
                ((GSpreadSheetsAchievement) itemId).setTextRu(textRu);
                ((GSpreadSheetsAchievement) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsAchievementDescription) {
                ((GSpreadSheetsAchievementDescription) itemId).setChangeTime(new Date());
                ((GSpreadSheetsAchievementDescription) itemId).setTextRu(textRu);
                ((GSpreadSheetsAchievementDescription) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsNote) {
                ((GSpreadSheetsNote) itemId).setChangeTime(new Date());
                ((GSpreadSheetsNote) itemId).setTextRu(textRu);
                ((GSpreadSheetsNote) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsAbilityDescription) {
                ((GSpreadSheetsAbilityDescription) itemId).setChangeTime(new Date());
                ((GSpreadSheetsAbilityDescription) itemId).setTextRu(textRu);
                ((GSpreadSheetsAbilityDescription) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsJournalEntry) {
                ((GSpreadSheetsJournalEntry) itemId).setChangeTime(new Date());
                ((GSpreadSheetsJournalEntry) itemId).setTextRu(textRu);
                ((GSpreadSheetsJournalEntry) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsItemName) {
                ((GSpreadSheetsItemName) itemId).setChangeTime(new Date());
                ((GSpreadSheetsItemName) itemId).setTextRu(textRu);
                ((GSpreadSheetsItemName) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsItemDescription) {
                ((GSpreadSheetsItemDescription) itemId).setChangeTime(new Date());
                ((GSpreadSheetsItemDescription) itemId).setTextRu(textRu);
                ((GSpreadSheetsItemDescription) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            }
            em.merge(itemId);
            if (itemId instanceof GSpreadSheetsLocationName) {
                GSpreadSheetsLocationName locationName = (GSpreadSheetsLocationName) itemId;
                Session session = (Session) em.getDelegate();
                Criteria crit = session.createCriteria(Location.class);
                crit.add(Restrictions.ilike("name", locationName.getTextEn()));
                List<Location> list = crit.list();
                for (Location l : list) {
                    l.setNameRu(locationName.getTextRu());
                    em.merge(l);
                }
            }
            if (itemId instanceof GSpreadSheetsQuestName) {
                GSpreadSheetsQuestName questName = (GSpreadSheetsQuestName) itemId;
                Session session = (Session) em.getDelegate();
                Criteria crit = session.createCriteria(Quest.class);
                crit.add(Restrictions.ilike("name", questName.getTextEn()));
                List<Quest> list = crit.list();
                for (Quest q : list) {
                    q.setNameRu(questName.getTextRu());
                    em.merge(q);
                }
            }
            if (itemId instanceof GSpreadSheetsNpcName) {
                GSpreadSheetsNpcName npcName = (GSpreadSheetsNpcName) itemId;
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

    public HierarchicalContainer getStatistics() {
        HierarchicalContainer result = new HierarchicalContainer();
        result.addContainerProperty("name", String.class, null);
        result.addContainerProperty("value", String.class, null);
        Query gdpreadsheetsStatsQuery = em.createNativeQuery("select 'Перевод названий локаций', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetslocationname where texten!=textru union all select null as translated,count(*) as total from gspreadsheetslocationname) as qres union all\n"
                + "select 'Перевод активаторов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsactivator where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsactivator) as qres union all\n"
                + "select 'Перевод достижений', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsachievement where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsachievement) as qres union all\n"
                + "select 'Перевод описаний достижений', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsachievementdescription where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsachievementdescription) as qres union all\n"
                + "select 'Перевод писем', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsnote where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsnote) as qres union all\n"
                + "select 'Перевод описаний способностей', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsabilitydescription where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsabilitydescription) as qres union all\n"
                + "select 'Перевод имён NPC', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsnpcname where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsnpcname) as qres union all\n"
                + "select 'Перевод названий квестов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsquestname where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsquestname) as qres union all\n"
                + "select 'Перевод описаний квестов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsquestdescription where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsquestdescription) as qres union all\n"
                + "select 'Перевод целей квестов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsquestdirection where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsquestdirection) as qres union all\n"
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
        Query preApproveTranslationsQuery = em.createNativeQuery("select count(*) from translatedtext where status='PREACCEPTED'");
        preApproveTranslationsQuery.setMaxResults(1);
        BigInteger preApproveTranslationCount = (BigInteger) preApproveTranslationsQuery.getSingleResult();
        Item preApproveItem = result.addItem(preApproveTranslationCount);
        preApproveItem.getItemProperty("name").setValue("Предварительно вычитанных строк");
        preApproveItem.getItemProperty("value").setValue(Long.toString(preApproveTranslationCount.longValue()));
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
            Query selectQ = em.createNativeQuery("select id,texten from esorawstring where aid=:aid and bid=:bid and cid=:cid");
            selectQ.setParameter("aid", row[0]);
            selectQ.setParameter("bid", row[1]);
            selectQ.setParameter("cid", row[2]);
            List resultList = selectQ.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                Object[] foundRow = (Object[]) resultList.get(0);
                BigInteger foundId = (BigInteger) foundRow[0];
                String foundText = (String) foundRow[1];
                if (foundText == null || !foundText.equals(row[3])) {
                    LOG.log(Level.INFO, "text changed, old:{0} new:{1}", new Object[]{foundText, row[3]});
                    Query updateQ = em.createNativeQuery("update esorawstring set texten=:texten where id=:id");
                    updateQ.setParameter("texten", row[3]);
                    updateQ.setParameter("id", foundId);
                    updateQ.executeUpdate();
                }

            } else {
                Query insertQ = em.createNativeQuery("insert into esorawstring (id,aid,bid,cid,texten) values (nextval('hibernate_sequence'),:aid,:bid,:cid,:texten)");
                insertQ.setParameter("aid", row[0]);
                insertQ.setParameter("bid", row[1]);
                insertQ.setParameter("cid", row[2]);
                insertQ.setParameter("texten", row[3]);
                insertQ.executeUpdate();
            }

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

    @Transactional
    public void addSpellerWord(String word) {
        Session session = (Session) em.getDelegate();
        Criteria c = session.createCriteria(SpellerWord.class);
        c.add(Restrictions.eq("text", word));
        SpellerWord w = (SpellerWord) c.uniqueResult();
        if (w == null) {
            SpellerWord newWord = new SpellerWord();
            newWord.setText(word);
            em.persist(newWord);
        }
    }

    @Transactional
    public boolean isExistSpellerWord(String word) {
        Session session = (Session) em.getDelegate();
        Criteria c = session.createCriteria(SpellerWord.class);
        c.add(Restrictions.eq("text", word));
        SpellerWord w = (SpellerWord) c.uniqueResult();
        if (w != null) {
            return true;
        }
        return false;
    }

    @Transactional
    public void assignSpreadSheetRowsToRawStrings() {

        TypedQuery<GSpreadSheetsActivator> activatorQuery = em.createQuery("select a from GSpreadSheetsActivator a where aId is null", GSpreadSheetsActivator.class);
        List<GSpreadSheetsActivator> activatorList = activatorQuery.getResultList();
        for (GSpreadSheetsActivator item : activatorList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{87370069L, 19398485L, 39619172L, 14464837L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);
            }
        }
        TypedQuery<GSpreadSheetsItemDescription> itemDescriptionQuery = em.createQuery("select a from GSpreadSheetsItemDescription a where aId is null", GSpreadSheetsItemDescription.class);
        List<GSpreadSheetsItemDescription> itemDescriptionList = itemDescriptionQuery.getResultList();
        for (GSpreadSheetsItemDescription item : itemDescriptionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{139139780L, 228378404L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);
            }
        }
        TypedQuery<GSpreadSheetsItemName> itemNameQuery = em.createQuery("select a from GSpreadSheetsItemName a where aId is null", GSpreadSheetsItemName.class);
        List<GSpreadSheetsItemName> itemNameList = itemNameQuery.getResultList();
        for (GSpreadSheetsItemName item : itemNameList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{242841733L, 267697733L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);
            }
        }
        TypedQuery<GSpreadSheetsJournalEntry> journalEntryQuery = em.createQuery("select a from GSpreadSheetsJournalEntry a where aId is null", GSpreadSheetsJournalEntry.class);
        List<GSpreadSheetsJournalEntry> journalEntryList = journalEntryQuery.getResultList();
        for (GSpreadSheetsJournalEntry item : journalEntryList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{103224356L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);
            }
        }
        TypedQuery<GSpreadSheetsLocationName> locationNameQuery = em.createQuery("select a from GSpreadSheetsLocationName a where aId is null", GSpreadSheetsLocationName.class);
        List<GSpreadSheetsLocationName> locationNameList = locationNameQuery.getResultList();
        for (GSpreadSheetsLocationName item : locationNameList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{10860933L, 146361138L, 162946485L, 162658389L, 164009093L, 267200725L, 28666901L, 81344020L, 268015829L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);
            }
        }
        TypedQuery<GSpreadSheetsNpcName> npcNameQuery = em.createQuery("select a from GSpreadSheetsNpcName a where aId is null", GSpreadSheetsNpcName.class);
        npcNameQuery.setMaxResults(1000);
        List<GSpreadSheetsNpcName> npcNameList = npcNameQuery.getResultList();
        for (GSpreadSheetsNpcName item : npcNameList) {
            String textEn = item.getTextEn();
            String textEn2 = item.getTextEn();
            if (item.getSex() != null) {
                switch (item.getSex()) {
                    case U:
                        textEn = item.getTextEn();
                        textEn2 = item.getTextEn();
                        break;
                    case F:
                        textEn = item.getTextEn() + "^F";
                        textEn2 = item.getTextEn() + "^F";
                        break;
                    case f:
                        textEn = item.getTextEn() + "^f";
                        textEn2 = item.getTextEn() + "^f";
                        break;
                    case M:
                        textEn = item.getTextEn() + "^M";
                        textEn2 = item.getTextEn() + "^M";
                        break;
                    case m:
                        textEn = item.getTextEn() + "^m";
                        textEn2 = item.getTextEn() + "^m";
                        break;
                    case N:
                        textEn = item.getTextEn() + "^N";
                        textEn2 = item.getTextEn() + "^N";
                        break;
                    case n:
                        textEn = item.getTextEn() + "^n";
                        textEn2 = item.getTextEn() + "^n";
                        break;
                }
            }
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where (textEn=:textEn or textEn=:textEn2) and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", textEn);
            rawQ.setParameter("textEn2", textEn2);
            rawQ.setParameter("aId", Arrays.asList(new Long[]{8290981L, 191999749L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);
            }
        }
        TypedQuery<GSpreadSheetsNpcPhrase> npcPhraseQuery = em.createQuery("select a from GSpreadSheetsNpcPhrase a where aId is null order by changeTime", GSpreadSheetsNpcPhrase.class);
        npcPhraseQuery.setMaxResults(100);
        List<GSpreadSheetsNpcPhrase> npcPhraseList = npcPhraseQuery.getResultList();
        for (GSpreadSheetsNpcPhrase item : npcPhraseList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{55049764L, 115740052L, 149328292L, 3952276L, 165399380L, 200879108L, 116521668L, 211899940L, 234743124L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);
            }
        }
        TypedQuery<GSpreadSheetsPlayerPhrase> PlayerPhraseQuery = em.createQuery("select a from GSpreadSheetsPlayerPhrase a where aId is null order by changeTime", GSpreadSheetsPlayerPhrase.class);
        PlayerPhraseQuery.setMaxResults(100);
        List<GSpreadSheetsPlayerPhrase> PlayerPhraseList = PlayerPhraseQuery.getResultList();
        for (GSpreadSheetsPlayerPhrase item : PlayerPhraseList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{204987124L, 20958740L, 249936564L, 228103012L, 232026500L, 150525940L, 99155012L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }
        TypedQuery<GSpreadSheetsQuestDescription> GSpreadSheetsQuestQuery = em.createQuery("select a from GSpreadSheetsQuestDescription a where aId is null order by changeTime", GSpreadSheetsQuestDescription.class);
        GSpreadSheetsQuestQuery.setMaxResults(100);
        List<GSpreadSheetsQuestDescription> GSpreadSheetsQuestList = GSpreadSheetsQuestQuery.getResultList();
        for (GSpreadSheetsQuestDescription item : GSpreadSheetsQuestList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{265851556L, 205344756L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }
        TypedQuery<GSpreadSheetsQuestDirection> QuestDirectionQuery = em.createQuery("select a from GSpreadSheetsQuestDirection a where aId is null order by changeTime", GSpreadSheetsQuestDirection.class);
        QuestDirectionQuery.setMaxResults(100);
        List<GSpreadSheetsQuestDirection> QuestDirectionList = QuestDirectionQuery.getResultList();
        for (GSpreadSheetsQuestDirection item : QuestDirectionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{7949764L, 256430276L, 121487972L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }

        TypedQuery<GSpreadSheetsQuestName> QuestNameQuery = em.createQuery("select a from GSpreadSheetsQuestName a where aId is null order by changeTime", GSpreadSheetsQuestName.class);
        QuestNameQuery.setMaxResults(100);
        List<GSpreadSheetsQuestName> QuestNameList = QuestNameQuery.getResultList();
        for (GSpreadSheetsQuestName item : QuestNameList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{52420949L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }

        TypedQuery<GSpreadSheetsAchievement> achievementQuery = em.createQuery("select a from GSpreadSheetsAchievement a where aId is null order by changeTime", GSpreadSheetsAchievement.class);
        achievementQuery.setMaxResults(100);
        List<GSpreadSheetsAchievement> achievementList = achievementQuery.getResultList();
        for (GSpreadSheetsAchievement item : achievementList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{12529189L, 172030117L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }

        TypedQuery<GSpreadSheetsAchievementDescription> achievementDescriptionQuery = em.createQuery("select a from GSpreadSheetsAchievementDescription a where aId is null order by changeTime", GSpreadSheetsAchievementDescription.class);
        achievementDescriptionQuery.setMaxResults(100);
        List<GSpreadSheetsAchievementDescription> achievementDescriptionList = achievementDescriptionQuery.getResultList();
        for (GSpreadSheetsAchievementDescription item : achievementDescriptionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{188155806L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }

        TypedQuery<GSpreadSheetsNote> noteQuery = em.createQuery("select a from GSpreadSheetsNote a where aId is null order by changeTime", GSpreadSheetsNote.class);
        noteQuery.setMaxResults(100);
        List<GSpreadSheetsNote> noteList = noteQuery.getResultList();
        for (GSpreadSheetsNote item : noteList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{219317028L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }
        TypedQuery<GSpreadSheetsAbilityDescription> abilityDescriptionQuery = em.createQuery("select a from GSpreadSheetsAbilityDescription a where aId is null order by changeTime", GSpreadSheetsAbilityDescription.class);
        //abilityDescriptionQuery.setMaxResults(100);
        List<GSpreadSheetsAbilityDescription> abilityDescriptionList = abilityDescriptionQuery.getResultList();
        for (GSpreadSheetsAbilityDescription item : abilityDescriptionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) order by aId,cId", EsoRawString.class);
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{132143172L}));
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());
            }
        }
    }

    @Transactional
    public void setAidBidCid(DAO e, Long aId, Long bId, Long cId) {
        em.refresh(e);
        setField(e, "aId", aId);
        setField(e, "bId", bId);
        setField(e, "cId", cId);
        em.merge(e);
    }

    private void setField(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        try {
            java.lang.reflect.Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (NoSuchFieldException e) {
            clazz = clazz.getSuperclass();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Transactional
    public void importInterfaceStrings(List<EsoInterfaceVariable> list) {
        for (EsoInterfaceVariable i : list) {
            TypedQuery<EsoInterfaceVariable> q = em.createQuery("select v from EsoInterfaceVariable v where name=:name", EsoInterfaceVariable.class);
            q.setParameter("name", i.getName());
            try {
                EsoInterfaceVariable r = q.getSingleResult();
                if (!r.getTextEn().equals(i.getTextEn())) {
                    r.setTextEn(i.getTextEn());
                    r.setChanged(Boolean.TRUE);
                    em.merge(r);
                }
            } catch (javax.persistence.NoResultException ex) {
                i.setChanged(Boolean.TRUE);
                em.persist(i);
            }
        }
    }

    @Transactional
    public void importRuInterfaceStrings(List<EsoInterfaceVariable> list) {
        for (EsoInterfaceVariable i : list) {
            TypedQuery<EsoInterfaceVariable> q = em.createQuery("select v from EsoInterfaceVariable v where name=:name", EsoInterfaceVariable.class);
            q.setParameter("name", i.getName());
            try {
                EsoInterfaceVariable r = q.getSingleResult();
                r.setTextRu(i.getTextRu());
                em.merge(r);
            } catch (javax.persistence.NoResultException ex) {

            }
        }
    }

    @Transactional
    public void updateGspreadSheetTextEn() {
        List<Object[]> resultList;
        Query activatorsQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsactivator g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = activatorsQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsactivator set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query itemDescriptionsQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsitemdescription g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = itemDescriptionsQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsitemdescription set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query itemNamesQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsitemname g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = itemNamesQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsitemname set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetsjournalentryQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsjournalentry g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetsjournalentryQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsjournalentry set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetslocationnameQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetslocationname g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetslocationnameQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetslocationname set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetsnpcnameQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru,g.sex from gspreadsheetsnpcname g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetsnpcnameQ.getResultList();
        Pattern MalePattern = Pattern.compile("\\^[M]");
        Pattern malePattern = Pattern.compile("\\^[m]");
        Pattern FemalePattern = Pattern.compile("\\^[F]");
        Pattern femalePattern = Pattern.compile("\\^[f]");
        Pattern NPattern = Pattern.compile("\\^[N]");
        Pattern nPattern = Pattern.compile("\\^[n]");
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            String sex = ((String) row[4]);
            NPC_SEX gSex = NPC_SEX.valueOf(sex);
            NPC_SEX eSex = NPC_SEX.U;

            Matcher MaleMatcher = MalePattern.matcher(eTextEn);
            if (MaleMatcher.find()) {
                eSex = NPC_SEX.M;
                eTextEn = eTextEn.replaceAll("\\^M", "").replaceAll("\\^m", "");
            }
            Matcher maleMatcher = malePattern.matcher(eTextEn);
            if (maleMatcher.find()) {
                eSex = NPC_SEX.m;
                eTextEn = eTextEn.replaceAll("\\^M", "").replaceAll("\\^m", "");
            }
            Matcher FemaleMatcher = FemalePattern.matcher(eTextEn);
            if (FemaleMatcher.find()) {
                eSex = NPC_SEX.F;
                eTextEn = eTextEn.replaceAll("\\^F", "").replaceAll("\\^f", "");
            }
            Matcher femaleMatcher = femalePattern.matcher(eTextEn);
            if (femaleMatcher.find()) {
                eSex = NPC_SEX.f;
                eTextEn = eTextEn.replaceAll("\\^F", "").replaceAll("\\^f", "");
            }
            Matcher NMatcher = NPattern.matcher(eTextEn);
            if (NMatcher.find()) {
                eSex = NPC_SEX.N;
                eTextEn = eTextEn.replaceAll("\\^N", "").replaceAll("\\^n", "");
            }
            Matcher nMatcher = nPattern.matcher(eTextEn);
            if (nMatcher.find()) {
                eSex = NPC_SEX.n;
                eTextEn = eTextEn.replaceAll("\\^N", "").replaceAll("\\^n", "");
            }
            if (!gTextEn.equals(eTextEn) || eSex != gSex) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsnpcname set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null,sex=:sex where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.setParameter("sex", eSex.name());
                updateQ.executeUpdate();
            }
        }

        Query gspreadsheetsnpcphraseQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsnpcphrase g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetsnpcphraseQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsnpcphrase set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetsplayerphraseQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsplayerphrase g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetsplayerphraseQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsplayerphrase set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetsquestdescriptionQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsquestdescription g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetsquestdescriptionQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsquestdescription set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetsquestdirectionQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsquestdirection g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetsquestdirectionQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsquestdirection set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetsquestnameQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsquestname g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetsquestnameQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsquestname set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetachievementQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsachievement g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetachievementQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsachievement set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetachievementDescriptionQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsachievementdescription g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetachievementDescriptionQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsachievementdescription set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetnodesQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsnote g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetnodesQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsnote set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetAbilityDescriptionsQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsabilitydescription g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetAbilityDescriptionsQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsabilitydescription set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
    }

    @Transactional
    public void assignActivatorsToItems() {
        Query activatorToItemQ = em.createNativeQuery("select i.id,a.textru,a.translator from gspreadsheetsactivator a join gspreadsheetsitemname i on a.texten=i.texten where a.texten!=a.textru and i.texten=i.textru");
        List<Object[]> activatorToItemR = activatorToItemQ.getResultList();
        for (Object[] row : activatorToItemR) {
            BigInteger id = (BigInteger) row[0];
            String textru = (String) row[1];
            String translator = (String) row[2];
            Date changeTime = new Date();
            if (translator == null || translator.isEmpty()) {
                translator = "?";
            }
            GSpreadSheetsItemName r = em.find(GSpreadSheetsItemName.class, id.longValue());
            r.setTextRu(textru);
            r.setTranslator(translator);
            r.setChangeTime(changeTime);
            em.merge(r);
        }
        Query itemToActivatorQ = em.createNativeQuery("select a.id,i.textru,i.translator from gspreadsheetsactivator a join gspreadsheetsitemname i on a.texten=i.texten where i.texten!=i.textru and a.texten=a.textru");
        List<Object[]> itemToActivatorR = itemToActivatorQ.getResultList();
        for (Object[] row : itemToActivatorR) {
            BigInteger id = (BigInteger) row[0];
            String textru = (String) row[1];
            String translator = (String) row[2];
            Date changeTime = new Date();
            if (translator == null || translator.isEmpty()) {
                translator = "?";
            }
            GSpreadSheetsActivator r = em.find(GSpreadSheetsActivator.class, id.longValue());
            r.setTextRu(textru);
            r.setTranslator(translator);
            r.setChangeTime(changeTime);
            em.merge(r);
        }
        Query activatorToLocationQ = em.createNativeQuery("select i.id,a.textru,a.translator from gspreadsheetsactivator a join gspreadsheetslocationname i on a.texten=i.texten where a.texten!=a.textru and i.texten=i.textru");
        List<Object[]> activatorToLocationR = activatorToLocationQ.getResultList();
        for (Object[] row : activatorToLocationR) {
            BigInteger id = (BigInteger) row[0];
            String textru = (String) row[1];
            String translator = (String) row[2];
            Date changeTime = new Date();
            if (translator == null || translator.isEmpty()) {
                translator = "?";
            }
            GSpreadSheetsLocationName r = em.find(GSpreadSheetsLocationName.class, id.longValue());
            r.setTextRu(textru);
            r.setTranslator(translator);
            r.setChangeTime(changeTime);
            em.merge(r);
        }
        Query locationToActivatorQ = em.createNativeQuery("select a.id,i.textru,i.translator from gspreadsheetsactivator a join gspreadsheetslocationname i on a.texten=i.texten where i.texten!=i.textru and a.texten=a.textru");
        List<Object[]> locationToActivatorR = locationToActivatorQ.getResultList();
        for (Object[] row : locationToActivatorR) {
            BigInteger id = (BigInteger) row[0];
            String textru = (String) row[1];
            String translator = (String) row[2];
            Date changeTime = new Date();
            if (translator == null || translator.isEmpty()) {
                translator = "?";
            }
            GSpreadSheetsActivator r = em.find(GSpreadSheetsActivator.class, id.longValue());
            r.setTextRu(textru);
            r.setTranslator(translator);
            r.setChangeTime(changeTime);
            em.merge(r);
        }
    }

    @Transactional
    public void newFormatImportNpcs(JSONObject source) {
        Session session = (Session) em.getDelegate();
        JSONObject npcLocationObject = source.getJSONObject("npc");
        Iterator locationsKeys = npcLocationObject.keys();
        while (locationsKeys.hasNext()) {
            String locationName = (String) locationsKeys.next();
            Criteria sheetLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
            sheetLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", locationName), Restrictions.ilike("textRu", locationName)));
            List<GSpreadSheetsLocationName> list = sheetLocationCrit.list();
            if (list != null && !list.isEmpty()) {
                GSpreadSheetsLocationName sheetsLocationName = list.get(0);
                Criteria locationCrit = session.createCriteria(Location.class);
                locationCrit.add(Restrictions.or(Restrictions.ilike("name", sheetsLocationName.getTextEn()), Restrictions.ilike("nameRu", sheetsLocationName.getTextRu())));
                List<Location> locations = locationCrit.list();
                Location location = null;
                if (locations != null && !locations.isEmpty()) {
                    location = locations.get(0);
                    if (EsnDecoder.IsRu(sheetsLocationName.getTextRu())) {
                        location.setNameRu(sheetsLocationName.getTextRu());
                    }
                } else {
                    location = new Location();
                    location.setProgress(BigDecimal.ZERO);

                }
                if (sheetsLocationName != null) {
                    location.setSheetsLocationName(sheetsLocationName);
                    location.setName(sheetsLocationName.getTextEn());
                }
                if (location.getId() == null) {
                    LOG.log(Level.INFO, "new location: {0}", location.toString());
                    em.persist(location);
                } else {
                    LOG.log(Level.INFO, "update location: {0}", location.toString());
                    em.merge(location);
                }

                JSONObject npcsObject = npcLocationObject.getJSONObject(locationName);
                Iterator npcsKeys = npcsObject.keys();
                while (npcsKeys.hasNext()) {
                    String npcKey = (String) npcsKeys.next();
                    String npcName = null;
                    String npcNameRu = null;
                    if (EsnDecoder.IsRu(npcKey)) {
                        npcNameRu = EsnDecoder.decode(npcKey);
                    } else {
                        npcName = npcKey;
                    }
                    GSpreadSheetsNpcName sheetNpc = null;
                    if (npcName == null) {
                        Criteria sheetNpcCrit = session.createCriteria(GSpreadSheetsNpcName.class);
                        sheetNpcCrit.add(Restrictions.ilike("textRu", npcNameRu));
                        List<GSpreadSheetsNpcName> sheetNpcList = sheetNpcCrit.list();
                        if (sheetNpcList != null && !sheetNpcList.isEmpty()) {
                            sheetNpc = sheetNpcList.get(0);
                            npcName = sheetNpc.getTextEn();
                        }
                    } else {
                        Criteria sheetNpcCrit = session.createCriteria(GSpreadSheetsNpcName.class);
                        sheetNpcCrit.add(Restrictions.ilike("textEn", npcName));
                        List<GSpreadSheetsNpcName> sheetNpcList = sheetNpcCrit.list();
                        if (sheetNpcList != null && !sheetNpcList.isEmpty()) {
                            sheetNpc = sheetNpcList.get(0);
                            if (EsnDecoder.IsRu(sheetNpc.getTextRu())) {
                                npcNameRu = sheetNpc.getTextRu();
                            }
                        }
                    }
                    Criteria npcCriteria = session.createCriteria(Npc.class);
                    npcCriteria.add(Restrictions.eq("location", location));
                    /*if (sheetNpc != null) {
                     npcCriteria.add(Restrictions.eq("sheetsNpcName", sheetNpc));
                     } else*/ if (npcName != null) {
                        npcCriteria.add(Restrictions.ilike("name", npcName));
                    } else if (npcNameRu != null) {
                        npcCriteria.add(Restrictions.ilike("nameRu", npcNameRu));
                    }
                    Npc currentNpc = null;
                    List<Npc> npcList = npcCriteria.list();
                    if (npcList != null && !npcList.isEmpty()) {
                        currentNpc = npcList.get(0);

                    } else {
                        currentNpc = new Npc();
                        currentNpc.setLocation(location);
                        if (npcName != null) {
                            currentNpc.setName(npcName);
                        }
                        if (npcNameRu != null) {
                            currentNpc.setNameRu(npcNameRu);
                        }
                    }
                    if (sheetNpc != null) {
                        currentNpc.setSheetsNpcName(sheetNpc);
                        currentNpc.setSex(sheetNpc.getSex());
                    }
                    if (currentNpc.getId() == null) {
                        LOG.log(Level.INFO, "new npc: {0}", currentNpc.toString());
                        em.persist(currentNpc);
                    } else {
                        LOG.log(Level.INFO, "update npc: {0}", currentNpc.toString());
                        em.merge(currentNpc);
                    }
                    JSONObject npcContent = npcsObject.getJSONObject(npcKey);
                    JSONObject topicsObject = null;
                    try {
                        topicsObject = npcContent.getJSONObject("topics");
                    } catch (JSONException ex) {

                    }
                    if (topicsObject != null) {
                        Iterator topicsKeys = topicsObject.keys();
                        while (topicsKeys.hasNext()) {
                            String topickey = (String) topicsKeys.next();
                            String playerText = null;
                            String playerTextRu = null;
                            String npcText = null;
                            String npcTextRu = null;
                            if (EsnDecoder.IsRu(topickey)) {
                                playerTextRu = topickey;
                            } else {
                                playerText = topickey;
                            }
                            if (EsnDecoder.IsRu(topicsObject.getString(topickey))) {
                                npcTextRu = topicsObject.getString(topickey);
                            } else {
                                npcText = topicsObject.getString(topickey);
                            }
                            if ((playerText != null&&!playerText.isEmpty()) || (npcText != null&&!npcText.isEmpty())) {
                                Criteria topicCriteria = session.createCriteria(Topic.class);
                                topicCriteria.add(Restrictions.eq("npc", currentNpc));
                                if (playerText != null) {
                                    topicCriteria.add(Restrictions.ilike("playerText", playerText));
                                }
                                if (playerTextRu != null) {
                                    topicCriteria.add(Restrictions.ilike("playerTextRu", playerTextRu));
                                }
                                if (npcText != null) {
                                    topicCriteria.add(Restrictions.or(Restrictions.ilike("npcText", npcText), Restrictions.isNull("npcText")));
                                }
                                if (npcTextRu != null) {
                                    topicCriteria.add(Restrictions.or(Restrictions.ilike("npcTextRu", npcTextRu), Restrictions.isNull("npcTextRu")));
                                }
                                List<Topic> topicList = topicCriteria.list();
                                if (topicList != null && !topicList.isEmpty()) {
                                    Topic topic = topicList.get(0);
                                    if (topic.getNpcText() == null && npcText != null) {
                                        topic.setNpcText(npcText);
                                        LOG.log(Level.INFO, "update topic: {0}|{1}|{2}|{3}", new String[]{playerText, npcText, playerTextRu, npcTextRu});
                                        em.merge(topic);
                                    }
                                    if (topic.getNpcTextRu() == null && npcTextRu != null) {
                                        topic.setNpcTextRu(npcTextRu);
                                        LOG.log(Level.INFO, "update topic: {0}|{1}|{2}|{3}", new String[]{playerText, npcText, playerTextRu, npcTextRu});
                                        em.merge(topic);
                                    }
                                } else if (playerText != null || npcText != null) {
                                    Topic topic = new Topic(playerText, npcText, playerTextRu, npcTextRu, currentNpc);
                                    LOG.log(Level.INFO, "new topic: {0}|{1}|{2}|{3}", new String[]{playerText, npcText, playerTextRu, npcTextRu});
                                    em.persist(topic);
                                }
                            }

                        }
                    }

                    JSONObject subtitlesObject = null;
                    try {
                        subtitlesObject = npcContent.getJSONObject("subtitle");
                    } catch (JSONException ex) {

                    }
                    if (subtitlesObject != null) {
                        Iterator subtitlesKeys = subtitlesObject.keys();
                        while (subtitlesKeys.hasNext()) {
                            String subtitlekey = (String) subtitlesKeys.next();
                            String subtitleText = null;
                            String subtitleTextRu = null;
                            if (EsnDecoder.IsRu(subtitlekey)) {
                                subtitleTextRu = subtitlekey;
                            } else {
                                subtitleText = subtitlekey;
                            }
                            if(subtitleText != null&&!subtitleText.isEmpty()) {
                                Criteria subtitleCriteria = session.createCriteria(Subtitle.class);
                            subtitleCriteria.add(Restrictions.eq("npc", currentNpc));
                            if (subtitleText != null) {
                                subtitleCriteria.add(Restrictions.eq("text", subtitleText));
                            }
                            if (subtitleTextRu != null) {
                                subtitleCriteria.add(Restrictions.eq("text", subtitleTextRu));
                            }
                            List<Subtitle> subtitleList = subtitleCriteria.list();
                            if (subtitleList == null || subtitleList.isEmpty()) {
                                Subtitle subtitle = new Subtitle(subtitleText, subtitleTextRu, currentNpc);
                                LOG.log(Level.INFO, "new subtitle: {0}|{1}", new String[]{subtitleText, subtitleTextRu});
                                em.persist(subtitle);
                            }
                            }
                            
                        }
                    }

                    JSONObject greetingsObject = null;
                    try {
                        greetingsObject = npcContent.getJSONObject("greetings");
                    } catch (JSONException ex) {

                    }
                    if (greetingsObject != null) {
                        Iterator greetingsKeys = greetingsObject.keys();
                        while (greetingsKeys.hasNext()) {
                            String greetingskey = (String) greetingsKeys.next();
                            String greetingText = null;
                            String greetingTextRu = null;
                            if (EsnDecoder.IsRu(greetingsObject.getString(greetingskey))) {
                                greetingTextRu = greetingsObject.getString(greetingskey);
                            } else {
                                greetingText = greetingsObject.getString(greetingskey);
                            }
                            if (greetingText != null&&!greetingText.isEmpty()) {
                                Criteria greetingsCriteria = session.createCriteria(Greeting.class);
                                greetingsCriteria.add(Restrictions.eq("npc", currentNpc));
                                if (greetingText != null) {
                                    greetingsCriteria.add(Restrictions.eq("text", greetingText));
                                }
                                if (greetingTextRu != null) {
                                    greetingsCriteria.add(Restrictions.eq("text", greetingTextRu));
                                }
                                List<Greeting> greetingList = greetingsCriteria.list();
                                if (greetingList == null || greetingList.isEmpty()) {
                                    Greeting greeting = new Greeting(greetingskey, greetingText, greetingTextRu, currentNpc);
                                    LOG.log(Level.INFO, "new greeting: {0}|{1}", new String[]{greetingText, greetingTextRu});
                                    em.persist(greeting);
                                }
                            }

                        }
                    }

                }
            }

        }

    }

}
