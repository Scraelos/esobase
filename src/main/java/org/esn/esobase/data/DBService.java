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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.esn.esobase.data.diffs.AbilityDescriptionsDiff;
import org.esn.esobase.data.diffs.AchievementDescriptionsDiff;
import org.esn.esobase.data.diffs.AchievementsDiff;
import org.esn.esobase.data.diffs.CollectibleDescriptionsDiff;
import org.esn.esobase.data.diffs.CollectiblesDiff;
import org.esn.esobase.data.diffs.LoadscreensDiff;
import org.esn.esobase.data.diffs.NotesDiff;
import org.esn.esobase.data.repository.BookRepository;
import org.esn.esobase.data.repository.GSpreadSheetsAbilityDescriptionRepository;
import org.esn.esobase.data.repository.GSpreadSheetsAchievementDescriptionRepository;
import org.esn.esobase.data.repository.GSpreadSheetsAchievementRepository;
import org.esn.esobase.data.repository.GSpreadSheetsActivatorRepository;
import org.esn.esobase.data.repository.GSpreadSheetsCollectibleDescriptionRepository;
import org.esn.esobase.data.repository.GSpreadSheetsCollectibleRepository;
import org.esn.esobase.data.repository.GSpreadSheetsItemDescriptionRepository;
import org.esn.esobase.data.repository.GSpreadSheetsItemNameRepository;
import org.esn.esobase.data.repository.GSpreadSheetsJournalEntryRepository;
import org.esn.esobase.data.repository.GSpreadSheetsLoadscreenRepository;
import org.esn.esobase.data.repository.GSpreadSheetsLocationNameRepository;
import org.esn.esobase.data.repository.GSpreadSheetsNoteRepository;
import org.esn.esobase.data.repository.GSpreadSheetsNpcNameRepository;
import org.esn.esobase.data.repository.GSpreadSheetsNpcPhraseRepository;
import org.esn.esobase.data.repository.GSpreadSheetsPlayerPhraseRepository;
import org.esn.esobase.data.repository.GSpreadSheetsQuestDescriptionRepository;
import org.esn.esobase.data.repository.GSpreadSheetsQuestDirectionRepository;
import org.esn.esobase.data.repository.GSpreadSheetsQuestNameRepository;
import org.esn.esobase.data.repository.LocationRepository;
import org.esn.esobase.data.repository.NpcRepository;
import org.esn.esobase.data.repository.SubtitleRepository;
import org.esn.esobase.data.repository.TopicRepository;
import org.esn.esobase.data.repository.TranslatedTextRepository;
import org.esn.esobase.data.specification.SubtitleSpecification;
import org.esn.esobase.data.specification.TopicSpecification;
import org.esn.esobase.model.Book;
import org.esn.esobase.model.BookText;
import org.esn.esobase.model.EsoInterfaceVariable;
import org.esn.esobase.model.EsoRawString;
import org.esn.esobase.model.GSpreadSheetEntity;
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
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.Greeting;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.NPC_SEX;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.QuestDirection;
import org.esn.esobase.model.QuestStep;
import org.esn.esobase.model.SpellerWord;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.SysAccountRole;
import org.esn.esobase.model.SystemProperty;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.Topic;
import org.esn.esobase.model.TranslatedEntity;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.model.lib.DAO;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.tools.EsnDecoder;
import org.esn.esobase.tools.GSpreadSheetLinkRouter;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
public class DBService {

    @Autowired
    private GSpreadSheetsNpcNameRepository gSpreadSheetsNpcNameRepository;
    @Autowired
    private GSpreadSheetsLocationNameRepository gSpreadSheetsLocationNameRepository;
    @Autowired
    private GSpreadSheetsActivatorRepository gSpreadSheetsActivatorRepository;
    @Autowired
    private GSpreadSheetsPlayerPhraseRepository gSpreadSheetsPlayerPhraseRepository;
    @Autowired
    private GSpreadSheetsNpcPhraseRepository gSpreadSheetsNpcPhraseRepository;
    @Autowired
    private GSpreadSheetsQuestNameRepository gSpreadSheetsQuestNameRepository;
    @Autowired
    private GSpreadSheetsQuestDescriptionRepository gSpreadSheetsQuestDescriptionRepository;
    @Autowired
    private GSpreadSheetsQuestDirectionRepository gSpreadSheetsQuestDirectionRepository;
    @Autowired
    private GSpreadSheetsItemNameRepository gSpreadSheetsItemNameRepository;
    @Autowired
    private GSpreadSheetsItemDescriptionRepository gSpreadSheetsItemDescriptionRepository;
    @Autowired
    private GSpreadSheetsJournalEntryRepository gSpreadSheetsJournalEntryRepository;
    @Autowired
    private GSpreadSheetsAchievementRepository gSpreadSheetsAchievementRepository;
    @Autowired
    private GSpreadSheetsAchievementDescriptionRepository gSpreadSheetsAchievementDescriptionRepository;
    @Autowired
    private GSpreadSheetsAbilityDescriptionRepository gSpreadSheetsAbilityDescriptionRepository;
    @Autowired
    private GSpreadSheetsNoteRepository gSpreadSheetsNoteRepository;
    @Autowired
    private TranslatedTextRepository translatedTextRepository;
    @Autowired
    private GSpreadSheetsLoadscreenRepository gSpreadSheetsLoadscreenRepository;
    @Autowired
    private GSpreadSheetsCollectibleRepository gSpreadSheetsCollectibleRepository;
    @Autowired
    private GSpreadSheetsCollectibleDescriptionRepository gSpreadSheetsCollectibleDescriptionRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private NpcRepository npcRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private SubtitleRepository subtitleRepository;

    public NpcRepository getNpcRepository() {
        return npcRepository;
    }

    public LocationRepository getLocationRepository() {
        return locationRepository;
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public GSpreadSheetsNpcNameRepository getgSpreadSheetsNpcNameRepository() {
        return gSpreadSheetsNpcNameRepository;
    }

    public GSpreadSheetsLocationNameRepository getgSpreadSheetsLocationNameRepository() {
        return gSpreadSheetsLocationNameRepository;
    }

    public GSpreadSheetsActivatorRepository getgSpreadSheetsActivatorRepository() {
        return gSpreadSheetsActivatorRepository;
    }

    public GSpreadSheetsPlayerPhraseRepository getgSpreadSheetsPlayerPhraseRepository() {
        return gSpreadSheetsPlayerPhraseRepository;
    }

    public GSpreadSheetsNpcPhraseRepository getgSpreadSheetsNpcPhraseRepository() {
        return gSpreadSheetsNpcPhraseRepository;
    }

    public GSpreadSheetsQuestNameRepository getgSpreadSheetsQuestNameRepository() {
        return gSpreadSheetsQuestNameRepository;
    }

    public GSpreadSheetsQuestDescriptionRepository getgSpreadSheetsQuestDescriptionRepository() {
        return gSpreadSheetsQuestDescriptionRepository;
    }

    public GSpreadSheetsQuestDirectionRepository getgSpreadSheetsQuestDirectionRepository() {
        return gSpreadSheetsQuestDirectionRepository;
    }

    public GSpreadSheetsItemNameRepository getgSpreadSheetsItemNameRepository() {
        return gSpreadSheetsItemNameRepository;
    }

    public GSpreadSheetsItemDescriptionRepository getgSpreadSheetsItemDescriptionRepository() {
        return gSpreadSheetsItemDescriptionRepository;
    }

    public GSpreadSheetsAchievementRepository getgSpreadSheetsAchievementRepository() {
        return gSpreadSheetsAchievementRepository;
    }

    public GSpreadSheetsJournalEntryRepository getgSpreadSheetsJournalEntryRepository() {
        return gSpreadSheetsJournalEntryRepository;
    }

    public GSpreadSheetsAchievementDescriptionRepository getgSpreadSheetsAchievementDescriptionRepository() {
        return gSpreadSheetsAchievementDescriptionRepository;
    }

    public GSpreadSheetsAbilityDescriptionRepository getgSpreadSheetsAbilityDescriptionRepository() {
        return gSpreadSheetsAbilityDescriptionRepository;
    }

    public GSpreadSheetsNoteRepository getgSpreadSheetsNoteRepository() {
        return gSpreadSheetsNoteRepository;
    }

    public TranslatedTextRepository getTranslatedTextRepository() {
        return translatedTextRepository;
    }

    public GSpreadSheetsLoadscreenRepository getgSpreadSheetsLoadscreenRepository() {
        return gSpreadSheetsLoadscreenRepository;
    }

    public GSpreadSheetsCollectibleRepository getgSpreadSheetsCollectibleRepository() {
        return gSpreadSheetsCollectibleRepository;
    }

    public GSpreadSheetsCollectibleDescriptionRepository getgSpreadSheetsCollectibleDescriptionRepository() {
        return gSpreadSheetsCollectibleDescriptionRepository;
    }

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
        roles.add(new SysAccountRole(25L, "ROLE_CORRECTOR", "Коррекция текста"));
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
                            /*for (Greeting greeting : npc.getGreetings()) {
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
                            }*/
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
            phrasesMap.put(phrase.getTextEn().toLowerCase(), phrase);
        }
        int total = phrases.size();
        int count = 0;
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "phrase {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsPlayerPhrase result = phrasesMap.get(phrase.getTextEn().toLowerCase());
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
                if (!phrase.getTextEn().equals(result.getTextEn())) {
                    result.setTextEn(phrase.getTextEn());
                    result.setaId(null);
                    result.setbId(null);
                    result.setcId(null);
                    isMerge = true;
                }
                if (isMerge) {

                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            GSpreadSheetsPlayerPhrase result = phrasesMap.get(phrase.getTextEn().toLowerCase());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting phrase for rowNum {0}", phrase.getRowNum());
                em.persist(phrase);
            }
        }

        Map<String, GSpreadSheetsPlayerPhrase> spreadSheetPhrasesMap = new HashMap<>();
        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            spreadSheetPhrasesMap.put(phrase.getTextEn().toLowerCase(), phrase);
        }
        for (GSpreadSheetsPlayerPhrase phrase : allPhrases) {
            GSpreadSheetsPlayerPhrase result = spreadSheetPhrasesMap.get(phrase.getTextEn().toLowerCase());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing phrase rownum={0} :{1}", new Object[]{phrase.getRowNum(), phrase.getTextEn()});
                Criteria playerTopicCrit = session.createCriteria(Topic.class);
                playerTopicCrit.add(Restrictions.eq("extPlayerPhrase", phrase));
                List<Topic> topics = playerTopicCrit.list();
                for (Topic t : topics) {
                    t.setExtPlayerPhrase(null);
                    em.merge(t);
                }
                for (TranslatedText t : phrase.getTranslatedTexts()) {
                    t.setSpreadSheetsPlayerPhrase(null);
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
                for (TranslatedText t : location.getTranslatedTexts()) {
                    t.setSpreadSheetsLocationName(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsQuestName(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsQuestDescription(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsQuestDirection(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsItemName(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsItemDescription(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsActivator(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsAchievement(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsAchievementDescription(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsNote(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSheetsAbilityDescription(null);
                    em.merge(t);
                }
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadCollectiblesFromSpreadSheet(List<GSpreadSheetsCollectible> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsCollectible.class);
        Map<String, GSpreadSheetsCollectible> itemMap = new HashMap<>();
        List<GSpreadSheetsCollectible> allItems = crit.list();
        for (GSpreadSheetsCollectible item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsCollectible item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "collectible {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsCollectible result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for collectible: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for collectible: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsCollectible item : items) {
            GSpreadSheetsCollectible result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting collectible for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsCollectible> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsCollectible item : items) {
            spreadSheetItemMap.put(item.getTextEn(), item);
        }
        for (GSpreadSheetsCollectible item : allItems) {
            GSpreadSheetsCollectible result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing collectible rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSheetsCollectible(null);
                    em.merge(t);
                }
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadCollectibleDescriptionsFromSpreadSheet(List<GSpreadSheetsCollectibleDescription> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsCollectibleDescription.class);
        Map<String, GSpreadSheetsCollectibleDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsCollectibleDescription> allItems = crit.list();
        for (GSpreadSheetsCollectibleDescription item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsCollectibleDescription item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "collectible description {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsCollectibleDescription result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for collectible description: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for collectible description: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsCollectibleDescription item : items) {
            GSpreadSheetsCollectibleDescription result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting collectible description for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsCollectibleDescription> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsCollectibleDescription item : items) {
            spreadSheetItemMap.put(item.getTextEn(), item);
        }
        for (GSpreadSheetsCollectibleDescription item : allItems) {
            GSpreadSheetsCollectibleDescription result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing collectible description rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSheetsCollectibleDescription(null);
                    em.merge(t);
                }
                em.remove(item);
            }
        }
    }

    @Transactional
    public void loadLoadscreensFromSpreadSheet(List<GSpreadSheetsLoadscreen> items) {
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsLoadscreen.class);
        Map<String, GSpreadSheetsLoadscreen> itemMap = new HashMap<>();
        List<GSpreadSheetsLoadscreen> allItems = crit.list();
        for (GSpreadSheetsLoadscreen item : allItems) {
            itemMap.put(item.getTextEn(), item);
        }
        int total = items.size();
        int count = 0;
        for (GSpreadSheetsLoadscreen item : items) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "loadscreen {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsLoadscreen result = itemMap.get(item.getTextEn());
            if (result != null) {
                boolean isMerge = false;
                if (item.getWeight() != null && (result.getWeight() == null || !result.getWeight().equals(item.getWeight()))) {
                    isMerge = true;
                    result.setWeight(item.getWeight());
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "weight changed for loadscreen: {0}", item.getTextEn());
                }
                if (!result.getRowNum().equals(item.getRowNum())) {
                    isMerge = true;
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "rowNum changed for loadscreen: {0}", item.getTextEn());
                    result.setRowNum(item.getRowNum());
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }
        for (GSpreadSheetsLoadscreen item : items) {
            GSpreadSheetsLoadscreen result = itemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "inserting loadscreen for rowNum {0}", item.getRowNum());
                em.persist(item);
            }
        }
        Map<String, GSpreadSheetsLoadscreen> spreadSheetItemMap = new HashMap<>();
        for (GSpreadSheetsLoadscreen item : items) {
            spreadSheetItemMap.put(item.getTextEn(), item);
        }
        for (GSpreadSheetsLoadscreen item : allItems) {
            GSpreadSheetsLoadscreen result = spreadSheetItemMap.get(item.getTextEn());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "removing loadscreen rownum={0} :{1}", new Object[]{item.getRowNum(), item.getTextEn()});
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSheetsLoadscreen(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : item.getTranslatedTexts()) {
                    t.setSpreadSheetsJournalEntry(null);
                    em.merge(t);
                }
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
                for (TranslatedText t : npc.getTranslatedTexts()) {
                    t.setSpreadSheetsNpcName(null);
                    em.merge(t);
                }
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
            phrasesMap.put(phrase.getTextEn().toLowerCase(), phrase);
        }
        int total = phrases.size();
        int count = 0;
        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            count++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "phrase {0}/{1}", new Object[]{Integer.toString(count), Integer.toString(total)});
            GSpreadSheetsNpcPhrase result = phrasesMap.get(phrase.getTextEn().toLowerCase());
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
                if (!phrase.getTextEn().equals(result.getTextEn())) {
                    result.setTextEn(phrase.getTextEn());
                    result.setaId(null);
                    result.setbId(null);
                    result.setcId(null);
                    isMerge = true;
                }
                if (isMerge) {
                    em.merge(result);
                }
            }
        }

        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            GSpreadSheetsNpcPhrase result = phrasesMap.get(phrase.getTextEn().toLowerCase());
            if (result == null) {
                Logger.getLogger(DBService.class.getName()).info("inserting phrase for rowNum " + phrase.getRowNum());
                em.persist(phrase);
            }
        }
        Map<String, GSpreadSheetsNpcPhrase> spreadSheetPhrasesMap = new HashMap<>();
        for (GSpreadSheetsNpcPhrase phrase : phrases) {
            spreadSheetPhrasesMap.put(phrase.getTextEn().toLowerCase(), phrase);
        }
        for (GSpreadSheetsNpcPhrase phrase : allPhrases) {
            GSpreadSheetsNpcPhrase result = spreadSheetPhrasesMap.get(phrase.getTextEn().toLowerCase());
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
                for (TranslatedText t : phrase.getTranslatedTexts()) {
                    t.setSpreadSheetsNpcPhrase(null);
                    em.merge(t);
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
    public HierarchicalContainer getCollectiblesDiff(List<GSpreadSheetsCollectible> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<CollectiblesDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsCollectible.class);
        Map<Long, GSpreadSheetsCollectible> itemMap = new HashMap<>();
        List<GSpreadSheetsCollectible> allItems = crit.list();
        for (GSpreadSheetsCollectible item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsCollectible item : items) {
            GSpreadSheetsCollectible result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new CollectiblesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new CollectiblesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new CollectiblesDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new CollectiblesDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new CollectiblesDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (CollectiblesDiff diff : diffs) {
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
    public HierarchicalContainer getCollectibleDescriptionsDiff(List<GSpreadSheetsCollectibleDescription> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<CollectibleDescriptionsDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsCollectibleDescription.class);
        Map<Long, GSpreadSheetsCollectibleDescription> itemMap = new HashMap<>();
        List<GSpreadSheetsCollectibleDescription> allItems = crit.list();
        for (GSpreadSheetsCollectibleDescription item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsCollectibleDescription item : items) {
            GSpreadSheetsCollectibleDescription result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new CollectibleDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new CollectibleDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new CollectibleDescriptionsDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new CollectibleDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new CollectibleDescriptionsDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (CollectibleDescriptionsDiff diff : diffs) {
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
    public HierarchicalContainer getLoadscreensDiff(List<GSpreadSheetsLoadscreen> items, HierarchicalContainer hc) throws OriginalTextMismatchException {
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
        List<LoadscreensDiff> diffs = new ArrayList<>();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsLoadscreen.class);
        Map<Long, GSpreadSheetsLoadscreen> itemMap = new HashMap<>();
        List<GSpreadSheetsLoadscreen> allItems = crit.list();
        for (GSpreadSheetsLoadscreen item : allItems) {
            itemMap.put(item.getRowNum(), item);
        }
        for (GSpreadSheetsLoadscreen item : items) {
            GSpreadSheetsLoadscreen result = itemMap.get(item.getRowNum());
            if (result != null) {
                if (!item.getTextEn().equals(result.getTextEn())) {
                    throw new OriginalTextMismatchException(item.getRowNum(), item.getTextEn(), result.getTextEn());
                }
                if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().before(result.getChangeTime())) {
                    diffs.add(new LoadscreensDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (item.getChangeTime() != null && result.getChangeTime() != null && item.getChangeTime().after(result.getChangeTime())) {
                    diffs.add(new LoadscreensDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() != null && item.getChangeTime() == null) {
                    diffs.add(new LoadscreensDiff(item, result, SYNC_TYPE.TO_SPREADSHEET));
                } else if (result.getChangeTime() == null && item.getChangeTime() != null) {
                    diffs.add(new LoadscreensDiff(item, result, SYNC_TYPE.TO_DB));
                } else if (result.getChangeTime() == null && item.getChangeTime() == null && (item.getTextRu() != null) && (result.getTextRu() != null) && !item.getTextRu().equals(result.getTextRu())) {
                    diffs.add(new LoadscreensDiff(item, result, SYNC_TYPE.TO_DB));
                }
            }
        }
        for (LoadscreensDiff diff : diffs) {
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Subtitle> subtitleQuery = cb.createQuery(Subtitle.class);
        Root<Subtitle> subtitleFrom = subtitleQuery.from(Subtitle.class);
        subtitleQuery.select(subtitleFrom);
        subtitleQuery.where(cb.and(
                cb.or(
                        cb.isNotNull(subtitleFrom.get("text")),
                        cb.isNotNull(subtitleFrom.get("textRu"))
                ),
                cb.isNull(subtitleFrom.get("extNpcPhrase")),
                cb.isNull(subtitleFrom.get("extNpcPhraseFailed"))
        ));
        List<Subtitle> subtitleList = em.createQuery(subtitleQuery).setMaxResults(10000).getResultList();
        total = subtitleList.size();
        counter = 0;
        foundCounter = 0;
        for (Subtitle s : subtitleList) {
            counter++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "subtitle {0}/{1}", new Object[]{counter, total});
            if (s.getText() != null && !s.getText().isEmpty()) {
                Long textId = searchTableItemIndexed("GSpreadSheetsNpcPhrase", s.getText());
                if (textId != null) {
                    foundCounter++;
                    GSpreadSheetsNpcPhrase phrase = em.find(GSpreadSheetsNpcPhrase.class, textId);
                    s.setExtNpcPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}. \nS: {3} \nF: {4}", new Object[]{Integer.toString(counter), total, foundCounter, s.getText(), phrase.getTextEn()});
                    em.merge(s);
                } else {
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Not found phrase for {0} of {1}. Total phrases found {2}. \nS: {3}", new Object[]{Integer.toString(counter), total, foundCounter, s.getText()});
                    s.setExtNpcPhraseFailed(Boolean.TRUE);
                    em.merge(s);
                }
            } else if (s.getTextRu() != null && !s.getTextRu().isEmpty()) {
                Long textId = searchTableItemRuIndexed("GSpreadSheetsNpcPhrase", s.getTextRu());
                if (textId != null) {
                    foundCounter++;
                    GSpreadSheetsNpcPhrase phrase = em.find(GSpreadSheetsNpcPhrase.class, textId);
                    s.setExtNpcPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}. \nS: {3} \nF: {4}", new Object[]{Integer.toString(counter), total, foundCounter, s.getTextRu(), phrase.getTextRu()});
                    em.merge(s);
                } else {
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Not found phrase for {0} of {1}. Total phrases found {2}. \nS: {3}", new Object[]{Integer.toString(counter), total, foundCounter, s.getTextRu()});
                    s.setExtNpcPhraseFailed(Boolean.TRUE);
                    em.merge(s);
                }
            }
        }

        CriteriaQuery<Topic> npcTopicQuery = cb.createQuery(Topic.class);
        Root<Topic> npcTopicFrom = npcTopicQuery.from(Topic.class);
        npcTopicQuery.select(npcTopicFrom);
        npcTopicQuery.where(cb.and(
                cb.or(
                        cb.and(cb.isNotNull(npcTopicFrom.get("npcText")), cb.notEqual(npcTopicFrom.get("npcText"), "")),
                        cb.and(cb.isNotNull(npcTopicFrom.get("npcTextRu")), cb.notEqual(npcTopicFrom.get("npcTextRu"), ""))
                ),
                cb.isNull(npcTopicFrom.get("extNpcPhrase")),
                cb.isNull(npcTopicFrom.get("extNpcPhraseFailed"))
        ));
        List<Topic> npcTopicList = em.createQuery(npcTopicQuery).setMaxResults(10000).getResultList();
        total = npcTopicList.size();
        counter = 0;
        foundCounter = 0;
        for (Topic t : npcTopicList) {
            counter++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "npc topic {0}/{1}", new Object[]{counter, total});
            if (t.getNpcText() != null && !t.getNpcText().isEmpty()) {
                Long phraseId = searchTableItemIndexed("GSpreadSheetsNpcPhrase", t.getNpcText());
                if (phraseId != null) {
                    GSpreadSheetsNpcPhrase phrase = em.find(GSpreadSheetsNpcPhrase.class, phraseId);
                    foundCounter++;
                    t.setExtNpcPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}. \nS: {3} \nF: {4}", new Object[]{Integer.toString(counter), total, foundCounter, t.getNpcText(), phrase.getTextEn()});
                    em.merge(t);
                } else {
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Not found phrase for {0} of {1}. Total phrases found {2}. \nS: {3}", new Object[]{Integer.toString(counter), total, foundCounter, t.getNpcText()});
                    t.setExtNpcPhraseFailed(Boolean.TRUE);
                    em.merge(t);
                }

            } else if (t.getNpcTextRu() != null && !t.getNpcTextRu().isEmpty()) {
                Long phraseId = searchTableItemRuIndexed("GSpreadSheetsNpcPhrase", t.getNpcTextRu());
                if (phraseId != null) {
                    GSpreadSheetsNpcPhrase phrase = em.find(GSpreadSheetsNpcPhrase.class, phraseId);
                    foundCounter++;
                    t.setExtNpcPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}. \nS: {3} \nF: {4}", new Object[]{Integer.toString(counter), total, foundCounter, t.getNpcTextRu(), phrase.getTextRu()});
                    em.merge(t);
                } else {
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Not found phrase for {0} of {1}. Total phrases found {2}. \nS: {3}", new Object[]{Integer.toString(counter), total, foundCounter, t.getNpcTextRu()});
                    t.setExtNpcPhraseFailed(Boolean.TRUE);
                    em.merge(t);
                }
            }
        }

        CriteriaQuery<Topic> playerTopicQuery = cb.createQuery(Topic.class);
        Root<Topic> playerTopicFrom = playerTopicQuery.from(Topic.class);

        playerTopicQuery.select(playerTopicFrom);

        playerTopicQuery.where(cb.and(
                cb.or(
                        cb.and(cb.isNotNull(playerTopicFrom.get("playerText")),
                                cb.notLike(playerTopicFrom.get("playerText"), ""),
                                cb.notLike(playerTopicFrom.get("playerText"), "%Here's % gold. Clear my bounty."),
                                cb.notLike(playerTopicFrom.get("playerText"), "%Here's % gold and everything I've stolen. Clear my bounty."),
                                cb.notLike(playerTopicFrom.get("playerText"), "View Stable"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Store"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Store (%)"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Store (%), Smuggler's Fee %"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Guild Store"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Guild Store (%)"),
                                cb.notLike(playerTopicFrom.get("playerText"), "%I have powerful friends. My bounty has already been covered."),
                                cb.notLike(playerTopicFrom.get("playerText"), "Bank"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Buy Backpack Upgrade"),
                                cb.notLike(playerTopicFrom.get("playerText"), "%I won''t pay the bounty."),
                                cb.notLike(playerTopicFrom.get("playerText"), "Guild Bank"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Bid on Guild Trader"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Hire Guild Trader"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Soul Healing"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Have any poisons or potions today?"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Have anything that can help make me less noticeable?"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Have any equipment today?"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Торговать (%)"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Магазин (%), процент контрабандиста %"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Гильдейский магазин (%)"),
                                cb.notLike(playerTopicFrom.get("playerText"), "Магазин (%)")),
                        cb.and(cb.isNotNull(playerTopicFrom.get("playerTextRu")),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), ""),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Store (%)"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Store (%), Smuggler's Fee %"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Guild Store (%)"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "%I have powerful friends. My bounty has already been covered."),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "%У меня есть могущественные друзья. Мой штраф уже был оплачен."),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "%I won''t pay the bounty."),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Торговать (%)"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Магазин (%), процент контрабандиста %"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Гильдейский магазин (%)"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Гильдейский банк"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Банк"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Сделать ставку на гильдейского торговца"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Торговать (%)"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Торговать"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Заплатить Вот % в уплату моего штрафа."),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Заплатить Вот % и все украденное мной. Сними с меня штраф."),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Гильдейский магазин"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Купить улучшение рюкзака"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Осмотреть конюшню"),
                                cb.notLike(playerTopicFrom.get("playerTextRu"), "Магазин (%)"))
                ),
                cb.isNull(playerTopicFrom.get("extPlayerPhrase")),
                cb.isNull(npcTopicFrom.get("extPlayerPhraseFailed"))
        ));
        List<Topic> playerTopicList = em.createQuery(playerTopicQuery).setMaxResults(10000).getResultList();
        total = playerTopicList.size();
        counter = 0;
        foundCounter = 0;
        for (Topic t : playerTopicList) {
            counter++;
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "player topic {0}/{1}", new Object[]{counter, total});
            if (t.getPlayerText() != null && !t.getPlayerText().isEmpty()) {
                Long phraseId = searchTableItemIndexed("GSpreadSheetsPlayerPhrase", t.getPlayerText().replaceFirst("^Intimidate ", "").replaceFirst("^Persuade ", "").replaceFirst("^Угроза ", "").replaceFirst("^Ложь ", "").replaceFirst("^Убеждение ", "").replace("|cFF0000Угроза|r ", "").replace("|cFF0000Убеждение|r ", "").replace("|cFF0000Intimidate|r ", "").replace("|cFF0000Persuade|r ", "").replace("|cFF0000Óàeæäeîèe|r ", "").replace("|cFF0000Ïoíèìoáaîèe|r ", ""));
                if (phraseId != null) {
                    GSpreadSheetsPlayerPhrase phrase = em.find(GSpreadSheetsPlayerPhrase.class, phraseId);
                    foundCounter++;
                    t.setExtPlayerPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}. \nS: {3} \nF: {4}", new Object[]{Integer.toString(counter), total, foundCounter, t.getPlayerText(), phrase.getTextEn()});
                    em.merge(t);
                } else {
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Not found phrase for {0} of {1}. Total phrases found {2}. \nS: {3}", new Object[]{Integer.toString(counter), total, foundCounter, t.getPlayerText()});
                    t.setExtPlayerPhraseFailed(Boolean.TRUE);
                    em.merge(t);
                }
            } else if (t.getPlayerTextRu() != null && !t.getPlayerTextRu().isEmpty()) {
                Long phraseId = searchTableItemRuIndexed("GSpreadSheetsPlayerPhrase", t.getPlayerTextRu().replaceFirst("^Intimidate ", "").replaceFirst("^Persuade ", "").replaceFirst("^Угроза ", "").replaceFirst("^Ложь ", "").replaceFirst("^Убеждение ", "").replace("|cFF0000Угроза|r ", "").replace("|cFF0000Убеждение|r ", "").replace("|cFF0000Intimidate|r ", "").replace("|cFF0000Persuade|r ", "").replace("|cFF0000Óàeæäeîèe|r ", "").replace("|cFF0000Ïoíèìoáaîèe|r ", ""));
                if (phraseId != null) {
                    GSpreadSheetsPlayerPhrase phrase = em.find(GSpreadSheetsPlayerPhrase.class, phraseId);
                    foundCounter++;
                    t.setExtPlayerPhrase(phrase);
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "found phrase for {0} of {1}. Total phrases found {2}. \nS: {3} \nF: {4}", new Object[]{Integer.toString(counter), total, foundCounter, t.getPlayerTextRu(), phrase.getTextRu()});
                    em.merge(t);
                } else {
                    Logger.getLogger(DBService.class.getName()).log(Level.INFO, "Not found phrase for {0} of {1}. Total phrases found {2}. \nS: {3}", new Object[]{Integer.toString(counter), total, foundCounter, t.getPlayerTextRu()});
                    t.setExtPlayerPhraseFailed(Boolean.TRUE);
                    em.merge(t);
                }
            }
        }
    }

    private GSpreadSheetsNpcPhrase getNpcPharse(String text) {
        GSpreadSheetsNpcPhrase result = null;
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
        crit.add(Restrictions.ilike("textEn", text.replace("\n", "$")));
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
                Logger
                        .getLogger(DBService.class
                                .getName()).log(Level.INFO, "successfull match {0} for {1}", new Object[]{row[1], text});
                break;

            }
            if (result == null) {
                Logger.getLogger(DBService.class
                        .getName()).log(Level.INFO, "nothing found with " + regexp);
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
    public BeanItemContainer<Topic> getNpcTopics(Npc npc, BeanItemContainer<Topic> container, Set<TRANSLATE_STATUS> translateStatus, SysAccount translator, boolean noTranslations, boolean emptyTranslations, String searchString) {
        container.removeAllItems();
        List<Topic> list = topicRepository.findAll(new TopicSpecification(npc, translateStatus, translator, noTranslations, emptyTranslations, searchString));
        if (searchString != null && searchString.length() > 2) {
            container.addAll(list);
        } else {
            List<Topic> orderedList = new ArrayList<>();
            for (Topic t : list) {
                if (t.getPreviousTopics() == null || t.getPreviousTopics().isEmpty()) {
                    addNextTopics(t, orderedList);
                }

            }
            for (Topic t : list) {
                addNextTopics(t, orderedList);
            }
            container.addAll(orderedList);
        }
        return container;
    }

    public void addNextTopics(Topic t, List<Topic> topics) {
        if (!topics.contains(t)) {
            topics.add(t);
            for (Topic nextTopic : t.getNextTopics()) {
                addNextTopics(nextTopic, topics);
            }
        }
    }

    @Transactional
    public BeanItemContainer<Subtitle> getNpcSubtitles(Npc npc, BeanItemContainer<Subtitle> container, Set<TRANSLATE_STATUS> translateStatus, SysAccount translator, boolean noTranslations, boolean emptyTranslations, String searchString) {
        container.removeAllItems();
        List<Subtitle> list = subtitleRepository.findAll(new SubtitleSpecification(npc, translateStatus, translator, noTranslations, emptyTranslations, searchString));
        if (searchString != null && searchString.length() > 2) {
            container.addAll(list);
        } else {
            List<Subtitle> orderedSubtitles = new ArrayList<>();
            for (Subtitle s : list) {
                if (!orderedSubtitles.contains(s)) {
                    addAllPreviousSubtitles(orderedSubtitles, s);
                    orderedSubtitles.add(s);
                    addAllNextSubtitles(orderedSubtitles, s);
                }
            }
            container.addAll(orderedSubtitles);
        }
        return container;
    }

    private void addAllPreviousSubtitles(List<Subtitle> list, Subtitle s) {
        if (s.getPreviousSubtitle() != null && !list.contains(s.getPreviousSubtitle())) {
            list.add(0, s.getPreviousSubtitle());
            addAllPreviousSubtitles(list, s.getPreviousSubtitle());
        }
    }

    private void addAllNextSubtitles(List<Subtitle> list, Subtitle s) {
        if (s.getNextSubtitle() != null && !list.contains(s.getNextSubtitle())) {
            list.add(s.getNextSubtitle());
            addAllNextSubtitles(list, s.getNextSubtitle());
        }
    }

    @Transactional
    public Long countTranslatedTextFilterResult(Location location, Location subLocation, Quest quest, Set<TRANSLATE_STATUS> translateStatus, SysAccount translator, boolean noTranslations, boolean emptyTranslations, String searchString) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        CriteriaQuery<Long> cq1 = cb.createQuery(Long.class);
        CriteriaQuery<Long> cq2 = cb.createQuery(Long.class);
        Root<Npc> root = cq.from(Npc.class);
        Root<Npc> root1 = cq1.from(Npc.class);
        Root<Npc> root2 = cq2.from(Npc.class);
        Predicate result = null;
        Predicate result1 = null;
        Predicate result2 = null;
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> predicates1 = new ArrayList<>();
        List<Predicate> predicates2 = new ArrayList<>();
        Join<Object, Object> topicsJoin = root.joinSet("topics", javax.persistence.criteria.JoinType.LEFT);
        Join<Object, Object> topicsJoin1 = root1.joinSet("topics", javax.persistence.criteria.JoinType.LEFT);
        Join<Object, Object> subtitlesJoin = root2.joinSet("subtitles", javax.persistence.criteria.JoinType.LEFT);
        Join<Object, Object> join = topicsJoin.join("extNpcPhrase", javax.persistence.criteria.JoinType.LEFT);
        Join<Object, Object> join1 = topicsJoin1.join("extPlayerPhrase", javax.persistence.criteria.JoinType.LEFT);
        Join<Object, Object> join2 = subtitlesJoin.join("extNpcPhrase", javax.persistence.criteria.JoinType.LEFT);
        cq.groupBy(join.get("id"));
        cq1.groupBy(join1.get("id"));
        cq2.groupBy(join2.get("id"));
        if (subLocation != null) {
            predicates.add(
                    cb.equal(root.get("location"), subLocation)
            );
            predicates1.add(
                    cb.equal(root1.get("location"), subLocation)
            );
            predicates2.add(
                    cb.equal(root2.get("location"), subLocation)
            );
        } else if (location != null) {
            predicates.add(cb.or(
                    cb.equal(root.get("location"), location),
                    cb.equal(root.get("location").get("parentLocation"), location)
            ));
            predicates1.add(cb.or(
                    cb.equal(root1.get("location"), location),
                    cb.equal(root1.get("location").get("parentLocation"), location)
            ));
            predicates2.add(cb.or(
                    cb.equal(root2.get("location"), location),
                    cb.equal(root2.get("location").get("parentLocation"), location)
            ));
        }
        if (searchString != null && (searchString.length() > 2)) {

            String searchPattern = "%" + searchString.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(join.get("textEn")), searchPattern),
                    cb.like(cb.lower(join.get("textRu")), searchPattern)
            ));
            predicates1.add(cb.or(
                    cb.like(cb.lower(join1.get("textEn")), searchPattern),
                    cb.like(cb.lower(join1.get("textRu")), searchPattern)
            ));
            predicates2.add(cb.or(
                    cb.like(cb.lower(join2.get("textEn")), searchPattern),
                    cb.like(cb.lower(join2.get("textRu")), searchPattern)
            ));
        } else {
            if (noTranslations) {
                predicates.add(cb.isNull(join.get("translator")));
                predicates.add(cb.isNotNull(topicsJoin.get("extNpcPhrase")));
                predicates1.add(cb.isNull(join1.get("translator")));
                predicates1.add(cb.isNotNull(topicsJoin1.get("extPlayerPhrase")));
                predicates2.add(cb.isNull(join2.get("translator")));
                predicates2.add(cb.isNotNull(subtitlesJoin.get("extNpcPhrase")));

            }
            if (quest != null) {
                predicates.add(cb.equal(root.join("quests").get("id"), quest.getId()));
                predicates1.add(cb.equal(root1.join("quests").get("id"), quest.getId()));
                predicates2.add(cb.equal(root2.join("quests").get("id"), quest.getId()));
            }
            if (emptyTranslations || (translateStatus != null && !translateStatus.isEmpty()) || translator != null) {

                if (emptyTranslations) {

                    predicates.add(cb.and(
                            cb.isNotNull(topicsJoin.get("extNpcPhrase")),
                            cb.isEmpty(join.get("translatedTexts")),
                            cb.isNull(join.get("translator"))
                    ));
                    predicates1.add(cb.and(
                            cb.isNotNull(topicsJoin1.get("extPlayerPhrase")),
                            cb.isEmpty(join1.get("translatedTexts")),
                            cb.isNull(join1.get("translator"))
                    ));
                    predicates2.add(cb.and(
                            cb.isNotNull(subtitlesJoin.get("extNpcPhrase")),
                            cb.isEmpty(join2.get("translatedTexts")),
                            cb.isNull(join2.get("translator"))
                    ));
                } else if ((translateStatus != null && !translateStatus.isEmpty()) || translator != null) {
                    Join<Object, Object> join3 = join.join("translatedTexts", javax.persistence.criteria.JoinType.LEFT);
                    Join<Object, Object> join4 = join1.join("translatedTexts", javax.persistence.criteria.JoinType.LEFT);
                    Join<Object, Object> join5 = join2.join("translatedTexts", javax.persistence.criteria.JoinType.LEFT);
                    if (translateStatus != null && !translateStatus.isEmpty() && translator != null) {

                        predicates.add(cb.and(
                                join3.get("status").in(translateStatus),
                                cb.equal(join3.get("author"), translator)
                        ));
                        predicates1.add(cb.and(
                                join4.get("status").in(translateStatus),
                                cb.equal(join4.get("author"), translator)
                        ));
                        predicates2.add(cb.and(
                                join5.get("status").in(translateStatus),
                                cb.equal(join5.get("author"), translator)
                        ));
                    } else if (translator != null) {
                        predicates.add(cb.equal(join3.get("author"), translator));
                        predicates1.add(cb.equal(join4.get("author"), translator));
                        predicates2.add(cb.equal(join5.get("author"), translator));
                    } else if (translateStatus != null) {
                        predicates.add(join3.get("status").in(translateStatus));
                        predicates1.add(join4.get("status").in(translateStatus));
                        predicates2.add(join5.get("status").in(translateStatus));
                    }
                }

            }
        }

        if (!predicates.isEmpty() && predicates.size() > 1) {
            result = cb.and(predicates.toArray(new Predicate[predicates.size()]));
        } else if (!predicates.isEmpty()) {
            result = predicates.get(0);
        }
        if (!predicates1.isEmpty() && predicates1.size() > 1) {
            result1 = cb.and(predicates1.toArray(new Predicate[predicates1.size()]));
        } else if (!predicates1.isEmpty()) {
            result1 = predicates1.get(0);
        }
        if (!predicates2.isEmpty() && predicates2.size() > 1) {
            result2 = cb.and(predicates2.toArray(new Predicate[predicates2.size()]));
        } else if (!predicates2.isEmpty()) {
            result2 = predicates2.get(0);
        }

        cq.select(cb.count(root));
        cq1.select(cb.count(root1));
        cq2.select(cb.count(root2));
        if (result != null) {
            cq.where(result);
        }
        if (result1 != null) {
            cq1.where(result1);
        }
        if (result2 != null) {
            cq2.where(result2);
        }
        Long count = 0L;
        List<Long> countList = em.createQuery(cq).getResultList();
        List<Long> countList1 = em.createQuery(cq1).getResultList();
        List<Long> countList2 = em.createQuery(cq2).getResultList();
        for (Long c : countList) {
            if (c > 0) {
                count++;
            }
        }
        for (Long c : countList1) {
            if (c > 0) {
                count++;
            }
        }
        for (Long c : countList2) {
            if (c > 0) {
                count++;
            }
        }
        return count;
    }

    @Transactional
    public BeanItemContainer<Npc> getNpcs(BeanItemContainer<Npc> container, TRANSLATE_STATUS translateStatus, SysAccount translator, boolean noTranslations) {
        container.removeAllItems();
        Session session = (Session) em.getDelegate();
        Criteria crit = session.createCriteria(Npc.class
        );
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
            em.remove(em.find(TranslatedText.class,
                    entity.getId()));
        } else {
            entity.setChangeTime(new Date());
            em.merge(entity);
        }
    }

    @Transactional
    public void refreshEntity(Object object) {
        em.refresh(object);
    }

    @Transactional
    public DAO loadDAO(DAO object) {
        return em.find(object.getClass(), object.getId());
    }

    @Transactional
    public void saveTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.NEW);
        if (entity.getId() == null) {
            entity.setCreateTime(new Date());
            em.persist(entity);

        } else if (entity.getText() == null || entity.getText().isEmpty()) {
            em.remove(em.find(TranslatedText.class,
                    entity.getId()));
        } else {
            entity.setChangeTime(new Date());
            em.merge(entity);
        }
    }

    @Transactional
    public void savePlayerPhrases(List<GSpreadSheetsPlayerPhrase> phrases) {
        Session session = (Session) em.getDelegate();

        for (GSpreadSheetsPlayerPhrase phrase : phrases) {
            Criteria crit = session.createCriteria(GSpreadSheetsPlayerPhrase.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsNpcPhrase.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsNpcName.class
            );
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
            Criteria npcNameCrit = session.createCriteria(Npc.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsLocationName.class
            );
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
            Criteria locationsCrit = session.createCriteria(Location.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsQuestName.class
            );
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
            Criteria questsCrit = session.createCriteria(Quest.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsQuestDescription.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsQuestDirection.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsItemName.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsItemDescription.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsActivator.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsAchievement.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsAchievementDescription.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsNote.class
            );
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
            Criteria crit = session.createCriteria(GSpreadSheetsAbilityDescription.class
            );
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
    public void saveCollectibles(List<GSpreadSheetsCollectible> items) {
        Session session = (Session) em.getDelegate();

        for (GSpreadSheetsCollectible item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsCollectible.class
            );
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsCollectible result = (GSpreadSheetsCollectible) crit.uniqueResult();
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
    public void saveCollectibleDescriptions(List<GSpreadSheetsCollectibleDescription> items) {
        Session session = (Session) em.getDelegate();

        for (GSpreadSheetsCollectibleDescription item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsCollectibleDescription.class
            );
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsCollectibleDescription result = (GSpreadSheetsCollectibleDescription) crit.uniqueResult();
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
    public void saveLoadscreens(List<GSpreadSheetsLoadscreen> items) {
        Session session = (Session) em.getDelegate();

        for (GSpreadSheetsLoadscreen item : items) {
            Criteria crit = session.createCriteria(GSpreadSheetsLoadscreen.class
            );
            crit.add(Restrictions.eq("rowNum", item.getRowNum()));
            GSpreadSheetsLoadscreen result = (GSpreadSheetsLoadscreen) crit.uniqueResult();
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
            Criteria crit = session.createCriteria(GSpreadSheetsJournalEntry.class
            );
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
        entity.setPreApprovedBy(SpringSecurityHelper.getSysAccount());
        em.merge(entity);
    }

    @Transactional
    public void correctTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.CORRECTED);
        entity.setCorrectedBy(SpringSecurityHelper.getSysAccount());
        em.merge(entity);
    }

    @Transactional
    public void rejectTranslatedText(TranslatedText entity) {
        entity.setStatus(TRANSLATE_STATUS.REJECTED);
        entity.setRejectedBy(SpringSecurityHelper.getSysAccount());
        entity.setCorrectedBy(null);
        entity.setPreApprovedBy(null);
        em.merge(entity);
    }

    @Transactional
    public void acceptTranslatedText(TranslatedText entity) {
        Session session = (Session) em.getDelegate();
        if (entity.getBook() == null) {
            entity.setText(entity.getText().trim().replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("\n", "$"));
        }
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
        } else if (entity.getSheetsCollectible() != null) {
            GSpreadSheetsCollectible gs = em.find(GSpreadSheetsCollectible.class, entity.getSheetsCollectible().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSheetsCollectibleDescription() != null) {
            GSpreadSheetsCollectibleDescription gs = em.find(GSpreadSheetsCollectibleDescription.class, entity.getSheetsCollectibleDescription().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getSheetsLoadscreen() != null) {
            GSpreadSheetsLoadscreen gs = em.find(GSpreadSheetsLoadscreen.class, entity.getSheetsLoadscreen().getId());
            gs.setTextRu(entity.getText());
            gs.setTranslator(entity.getAuthor().getLogin());
            gs.setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getBook() != null) {
            BookText gs = em.find(BookText.class, entity.getBook().getId());
            gs.setTextRu(entity.getText());
            gs.getBook().setTranslator(entity.getAuthor().getLogin());
            gs.getBook().setChangeTime(new Date());
            em.merge(gs);
            isSucceeded = true;
        } else if (entity.getBookName() != null) {
            Book gs = em.find(Book.class, entity.getBookName().getId());
            gs.setNameRu(entity.getText());
            em.merge(gs);
            Criteria activatorCrit = session.createCriteria(GSpreadSheetsActivator.class);
            activatorCrit.add(Restrictions.eq("textEn", gs.getNameEn()));
            List<GSpreadSheetsActivator> activatorList = activatorCrit.list();
            if (activatorList != null) {
                for (GSpreadSheetsActivator ac : activatorList) {
                    ac.setTextRu(entity.getText());
                    ac.setTranslator(entity.getAuthor().getLogin());
                    ac.setChangeTime(new Date());
                    em.merge(ac);
                }
            }
            Criteria itemCrit = session.createCriteria(GSpreadSheetsItemName.class);
            itemCrit.add(Restrictions.eq("textEn", gs.getNameEn()));
            List<GSpreadSheetsItemName> itemList = itemCrit.list();
            if (itemList != null) {
                for (GSpreadSheetsItemName it : itemList) {
                    it.setTextRu(entity.getText());
                    it.setTranslator(entity.getAuthor().getLogin());
                    it.setChangeTime(new Date());
                    em.merge(it);
                }
            }
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

        Criteria npcCrit = session.createCriteria(GSpreadSheetsNpcName.class
        );
        npcCrit.add(searchTerms);
        List<GSpreadSheetsNpcName> npcList = npcCrit.list();
        for (GSpreadSheetsNpcName npc : npcList) {
            Item item = hc.addItem(npc);
            item.getItemProperty("textEn").setValue(npc.getTextEn());
            if (npc.getSex() != null) {
                item.getItemProperty("textEn").setValue(npc.getTextEn() + "(" + npc.getSex().toString().substring(0, 1) + ")");
            }
            item.getItemProperty("textRu").setValue(npc.getTextRu());
            item.getItemProperty("weight").setValue(npc.getWeight());
            item.getItemProperty("translator").setValue(npc.getTranslator());
            item.getItemProperty("catalogType").setValue("NPC");

        }
        Criteria locationCrit = session.createCriteria(GSpreadSheetsLocationName.class
        );
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
        Criteria activatorCrit = session.createCriteria(GSpreadSheetsActivator.class
        );
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

        Criteria itemNameCrit = session.createCriteria(GSpreadSheetsItemName.class
        );
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

        Criteria itemDescriptionCrit = session.createCriteria(GSpreadSheetsItemDescription.class
        );
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

        Criteria questNameCrit = session.createCriteria(GSpreadSheetsQuestName.class
        );
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

        Criteria questDescriptionCrit = session.createCriteria(GSpreadSheetsQuestDescription.class
        );
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

        Criteria questDirectionCrit = session.createCriteria(GSpreadSheetsQuestDirection.class
        );
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

        Criteria journalEntryCrit = session.createCriteria(GSpreadSheetsJournalEntry.class
        );
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

        Criteria npcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class
        );
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
        Criteria playerPhraseCrit = session.createCriteria(GSpreadSheetsPlayerPhrase.class
        );
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
        Criteria achievementCrit = session.createCriteria(GSpreadSheetsAchievement.class
        );
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
        Criteria achievementDescriptionCrit = session.createCriteria(GSpreadSheetsAchievementDescription.class
        );
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
        Criteria noteCrit = session.createCriteria(GSpreadSheetsNote.class
        );
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
        Criteria abilityDescriptionCrit = session.createCriteria(GSpreadSheetsAbilityDescription.class
        );
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
        Criteria collectibleCrit = session.createCriteria(GSpreadSheetsCollectible.class
        );
        collectibleCrit.add(searchTerms);
        List<GSpreadSheetsCollectible> collectibleList = collectibleCrit.list();
        for (GSpreadSheetsCollectible i : collectibleList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("weight").setValue(i.getWeight());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Коллекционный предмет");

        }
        Criteria collectibleDescriptionCrit = session.createCriteria(GSpreadSheetsCollectibleDescription.class
        );
        collectibleDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsCollectibleDescription> collectibleDescriptionList = collectibleDescriptionCrit.list();
        for (GSpreadSheetsCollectibleDescription i : collectibleDescriptionList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("weight").setValue(i.getWeight());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Описание коллекционного предмета");

        }
        Criteria loadscreenCrit = session.createCriteria(GSpreadSheetsLoadscreen.class
        );
        loadscreenCrit.add(searchTerms);
        List<GSpreadSheetsLoadscreen> loadscreenList = loadscreenCrit.list();
        for (GSpreadSheetsLoadscreen i : loadscreenList) {
            Item item = hc.addItem(i);
            item.getItemProperty("textEn").setValue(i.getTextEn());
            item.getItemProperty("textRu").setValue(i.getTextRu());
            item.getItemProperty("weight").setValue(i.getWeight());
            item.getItemProperty("translator").setValue(i.getTranslator());
            item.getItemProperty("catalogType").setValue("Загрузочный экран");

        }
        Criteria esoInterfaceVariableCrit = session.createCriteria(EsoInterfaceVariable.class
        );
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
    public List<TranslatedEntity> searchInCatalogs(String search) {
        List<TranslatedEntity> result = new ArrayList<>();
        List<Criterion> searchTermitems = new ArrayList<>();
        searchTermitems.add(Restrictions.ilike("textEn", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("textRu", search, MatchMode.ANYWHERE));
        Disjunction searchTerms = Restrictions.or(searchTermitems.toArray(new Criterion[searchTermitems.size()]));
        Session session = (Session) em.getDelegate();
        Criteria npcCrit = session.createCriteria(GSpreadSheetsNpcName.class);
        npcCrit.add(searchTerms);
        List<GSpreadSheetsNpcName> npcList = npcCrit.list();
        result.addAll(npcList);

        Criteria locationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
        locationCrit.add(searchTerms);
        List<GSpreadSheetsLocationName> locationList = locationCrit.list();
        result.addAll(locationList);

        /*
         Criteria activatorCrit = session.createCriteria(GSpreadSheetsActivator.class);
         activatorCrit.add(searchTerms);
         List<GSpreadSheetsActivator> activatorList = activatorCrit.list();
         result.addAll(activatorList);
         */
        Criteria itemNameCrit = session.createCriteria(GSpreadSheetsItemName.class);
        itemNameCrit.add(searchTerms);
        List<GSpreadSheetsItemName> itemNameList = itemNameCrit.list();
        result.addAll(itemNameList);

        /*Criteria itemDescriptionCrit = session.createCriteria(GSpreadSheetsItemDescription.class);
         itemDescriptionCrit.add(searchTerms);
         List<GSpreadSheetsItemDescription> itemDescriptionList = itemDescriptionCrit.list();
         result.addAll(itemDescriptionList);*/
        Criteria questNameCrit = session.createCriteria(GSpreadSheetsQuestName.class);
        questNameCrit.add(searchTerms);
        List<GSpreadSheetsQuestName> questNameList = questNameCrit.list();
        result.addAll(questNameList);

        /*Criteria questDescriptionCrit = session.createCriteria(GSpreadSheetsQuestDescription.class);
         questDescriptionCrit.add(searchTerms);
         List<GSpreadSheetsQuestDescription> questDescriptionList = questDescriptionCrit.list();
         result.addAll(questDescriptionList);

         Criteria questDirectionCrit = session.createCriteria(GSpreadSheetsQuestDirection.class);
         questDirectionCrit.add(searchTerms);
         List<GSpreadSheetsQuestDirection> questDirectionList = questDirectionCrit.list();
         result.addAll(questDirectionList);

         Criteria journalEntryCrit = session.createCriteria(GSpreadSheetsJournalEntry.class);
         journalEntryCrit.add(searchTerms);
         List<GSpreadSheetsJournalEntry> journalEntryList = journalEntryCrit.list();
         result.addAll(journalEntryList);
         */

 /*Criteria npcPhraseCrit = session.createCriteria(GSpreadSheetsNpcPhrase.class);
         npcPhraseCrit.add(searchTerms);
         List<GSpreadSheetsNpcPhrase> npcPhraseList = npcPhraseCrit.list();
         result.addAll(npcPhraseList);

         Criteria playerPhraseCrit = session.createCriteria(GSpreadSheetsPlayerPhrase.class);
         playerPhraseCrit.add(searchTerms);
         List<GSpreadSheetsPlayerPhrase> playerPhraseList = playerPhraseCrit.list();
         result.addAll(playerPhraseList);
         */
        Criteria achievementCrit = session.createCriteria(GSpreadSheetsAchievement.class);
        achievementCrit.add(searchTerms);
        List<GSpreadSheetsAchievement> achievementList = achievementCrit.list();
        result.addAll(achievementList);

        /*Criteria achievementDescriptionCrit = session.createCriteria(GSpreadSheetsAchievementDescription.class);
         achievementDescriptionCrit.add(searchTerms);
         List<GSpreadSheetsAchievementDescription> achievementDescriptionList = achievementDescriptionCrit.list();
         result.addAll(achievementDescriptionList);

         Criteria noteCrit = session.createCriteria(GSpreadSheetsNote.class);
         noteCrit.add(searchTerms);
         List<GSpreadSheetsNote> noteList = noteCrit.list();
         result.addAll(noteList);
         */
        Criteria abilityDescriptionCrit = session.createCriteria(GSpreadSheetsAbilityDescription.class);
        abilityDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsAbilityDescription> abilityDescriptionList = abilityDescriptionCrit.list();
        result.addAll(abilityDescriptionList);

        Criteria collectibleCrit = session.createCriteria(GSpreadSheetsCollectible.class);
        collectibleCrit.add(searchTerms);
        List<GSpreadSheetsCollectible> collectibleList = collectibleCrit.list();
        result.addAll(collectibleList);

        Criteria collectibleDescriptionCrit = session.createCriteria(GSpreadSheetsCollectibleDescription.class);
        collectibleDescriptionCrit.add(searchTerms);
        List<GSpreadSheetsCollectibleDescription> collectibleDescriptionList = collectibleDescriptionCrit.list();
        result.addAll(collectibleDescriptionList);

        Criteria loadscreenCrit = session.createCriteria(GSpreadSheetsLoadscreen.class);
        loadscreenCrit.add(searchTerms);
        List<GSpreadSheetsLoadscreen> loadscreenList = loadscreenCrit.list();
        result.addAll(loadscreenList);

        Criteria esoInterfaceVariableCrit = session.createCriteria(EsoInterfaceVariable.class);
        searchTermitems = new ArrayList<>();
        searchTermitems.add(Restrictions.ilike("textEn", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("textRu", search, MatchMode.ANYWHERE));
        searchTermitems.add(Restrictions.ilike("name", search, MatchMode.ANYWHERE));
        searchTerms = Restrictions.or(searchTermitems.toArray(new Criterion[searchTermitems.size()]));
        esoInterfaceVariableCrit.add(searchTerms);
        List<EsoInterfaceVariable> esoInterfaceVariableList = esoInterfaceVariableCrit.list();
        result.addAll(esoInterfaceVariableList);

        return result;
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
        if ((item.getEntity() instanceof GSpreadSheetsNpcName) || (item.getEntity() instanceof GSpreadSheetsLocationName) || (item.getEntity() instanceof GSpreadSheetsNpcPhrase) || (item.getEntity() instanceof GSpreadSheetsPlayerPhrase) || (item.getEntity() instanceof GSpreadSheetsQuestName) || (item.getEntity() instanceof GSpreadSheetsQuestDescription) || (item.getEntity() instanceof GSpreadSheetsActivator) || (item.getEntity() instanceof GSpreadSheetsJournalEntry) || (item.getEntity() instanceof GSpreadSheetsItemName) || (item.getEntity() instanceof GSpreadSheetsItemDescription) || (item.getEntity() instanceof GSpreadSheetsQuestDirection) || (item.getEntity() instanceof EsoInterfaceVariable) || (item.getEntity() instanceof GSpreadSheetsAchievement) || (item.getEntity() instanceof GSpreadSheetsAchievementDescription) || (item.getEntity() instanceof GSpreadSheetsNote) || (item.getEntity() instanceof GSpreadSheetsAbilityDescription) || (item.getEntity() instanceof GSpreadSheetsCollectible) || (item.getEntity() instanceof GSpreadSheetsCollectibleDescription) || (item.getEntity() instanceof GSpreadSheetsLoadscreen)) {
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
            } else if (itemId instanceof GSpreadSheetsCollectible) {
                ((GSpreadSheetsCollectible) itemId).setChangeTime(new Date());
                ((GSpreadSheetsCollectible) itemId).setTextRu(textRu);
                ((GSpreadSheetsCollectible) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsCollectibleDescription) {
                ((GSpreadSheetsCollectibleDescription) itemId).setChangeTime(new Date());
                ((GSpreadSheetsCollectibleDescription) itemId).setTextRu(textRu);
                ((GSpreadSheetsCollectibleDescription) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
            } else if (itemId instanceof GSpreadSheetsLoadscreen) {
                ((GSpreadSheetsLoadscreen) itemId).setChangeTime(new Date());
                ((GSpreadSheetsLoadscreen) itemId).setTextRu(textRu);
                ((GSpreadSheetsLoadscreen) itemId).setTranslator(SpringSecurityHelper.getSysAccount().getLogin());
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
                Criteria crit = session.createCriteria(Location.class
                );
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
                Criteria crit = session.createCriteria(Quest.class
                );
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
                Criteria crit = session.createCriteria(Npc.class
                );
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
        result
                .addContainerProperty("name", String.class,
                        null);
        result
                .addContainerProperty("value", String.class,
                        null);
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
                + "select 'Перевод названий коллекционных предметов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetscollectible where texten!=textru union all select null as translated,count(*) as total from gspreadsheetscollectible) as qres union all\n"
                + "select 'Перевод описаний коллекционных предметов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetscollectibledescription where texten!=textru union all select null as translated,count(*) as total from gspreadsheetscollectibledescription) as qres union all\n"
                + "select 'Перевод загрузочных экранов', sum(translated) as translated,sum(total) as total from (select count(*) as translated,null as total from gspreadsheetsloadscreen where texten!=textru union all select null as translated,count(*) as total from gspreadsheetsloadscreen) as qres union all\n"
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
        Criteria crit = session.createCriteria(SystemProperty.class
        );
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
        Criteria crit = session.createCriteria(SystemProperty.class
        );
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
    public void cleanupRawWithWrongVer(String ver) {
        Query updateQ = em.createNativeQuery("delete from esorawstring where ver!=:ver");
        updateQ.setParameter("ver", ver);
        updateQ.executeUpdate();
    }

    @Transactional
    public void cleanupRawWithNullVer() {
        Query updateQ = em.createNativeQuery("delete from esorawstring where ver is null");
        updateQ.executeUpdate();
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
    public void updateRuRawStrings(List<Object[]> rows, String ver) {
        for (Object[] row : rows) {
            Query q = em.createNativeQuery("update esorawstring set textru=:textru,ver=:ver where aid=:aid and bid=:bid and cid=:cid");
            q.setParameter("aid", row[0]);
            q.setParameter("bid", row[1]);
            q.setParameter("cid", row[2]);
            q.setParameter("textru", row[3]);
            q.setParameter("ver", ver);
            q.executeUpdate();
        }
    }

    @Transactional
    public void addSpellerWord(String word) {
        Session session = (Session) em.getDelegate();
        Criteria c = session.createCriteria(SpellerWord.class
        );
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
        Criteria c = session.createCriteria(SpellerWord.class
        );
        c.add(Restrictions.eq("text", word));
        SpellerWord w = (SpellerWord) c.uniqueResult();
        if (w != null) {
            return true;
        }
        return false;
    }

    @Transactional
    public void assignSpreadSheetRowsToRawStrings() {

        TypedQuery<GSpreadSheetsActivator> activatorQuery = em.createQuery("select a from GSpreadSheetsActivator a where aId is null", GSpreadSheetsActivator.class
        );
        List<GSpreadSheetsActivator> activatorList = activatorQuery.getResultList();

        for (GSpreadSheetsActivator item : activatorList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{87370069L, 19398485L, 39619172L, 14464837L, 207758933L, 77659573L, 124318053L, 219936053L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);

            }
        }
        TypedQuery<GSpreadSheetsItemDescription> itemDescriptionQuery = em.createQuery("select a from GSpreadSheetsItemDescription a where aId is null", GSpreadSheetsItemDescription.class
        );
        List<GSpreadSheetsItemDescription> itemDescriptionList = itemDescriptionQuery.getResultList();

        for (GSpreadSheetsItemDescription item : itemDescriptionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{139139780L, 228378404L, 249673710L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);

            }
        }
        TypedQuery<GSpreadSheetsItemName> itemNameQuery = em.createQuery("select a from GSpreadSheetsItemName a where aId is null", GSpreadSheetsItemName.class
        );
        List<GSpreadSheetsItemName> itemNameList = itemNameQuery.getResultList();

        for (GSpreadSheetsItemName item : itemNameList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{242841733L, 267697733L, 124362421L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);

            }
        }
        TypedQuery<GSpreadSheetsJournalEntry> journalEntryQuery = em.createQuery("select a from GSpreadSheetsJournalEntry a where aId is null", GSpreadSheetsJournalEntry.class
        );
        List<GSpreadSheetsJournalEntry> journalEntryList = journalEntryQuery.getResultList();

        for (GSpreadSheetsJournalEntry item : journalEntryList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{103224356L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);

            }
        }
        TypedQuery<GSpreadSheetsLocationName> locationNameQuery = em.createQuery("select a from GSpreadSheetsLocationName a where aId is null", GSpreadSheetsLocationName.class
        );
        List<GSpreadSheetsLocationName> locationNameList = locationNameQuery.getResultList();

        for (GSpreadSheetsLocationName item : locationNameList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{10860933L, 146361138L, 162946485L, 162658389L, 164009093L, 267200725L, 28666901L, 81344020L, 268015829L, 111863941L, 157886597L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);

            }
        }
        TypedQuery<GSpreadSheetsNpcName> npcNameQuery = em.createQuery("select a from GSpreadSheetsNpcName a where aId is null", GSpreadSheetsNpcName.class
        );
        npcNameQuery.setMaxResults(1000);
        List<GSpreadSheetsNpcName> npcNameList = npcNameQuery.getResultList();
        for (GSpreadSheetsNpcName item : npcNameList) {
            String textEn = item.getTextEn().replace("$", "\n");
            String textEn2 = item.getTextEn().replace("$", "\n");
            if (item.getSex() != null) {
                switch (item.getSex()) {
                    case U:
                        textEn = item.getTextEn().replace("$", "\n");
                        textEn2 = item.getTextEn().replace("$", "\n");
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
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where (textEn=:textEn or textEn=:textEn2) and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", textEn);
            rawQ.setParameter("textEn2", textEn2);
            rawQ.setParameter("aId", Arrays.asList(new Long[]{8290981L, 51188660L, 191999749L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);

            }
        }
        TypedQuery<GSpreadSheetsNpcPhrase> npcPhraseQuery = em.createQuery("select a from GSpreadSheetsNpcPhrase a where aId is null order by changeTime", GSpreadSheetsNpcPhrase.class
        );
        npcPhraseQuery.setMaxResults(1000);
        List<GSpreadSheetsNpcPhrase> npcPhraseList = npcPhraseQuery.getResultList();

        for (GSpreadSheetsNpcPhrase item : npcPhraseList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and bId=:bId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{55049764L, 115740052L, 149328292L, 3952276L, 165399380L, 200879108L, 116521668L, 211899940L, 234743124L}));
            rawQ.setParameter("bId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                item.setaId(s.getaId());
                item.setbId(s.getbId());
                item.setcId(s.getcId());
                em.merge(item);

            }
        }
        TypedQuery<GSpreadSheetsPlayerPhrase> PlayerPhraseQuery = em.createQuery("select a from GSpreadSheetsPlayerPhrase a where aId is null order by changeTime", GSpreadSheetsPlayerPhrase.class
        );
        PlayerPhraseQuery.setMaxResults(100);
        List<GSpreadSheetsPlayerPhrase> PlayerPhraseList = PlayerPhraseQuery.getResultList();

        for (GSpreadSheetsPlayerPhrase item : PlayerPhraseList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and bId=:bId and textDe is not null order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{204987124L, 20958740L, 249936564L, 228103012L, 232026500L, 150525940L, 99155012L, 109216308L}));
            rawQ.setParameter("bId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }
        TypedQuery<GSpreadSheetsQuestDescription> GSpreadSheetsQuestQuery = em.createQuery("select a from GSpreadSheetsQuestDescription a where aId is null order by changeTime", GSpreadSheetsQuestDescription.class
        );
        GSpreadSheetsQuestQuery.setMaxResults(100);
        List<GSpreadSheetsQuestDescription> GSpreadSheetsQuestList = GSpreadSheetsQuestQuery.getResultList();

        for (GSpreadSheetsQuestDescription item : GSpreadSheetsQuestList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{265851556L, 205344756L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }
        TypedQuery<GSpreadSheetsQuestDirection> QuestDirectionQuery = em.createQuery("select a from GSpreadSheetsQuestDirection a where aId is null order by changeTime", GSpreadSheetsQuestDirection.class
        );
        QuestDirectionQuery.setMaxResults(1000);
        List<GSpreadSheetsQuestDirection> QuestDirectionList = QuestDirectionQuery.getResultList();

        for (GSpreadSheetsQuestDirection item : QuestDirectionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{7949764L, 256430276L, 121487972L, 168415844L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }

        TypedQuery<GSpreadSheetsQuestName> QuestNameQuery = em.createQuery("select a from GSpreadSheetsQuestName a where aId is null order by changeTime", GSpreadSheetsQuestName.class
        );
        QuestNameQuery.setMaxResults(100);
        List<GSpreadSheetsQuestName> QuestNameList = QuestNameQuery.getResultList();

        for (GSpreadSheetsQuestName item : QuestNameList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{52420949L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }

        TypedQuery<GSpreadSheetsAchievement> achievementQuery = em.createQuery("select a from GSpreadSheetsAchievement a where aId is null order by changeTime", GSpreadSheetsAchievement.class
        );
        achievementQuery.setMaxResults(100);
        List<GSpreadSheetsAchievement> achievementList = achievementQuery.getResultList();

        for (GSpreadSheetsAchievement item : achievementList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{12529189L, 172030117L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }

        TypedQuery<GSpreadSheetsAchievementDescription> achievementDescriptionQuery = em.createQuery("select a from GSpreadSheetsAchievementDescription a where aId is null order by changeTime", GSpreadSheetsAchievementDescription.class
        );
        achievementDescriptionQuery.setMaxResults(100);
        List<GSpreadSheetsAchievementDescription> achievementDescriptionList = achievementDescriptionQuery.getResultList();

        for (GSpreadSheetsAchievementDescription item : achievementDescriptionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{188155806L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }

        TypedQuery<GSpreadSheetsNote> noteQuery = em.createQuery("select a from GSpreadSheetsNote a where aId is null order by changeTime", GSpreadSheetsNote.class
        );
        noteQuery.setMaxResults(100);
        List<GSpreadSheetsNote> noteList = noteQuery.getResultList();

        for (GSpreadSheetsNote item : noteList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{219317028L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }
        TypedQuery<GSpreadSheetsAbilityDescription> abilityDescriptionQuery = em.createQuery("select a from GSpreadSheetsAbilityDescription a where aId is null order by changeTime", GSpreadSheetsAbilityDescription.class
        );
        //abilityDescriptionQuery.setMaxResults(100);
        List<GSpreadSheetsAbilityDescription> abilityDescriptionList = abilityDescriptionQuery.getResultList();

        for (GSpreadSheetsAbilityDescription item : abilityDescriptionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{132143172L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }

        TypedQuery<GSpreadSheetsCollectible> collectibleQuery = em.createQuery("select a from GSpreadSheetsCollectible a where aId is null order by changeTime", GSpreadSheetsCollectible.class
        );
        //abilityDescriptionQuery.setMaxResults(100);
        List<GSpreadSheetsCollectible> collectibleList = collectibleQuery.getResultList();

        for (GSpreadSheetsCollectible item : collectibleList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{18173141L, 70328405L, 160914197L, 245765621L, 213229525L, 204530069L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }

        TypedQuery<GSpreadSheetsCollectibleDescription> collectibleDescriptionQuery = em.createQuery("select a from GSpreadSheetsCollectibleDescription a where aId is null order by changeTime", GSpreadSheetsCollectibleDescription.class
        );
        //abilityDescriptionQuery.setMaxResults(100);
        List<GSpreadSheetsCollectibleDescription> collectibleDescriptionList = collectibleDescriptionQuery.getResultList();

        for (GSpreadSheetsCollectibleDescription item : collectibleDescriptionList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{211640654L, 263796174L, 86917166L, 69169806L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
            List<EsoRawString> rList = rawQ.getResultList();
            if (rList != null && !rList.isEmpty()) {
                EsoRawString s = rList.get(0);
                setAidBidCid(item, s.getaId(), s.getbId(), s.getcId());

            }
        }

        TypedQuery<GSpreadSheetsLoadscreen> loadscreenQuery = em.createQuery("select a from GSpreadSheetsLoadscreen a where aId is null order by changeTime", GSpreadSheetsLoadscreen.class
        );
        //abilityDescriptionQuery.setMaxResults(100);
        List<GSpreadSheetsLoadscreen> loadscreenList = loadscreenQuery.getResultList();

        for (GSpreadSheetsLoadscreen item : loadscreenList) {
            TypedQuery<EsoRawString> rawQ = em.createQuery("select a from EsoRawString a where textEn=:textEn and aId in (:aId) and cId=:cId order by aId,cId", EsoRawString.class
            );
            rawQ.setParameter("textEn", item.getTextEn().replace("$", "\n"));
            rawQ.setParameter("aId", Arrays.asList(new Long[]{70901198L, 153349653L, 4922190L}));
            rawQ.setParameter("cId", item.getWeight().longValue());
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
    public DAO getLinkedItem(GSpreadSheetEntity entity) {
        DAO result = null;
        GSpreadSheetLinkRouter.RouteEntry route = GSpreadSheetLinkRouter.getRoute(entity.getaId());
        if (route != null) {
            Session session = (Session) em.getDelegate();
            Criteria crit = session.createCriteria(route.getTargetClass());
            crit.add(Restrictions.eq("aId", route.getTargetId()));
            crit.add(Restrictions.eq("cId", entity.getcId()));
            List<DAO> list = crit.list();
            if (list != null && !list.isEmpty()) {
                result = list.get(0);
            }
        }

        return result;
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
        Query gspreadsheetCollectiblesQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetscollectible g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetCollectiblesQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetscollectible set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetCollectibleDescriptionsQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetscollectibledescription g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetCollectibleDescriptionsQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetscollectibledescription set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query gspreadsheetLoadscreensQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru from gspreadsheetsloadscreen g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = gspreadsheetLoadscreensQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]).replace("\n", "$");
            if (!gTextEn.equals(eTextEn)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update gspreadsheetsloadscreen set textEn=:textEn,textRu=:textRu,translator=null,changeTime=null where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextEn);
                updateQ.executeUpdate();
            }
        }
        Query bookttextQ = em.createNativeQuery("select g.id ,g.texten as gtexten,e.texten as etexten, g.textru as textru,e.textru as etextru from booktext g join esorawstring e on e.aid=g.aid and e.bid=g.bid and e.cid=g.cid");
        resultList = bookttextQ.getResultList();
        for (Object[] row : resultList) {
            BigInteger id = (BigInteger) row[0];
            String gTextEn = ((String) row[1]);
            String eTextEn = ((String) row[2]);
            String gTextRu = ((String) row[3]);
            String eTextRu = ((String) row[4]);
            if (!eTextEn.equals(eTextRu) && gTextEn.equals(gTextRu)) {
                LOG.log(Level.INFO, "{0} -> {1}", new Object[]{gTextEn, eTextEn});
                Query updateQ = em.createNativeQuery("update booktext set textEn=:textEn,textRu=:textRu where id=:id");
                updateQ.setParameter("id", id);
                updateQ.setParameter("textEn", eTextEn);
                updateQ.setParameter("textRu", eTextRu);
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
    public void newFormatImportSubtitles(JSONObject source) {
        Pattern nameCasesPattern = Pattern.compile("(.*)\\((.*)\\)");
        Session session = (Session) em.getDelegate();
        JSONObject npcLocationObject = null;
        try {
            npcLocationObject = source.getJSONObject("subtitles");
            Iterator locationsKeys = npcLocationObject.keys();
            while (locationsKeys.hasNext()) {
                String locationName = (String) locationsKeys.next();
                Criteria sheetLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class
                );
                sheetLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", locationName), Restrictions.ilike("textRu", locationName)));
                List<GSpreadSheetsLocationName> list = sheetLocationCrit.list();
                if (list != null && !list.isEmpty()) {
                    GSpreadSheetsLocationName sheetsLocationName = list.get(0);
                    Criteria locationCrit = session.createCriteria(Location.class);
                    locationCrit.add(Restrictions.ilike("name", sheetsLocationName.getTextEn()));
                    List<Location> locations = locationCrit.list();
                    Location location = null;
                    if (locations != null && !locations.isEmpty()) {
                        location = locations.get(0);
                        if (EsnDecoder.IsMostlyRu(sheetsLocationName.getTextRu())) {
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

                    JSONObject locationSubtitlesObject = npcLocationObject.getJSONObject(locationName);
                    Iterator locationSubtitlesObjectIterator = locationSubtitlesObject.keys();
                    while (locationSubtitlesObjectIterator.hasNext()) {
                        JSONObject subtitleSet = locationSubtitlesObject.getJSONObject((String) locationSubtitlesObjectIterator.next());
                        int subtitleCount = subtitleSet.length();
                        Subtitle[] subtilteArray = new Subtitle[subtitleCount];
                        Iterator subtitleSetIterator = subtitleSet.keys();
                        while (subtitleSetIterator.hasNext()) {
                            String currentKey = (String) subtitleSetIterator.next();
                            Integer currentIndex = Integer.valueOf(currentKey);
                            JSONObject subtitleObject = subtitleSet.getJSONObject(currentKey);
                            String npcNameString = subtitleObject.getString("name");
                            String subtitleTextString = subtitleObject.getString("text");
                            String npcName = null;
                            String npcNameRu = null;
                            Matcher npcWithCasesMatcher = nameCasesPattern.matcher(npcNameString);
                            if (npcWithCasesMatcher.matches()) {
                                String group1 = npcWithCasesMatcher.group(1);
                                String group2 = npcWithCasesMatcher.group(2);
                                if (!EsnDecoder.IsMostlyRu(group1)) {
                                    npcName = group1.trim();
                                } else {
                                    npcNameRu = group2.trim();
                                }
                            } else {
                                if (EsnDecoder.IsMostlyRu(npcNameString)) {
                                    npcNameRu = npcNameString;
                                } else {
                                    npcName = npcNameString;
                                }
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
                                    if (EsnDecoder.IsMostlyRu(sheetNpc.getTextRu())) {
                                        npcNameRu = sheetNpc.getTextRu();
                                    }
                                }
                            }
                            Criteria npcCriteria = session.createCriteria(Npc.class);
                            npcCriteria.add(Restrictions.eq("location", location));
                            if (npcName != null) {
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
                            String subtitleText = null;
                            String subtitleTextRu = null;
                            if (EsnDecoder.IsMostlyRu(subtitleTextString)) {
                                subtitleTextRu = subtitleTextString;
                            } else {
                                subtitleText = subtitleTextString;

                            }
                            if ((subtitleText != null && !subtitleText.isEmpty()) || (subtitleTextRu != null && !subtitleTextRu.isEmpty())) {
                                Criteria subtitleCriteria = session.createCriteria(Subtitle.class
                                );
                                subtitleCriteria.add(Restrictions.eq("npc", currentNpc));
                                if (subtitleText != null) {
                                    subtitleCriteria.add(Restrictions.ilike("text", subtitleText));
                                }
                                if (subtitleTextRu != null) {
                                    subtitleCriteria.add(Restrictions.ilike("textRu", subtitleTextRu));
                                }
                                List<Subtitle> subtitleList = subtitleCriteria.list();
                                Subtitle subtitle = null;
                                if (subtitleList == null || subtitleList.isEmpty()) {
                                    subtitle = new Subtitle(subtitleText, subtitleTextRu, currentNpc);
                                    LOG.log(Level.INFO, "new subtitle: {0}|{1}", new String[]{subtitleText, subtitleTextRu});
                                    em.persist(subtitle);

                                } else {
                                    subtitle = subtitleList.get(0);
                                    if (subtitleTextRu != null && subtitle.getTextRu() == null) {
                                        subtitle.setTextRu(subtitleTextRu);
                                    }
                                    if (subtitleText != null && subtitle.getText() == null) {
                                        subtitle.setText(subtitleText);
                                    }
                                    em.merge(subtitle);
                                }
                                subtilteArray[currentIndex - 1] = subtitle;
                            }
                            for (int i = 1; i < subtilteArray.length; i++) {
                                Subtitle s = subtilteArray[i];
                                if (s.getPreviousSubtitle() == null) {
                                    Subtitle preS = subtilteArray[i - 1];
                                    preS.setNextSubtitle(s);
                                    em.merge(preS);
                                    s.setPreviousSubtitle(preS);
                                    em.merge(s);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException ex) {

        }
    }

    @Transactional
    public void newFormatImportNpcs(JSONObject source) {
        Pattern nameCasesPattern = Pattern.compile("(.*)\\((.*)\\)");
        Session session = (Session) em.getDelegate();
        JSONObject npcLocationObject = source.getJSONObject("npc");
        Iterator locationsKeys = npcLocationObject.keys();
        while (locationsKeys.hasNext()) {
            String locationKey = (String) locationsKeys.next();
            String locationName = null;
            Matcher locationWithCasesMatcher = nameCasesPattern.matcher(locationKey);
            if (locationWithCasesMatcher.matches()) {
                String group1 = locationWithCasesMatcher.group(1);
                String group2 = locationWithCasesMatcher.group(2);
                if (!EsnDecoder.IsMostlyRu(group1)) {
                    locationName = group1.trim();
                } else {
                    locationName = group2.trim();
                }
            } else {
                locationName = locationKey;

            }
            Criteria sheetLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class
            );
            sheetLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", locationName), Restrictions.ilike("textRu", locationName)));
            List<GSpreadSheetsLocationName> list = sheetLocationCrit.list();
            if (list != null && !list.isEmpty()) {
                GSpreadSheetsLocationName sheetsLocationName = list.get(0);
                Criteria locationCrit = session.createCriteria(Location.class
                );
                locationCrit.add(Restrictions.ilike("name", sheetsLocationName.getTextEn()));
                List<Location> locations = locationCrit.list();
                Location location = null;
                if (locations != null && !locations.isEmpty()) {
                    location = locations.get(0);
                    if (EsnDecoder.IsMostlyRu(sheetsLocationName.getTextRu())) {
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

                JSONObject npcsObject = npcLocationObject.getJSONObject(locationKey);
                Iterator npcsKeys = npcsObject.keys();
                while (npcsKeys.hasNext()) {
                    String npcKey = (String) npcsKeys.next();
                    String npcName = null;
                    String npcNameRu = null;
                    Matcher npcWithCasesMatcher = nameCasesPattern.matcher(npcKey);
                    if (npcWithCasesMatcher.matches()) {
                        String group1 = npcWithCasesMatcher.group(1);
                        String group2 = npcWithCasesMatcher.group(2);
                        if (!EsnDecoder.IsMostlyRu(group1)) {
                            npcName = group1.trim();
                        } else {
                            npcNameRu = group2.trim();
                        }
                    } else {
                        if (EsnDecoder.IsMostlyRu(npcKey)) {
                            npcNameRu = npcKey;
                        } else {
                            npcName = npcKey;
                        }
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
                            if (EsnDecoder.IsMostlyRu(sheetNpc.getTextRu())) {
                                npcNameRu = sheetNpc.getTextRu();

                            }
                        }
                    }
                    Criteria npcCriteria = session.createCriteria(Npc.class
                    );
                    npcCriteria.add(Restrictions.eq("location", location));
                    if (npcName != null) {
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

                    List<Topic> npcTopics = new ArrayList<>();
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
                            if (EsnDecoder.IsMostlyRu(greetingsObject.getString(greetingskey))) {
                                greetingTextRu = greetingsObject.getString(greetingskey);
                            } else {
                                greetingText = greetingsObject.getString(greetingskey);
                            }
                            if (greetingText != null && !greetingText.isEmpty()) {
                                Criteria greetingsCriteria = session.createCriteria(Topic.class);
                                greetingsCriteria.add(Restrictions.eq("npc", currentNpc));
                                if (greetingText != null) {
                                    greetingsCriteria.add(Restrictions.eq("npcText", greetingText));
                                }
                                if (greetingTextRu != null) {
                                    greetingsCriteria.add(Restrictions.eq("npcTextRu", greetingTextRu));
                                }
                                List<Topic> greetingList = greetingsCriteria.list();
                                if (greetingList == null || greetingList.isEmpty()) {
                                    Topic greeting = new Topic(null, greetingText, null, greetingTextRu, currentNpc);
                                    LOG.log(Level.INFO, "new greeting topic: {0}|{1}", new String[]{greetingText, greetingTextRu});
                                    em.persist(greeting);
                                    npcTopics.add(greeting);
                                } else {
                                    Topic greeting = greetingList.get(0);
                                    if (greeting.getNpcText() == null && greetingText != null) {
                                        greeting.setNpcText(greetingText);
                                        em.merge(greeting);
                                    }
                                    if (greeting.getNpcTextRu() == null && greetingTextRu != null) {
                                        greeting.setNpcTextRu(greetingTextRu);
                                        em.merge(greeting);
                                    }
                                    npcTopics.add(greeting);
                                }
                            }

                        }
                    }

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
                            if (EsnDecoder.IsMostlyRu(topickey)) {
                                playerTextRu = topickey;
                            } else {
                                playerText = topickey;
                            }
                            if (EsnDecoder.IsMostlyRu(topicsObject.getString(topickey))) {
                                npcTextRu = topicsObject.getString(topickey);
                            } else {
                                npcText = topicsObject.getString(topickey);
                            }
                            if (npcText != null && npcText.isEmpty()) {
                                npcText = null;
                            }
                            if (npcTextRu != null && npcTextRu.isEmpty()) {
                                npcTextRu = null;

                            }
                            if ((playerText != null && !playerText.isEmpty()) || (npcText != null && !npcText.isEmpty())) {
                                Criteria topicCriteria = session.createCriteria(Topic.class
                                );
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
                                    npcTopics.add(topic);
                                } else if (playerText != null || npcText != null) {
                                    Topic topic = new Topic(playerText, npcText, playerTextRu, npcTextRu, currentNpc);
                                    LOG.log(Level.INFO, "new topic: {0}|{1}|{2}|{3}", new String[]{playerText, npcText, playerTextRu, npcTextRu});
                                    em.persist(topic);
                                    npcTopics.add(topic);
                                }
                            }

                        }
                    }

                    JSONObject topicLinkObject = null;

                    try {
                        topicLinkObject = npcContent.getJSONObject("links");
                    } catch (JSONException ex) {

                    }

                    if (topicLinkObject != null) {
                        Iterator linkKeys = topicLinkObject.keys();
                        while (linkKeys.hasNext()) {
                            String npcText = (String) linkKeys.next();
                            Topic parentTopic = null;
                            for (Topic npcTopic : npcTopics) {
                                if ((npcTopic.getNpcText() != null && npcTopic.getNpcText().equals(npcText)) || (npcTopic.getNpcTextRu() != null && npcTopic.getNpcTextRu().equals(npcText))) {
                                    parentTopic = npcTopic;
                                    JSONObject nextTopicsObject = null;

                                    try {
                                        nextTopicsObject = topicLinkObject.getJSONObject(npcText);
                                    } catch (JSONException ex) {

                                    }

                                    if (nextTopicsObject != null) {
                                        Iterator nextTopicsIterator = nextTopicsObject.keys();
                                        while (nextTopicsIterator.hasNext()) {
                                            String playerText = (String) nextTopicsIterator.next();
                                            Topic childTopic = null;
                                            for (Topic npcTopic2 : npcTopics) {
                                                if ((npcTopic2.getPlayerText() != null && npcTopic2.getPlayerText().equals(playerText)) || (npcTopic2.getPlayerTextRu() != null && npcTopic2.getPlayerTextRu().equals(playerText))) {
                                                    childTopic = npcTopic2;
                                                    if (childTopic.getPreviousTopics() == null) {
                                                        childTopic.setPreviousTopics(new HashSet<Topic>());
                                                    }
                                                    childTopic.getPreviousTopics().add(parentTopic);
                                                    LOG.info("adding previous topic to " + childTopic.getId());
                                                    em.merge(childTopic);
                                                }
                                            }

                                        }
                                    }

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
                            if (EsnDecoder.IsMostlyRu(subtitlekey)) {
                                subtitleTextRu = subtitlekey;
                            } else {
                                subtitleText = subtitlekey;

                            }
                            if (subtitleText != null && !subtitleText.isEmpty()) {
                                Criteria subtitleCriteria = session.createCriteria(Subtitle.class
                                );
                                subtitleCriteria.add(Restrictions.eq("npc", currentNpc));
                                if (subtitleText != null) {
                                    subtitleCriteria.add(Restrictions.ilike("text", subtitleText));
                                }
                                if (subtitleTextRu != null) {
                                    subtitleCriteria.add(Restrictions.ilike("textRu", subtitleTextRu));
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

                }
            }

        }

    }

    @Transactional
    public void newFormatImportNpcsWithSublocations(JSONObject source) {
        Pattern nameCasesPattern = Pattern.compile("(.*)\\((.*)\\)");
        Session session = (Session) em.getDelegate();
        JSONObject npcLocationObject = source.getJSONObject("npc");
        Iterator locationsKeys = npcLocationObject.keys();
        while (locationsKeys.hasNext()) {
            String locationKey = (String) locationsKeys.next();
            String locationName = null;
            Matcher locationWithCasesMatcher = nameCasesPattern.matcher(locationKey);
            if (locationWithCasesMatcher.matches()) {
                String group1 = locationWithCasesMatcher.group(1);
                String group2 = locationWithCasesMatcher.group(2);
                if (!EsnDecoder.IsMostlyRu(group1)) {
                    locationName = group1.trim();
                } else {
                    locationName = group2.trim();
                }
            } else {
                locationName = locationKey;
            }
            Criteria sheetLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
            sheetLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", locationName), Restrictions.ilike("textRu", locationName)));
            List<GSpreadSheetsLocationName> list = sheetLocationCrit.list();
            if (list != null && !list.isEmpty()) {
                GSpreadSheetsLocationName sheetsLocationName = list.get(0);
                Criteria locationCrit = session.createCriteria(Location.class);
                locationCrit.add(Restrictions.ilike("name", sheetsLocationName.getTextEn()));
                List<Location> locations = locationCrit.list();
                Location location = null;
                if (locations != null && !locations.isEmpty()) {
                    location = locations.get(0);
                    if (EsnDecoder.IsMostlyRu(sheetsLocationName.getTextRu())) {
                        location.setNameRu(sheetsLocationName.getTextRu());
                    }
                } else {
                    location = new Location();
                    location.setProgress(BigDecimal.ZERO);

                }
                if (sheetsLocationName != null) {
                    location.setSheetsLocationName(sheetsLocationName);
                    location.setName(sheetsLocationName.getTextEn());
                    if (!sheetsLocationName.getTextEn().equals(sheetsLocationName.getTextRu())) {
                        location.setNameRu(sheetsLocationName.getTextRu());
                    }
                }
                if (location.getId() == null) {
                    LOG.log(Level.INFO, "new location: {0}", location.toString());
                    em.persist(location);
                } else {
                    LOG.log(Level.INFO, "update location: {0}", location.toString());
                    em.merge(location);
                }
                JSONObject subLocationObject = npcLocationObject.getJSONObject(locationKey);
                Iterator subLocationKeys = subLocationObject.keys();
                while (subLocationKeys.hasNext()) {
                    String subLocationKey = (String) subLocationKeys.next();
                    String subLocationName = null;
                    Location subLocation = null;
                    if (subLocationKey.equals(locationKey)) {
                        subLocation = location;
                    } else {
                        Matcher subLocationWithCasesMatcher = nameCasesPattern.matcher(subLocationKey);
                        if (subLocationWithCasesMatcher.matches()) {
                            String group1 = subLocationWithCasesMatcher.group(1);
                            String group2 = subLocationWithCasesMatcher.group(2);
                            if (!EsnDecoder.IsMostlyRu(group1)) {
                                subLocationName = group1.trim();
                            } else {
                                subLocationName = group2.trim();
                            }
                        } else {
                            subLocationName = subLocationKey;
                        }

                        Criteria sheetSubLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
                        sheetSubLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", subLocationName), Restrictions.ilike("textRu", subLocationName), Restrictions.ilike("textRu", subLocationName.replace("—", "-"))));
                        List<GSpreadSheetsLocationName> sulLocationList = sheetSubLocationCrit.list();
                        if (sulLocationList != null && !sulLocationList.isEmpty()) {
                            GSpreadSheetsLocationName sheetsSubLocationName = sulLocationList.get(0);
                            Criteria subLocationCrit = session.createCriteria(Location.class);
                            subLocationCrit.add(Restrictions.ilike("name", sheetsSubLocationName.getTextEn()));
                            List<Location> subLocations = subLocationCrit.list();

                            if (subLocations != null && !subLocations.isEmpty()) {
                                subLocation = subLocations.get(0);
                                if (EsnDecoder.IsMostlyRu(sheetsSubLocationName.getTextRu())) {
                                    subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                }
                            } else {
                                subLocation = new Location();
                                subLocation.setProgress(BigDecimal.ZERO);

                            }
                            subLocation.setParentLocation(location);
                            if (sheetsSubLocationName != null) {
                                subLocation.setSheetsLocationName(sheetsSubLocationName);
                                subLocation.setName(sheetsSubLocationName.getTextEn());
                                if (!sheetsSubLocationName.getTextEn().equals(sheetsSubLocationName.getTextRu())) {
                                    subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                }
                            }
                            if (subLocation.getId() == null) {
                                LOG.log(Level.INFO, "new sublocation: " + subLocation.toString() + "/" + location.toString());
                                em.persist(subLocation);
                            } else {
                                LOG.log(Level.INFO, "update sublocation: " + subLocation.toString() + "/" + location.toString());
                                em.merge(subLocation);
                            }
                        }
                    }
                    JSONObject npcsObject = subLocationObject.getJSONObject(subLocationKey);
                    Iterator npcsKeys = npcsObject.keys();
                    while (npcsKeys.hasNext()) {
                        String npcKey = (String) npcsKeys.next();
                        String npcName = null;
                        String npcNameRu = null;
                        Matcher npcWithCasesMatcher = nameCasesPattern.matcher(npcKey);
                        if (npcWithCasesMatcher.matches()) {
                            String group1 = npcWithCasesMatcher.group(1);
                            String group2 = npcWithCasesMatcher.group(2);
                            if (!EsnDecoder.IsMostlyRu(group1)) {
                                npcName = group1.trim();
                            } else {
                                npcNameRu = group2.trim();
                            }
                        } else {
                            if (EsnDecoder.IsMostlyRu(npcKey)) {
                                npcNameRu = npcKey;
                            } else {
                                npcName = npcKey;
                            }
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
                                if (EsnDecoder.IsMostlyRu(sheetNpc.getTextRu())) {
                                    npcNameRu = sheetNpc.getTextRu();
                                }
                            }
                        }
                        Criteria npcCriteria = session.createCriteria(Npc.class);
                        npcCriteria.add(Restrictions.eq("location", subLocation));
                        if (npcName != null) {
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
                            currentNpc.setLocation(subLocation);
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

                        List<Topic> npcTopics = new ArrayList<>();
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
                                Integer weight = Integer.valueOf(greetingskey);
                                weight = weight * 1000;
                                Topic greetingTopic = null;
                                Criteria greetingsCriteria0 = session.createCriteria(Topic.class);
                                greetingsCriteria0.add(Restrictions.eq("npc", currentNpc));
                                greetingsCriteria0.add(Restrictions.or(Restrictions.ilike("npcText", greetingsObject.getString(greetingskey)), Restrictions.ilike("npcTextRu", greetingsObject.getString(greetingskey))));
                                List<Topic> greetingList = greetingsCriteria0.list();
                                if (greetingList != null && !greetingList.isEmpty()) {
                                    greetingTopic = greetingList.get(0);
                                }
                                GSpreadSheetsNpcPhrase greetingExtPhrase = null;
                                Long greetingExtPhraseId = null;
                                if (greetingTopic == null) {

                                    if (EsnDecoder.IsRu(greetingsObject.getString(greetingskey))) {
                                        greetingTextRu = greetingsObject.getString(greetingskey);
                                        greetingExtPhraseId = searchTableItemRuIndexed("GSpreadSheetsNpcPhrase", greetingTextRu);
                                    } else if (EsnDecoder.IsEn(greetingsObject.getString(greetingskey))) {
                                        greetingText = greetingsObject.getString(greetingskey);
                                        greetingExtPhraseId = searchTableItemIndexed("GSpreadSheetsNpcPhrase", greetingText);
                                    } else {
                                        greetingText = greetingsObject.getString(greetingskey);
                                        greetingExtPhraseId = searchTableItemUncertainIndexed("GSpreadSheetsNpcPhrase", greetingText);
                                    }
                                    if (greetingExtPhraseId != null) {
                                        greetingExtPhrase = em.find(GSpreadSheetsNpcPhrase.class, greetingExtPhraseId);
                                    }
                                    Criteria greetingsCriteria = session.createCriteria(Topic.class);
                                    greetingsCriteria.add(Restrictions.eq("npc", currentNpc));
                                    if (greetingExtPhrase != null) {
                                        greetingsCriteria.add(Restrictions.eq("extNpcPhrase", greetingExtPhrase));
                                    } else if (greetingText != null) {
                                        greetingsCriteria.add(Restrictions.ilike("npcText", greetingText));
                                    } else if (greetingTextRu != null) {
                                        greetingsCriteria.add(Restrictions.ilike("npcTextRu", greetingTextRu));
                                    }
                                    greetingList = greetingsCriteria.list();
                                    if (greetingList != null && !greetingList.isEmpty()) {
                                        greetingTopic = greetingList.get(0);
                                    }

                                }
                                if (greetingTopic == null) {
                                    greetingTopic = new Topic(null, greetingText, null, greetingTextRu, currentNpc);
                                    LOG.log(Level.INFO, "new greeting topic: {0}|{1}", new String[]{greetingText, greetingTextRu});
                                    if (greetingExtPhrase != null) {
                                        greetingTopic.setExtNpcPhrase(greetingExtPhrase);
                                    }
                                    greetingTopic.setWeight(weight);
                                    em.persist(greetingTopic);
                                    npcTopics.add(greetingTopic);
                                } else {
                                    if (greetingTopic.getNpcText() == null && greetingText != null) {
                                        greetingTopic.setNpcText(greetingText);
                                        em.merge(greetingTopic);
                                    }
                                    if (greetingTopic.getNpcTextRu() == null && greetingTextRu != null) {
                                        greetingTopic.setNpcTextRu(greetingTextRu);
                                        em.merge(greetingTopic);
                                    }
                                    if (greetingTopic.getWeight() == null || greetingTopic.getWeight() < weight) {
                                        greetingTopic.setWeight(weight);
                                        em.merge(greetingTopic);
                                    }
                                    npcTopics.add(greetingTopic);
                                }

                            }
                        }

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
                                Topic topic = null;
                                Criteria topicCriteria0 = session.createCriteria(Topic.class);
                                topicCriteria0.add(Restrictions.eq("npc", currentNpc));
                                topicCriteria0.add(Restrictions.or(
                                        Restrictions.ilike("playerText", topickey),
                                        Restrictions.ilike("playerTextRu", topickey)
                                ));
                                topicCriteria0.add(Restrictions.or(
                                        Restrictions.ilike("npcText", topicsObject.getString(topickey)),
                                        Restrictions.ilike("npcTextRu", topicsObject.getString(topickey)),
                                        Restrictions.and(Restrictions.isNull("npcText"), Restrictions.isNull("npcTextRu"))
                                ));
                                List<Topic> topicList = topicCriteria0.list();
                                if (topicList != null && !topicList.isEmpty()) {
                                    topic = topicList.get(0);
                                }
                                GSpreadSheetsNpcPhrase npcExtPhrase = null;
                                Long npcExtPhraseId = null;
                                GSpreadSheetsPlayerPhrase playerExtPhrase = null;
                                Long playerExtPhraseId = null;

                                if (topic == null) {
                                    if (EsnDecoder.IsRu(topickey)) {
                                        playerTextRu = topickey;
                                        playerExtPhraseId = searchTableItemRuIndexed("GSpreadSheetsPlayerPhrase", playerTextRu);
                                    } else if (EsnDecoder.IsEn(topickey)) {
                                        playerText = topickey;
                                        playerExtPhraseId = searchTableItemIndexed("GSpreadSheetsPlayerPhrase", playerText);
                                    } else {
                                        playerText = topickey;
                                        playerExtPhraseId = searchTableItemUncertainIndexed("GSpreadSheetsPlayerPhrase", playerText);
                                    }
                                    if (EsnDecoder.IsRu(topicsObject.getString(topickey))) {
                                        npcTextRu = topicsObject.getString(topickey);
                                        npcExtPhraseId = searchTableItemRuIndexed("GSpreadSheetsNpcPhrase", npcTextRu);
                                    } else if (EsnDecoder.IsEn(topicsObject.getString(topickey))) {
                                        npcText = topicsObject.getString(topickey);
                                        npcExtPhraseId = searchTableItemIndexed("GSpreadSheetsNpcPhrase", npcText);
                                    } else {
                                        npcText = topicsObject.getString(topickey);
                                        npcExtPhraseId = searchTableItemUncertainIndexed("GSpreadSheetsNpcPhrase", npcText);
                                    }
                                    if (npcExtPhraseId != null) {
                                        npcExtPhrase = em.find(GSpreadSheetsNpcPhrase.class, npcExtPhraseId);
                                    }
                                    if (playerExtPhraseId != null) {
                                        playerExtPhrase = em.find(GSpreadSheetsPlayerPhrase.class, playerExtPhraseId);
                                    }
                                    if (npcText != null && npcText.isEmpty()) {
                                        npcText = null;
                                    }
                                    if (npcTextRu != null && npcTextRu.isEmpty()) {
                                        npcTextRu = null;
                                    }
                                    Criteria topicCriteria = session.createCriteria(Topic.class);
                                    topicCriteria.add(Restrictions.eq("npc", currentNpc));
                                    if (playerExtPhrase != null) {
                                        topicCriteria.add(Restrictions.eq("extPlayerPhrase", playerExtPhrase));
                                    } else if (playerText != null) {
                                        topicCriteria.add(Restrictions.ilike("playerText", playerText));
                                    } else if (playerTextRu != null) {
                                        topicCriteria.add(Restrictions.ilike("playerTextRu", playerTextRu));
                                    }
                                    if (npcExtPhrase != null) {
                                        topicCriteria.add(Restrictions.or(Restrictions.eq("extNpcPhrase", npcExtPhrase), Restrictions.isNull("extNpcPhrase")));
                                    } else if (npcText != null) {
                                        topicCriteria.add(Restrictions.or(Restrictions.ilike("npcText", npcText), Restrictions.isNull("npcText")));
                                    } else if (npcTextRu != null) {
                                        topicCriteria.add(Restrictions.or(Restrictions.ilike("npcTextRu", npcTextRu), Restrictions.isNull("npcTextRu")));
                                    }
                                    topicList = topicCriteria.list();
                                    if (topicList != null && !topicList.isEmpty()) {
                                        topic = topicList.get(0);
                                    }

                                }
                                if (topic != null) {
                                    if (playerExtPhrase != null) {
                                        topic.setExtPlayerPhrase(playerExtPhrase);
                                        em.merge(topic);
                                    }
                                    if (npcExtPhrase != null) {
                                        topic.setExtNpcPhrase(npcExtPhrase);
                                        em.merge(topic);
                                    }
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
                                    npcTopics.add(topic);
                                } else if (playerText != null || npcText != null || playerTextRu != null || npcTextRu != null) {
                                    topic = new Topic(playerText, npcText, playerTextRu, npcTextRu, currentNpc);
                                    LOG.log(Level.INFO, "new topic: {0}|{1}|{2}|{3}", new String[]{playerText, npcText, playerTextRu, npcTextRu});
                                    if (playerExtPhrase != null) {
                                        topic.setExtPlayerPhrase(playerExtPhrase);
                                    }
                                    if (npcExtPhrase != null) {
                                        topic.setExtNpcPhrase(npcExtPhrase);
                                    }
                                    em.persist(topic);
                                    npcTopics.add(topic);
                                }

                            }
                        }

                        JSONObject topicLinkObject = null;

                        try {
                            topicLinkObject = npcContent.getJSONObject("links");
                        } catch (JSONException ex) {

                        }

                        if (topicLinkObject != null) {
                            Iterator linkKeys = topicLinkObject.keys();
                            while (linkKeys.hasNext()) {
                                String npcText = (String) linkKeys.next();
                                Topic parentTopic = null;
                                for (Topic npcTopic : npcTopics) {
                                    if ((npcTopic.getNpcText() != null && npcTopic.getNpcText().equals(npcText)) || (npcTopic.getNpcTextRu() != null && npcTopic.getNpcTextRu().equals(npcText))) {
                                        parentTopic = npcTopic;
                                        JSONObject nextTopicsObject = null;

                                        try {
                                            nextTopicsObject = topicLinkObject.getJSONObject(npcText);
                                        } catch (JSONException ex) {

                                        }

                                        if (nextTopicsObject != null) {
                                            Iterator nextTopicsIterator = nextTopicsObject.keys();
                                            while (nextTopicsIterator.hasNext()) {
                                                String playerText = (String) nextTopicsIterator.next();
                                                Topic childTopic = null;
                                                for (Topic npcTopic2 : npcTopics) {
                                                    if ((npcTopic2.getPlayerText() != null && npcTopic2.getPlayerText().equals(playerText)) || (npcTopic2.getPlayerTextRu() != null && npcTopic2.getPlayerTextRu().equals(playerText))) {
                                                        childTopic = npcTopic2;
                                                        if (childTopic.getPreviousTopics() == null) {
                                                            childTopic.setPreviousTopics(new HashSet<Topic>());
                                                        }
                                                        if (parentTopic.getWeight() != null && childTopic.getWeight() == null) {
                                                            childTopic.setWeight(parentTopic.getWeight() + 1);
                                                        }
                                                        childTopic.getPreviousTopics().add(parentTopic);
                                                        LOG.info("adding previous topic to " + childTopic.getId());
                                                        em.merge(childTopic);
                                                    }
                                                }

                                            }
                                        }

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
                                Subtitle subtitle = null;
                                Criteria subtitleCriteria0 = session.createCriteria(Subtitle.class);
                                subtitleCriteria0.add(Restrictions.eq("npc", currentNpc));
                                subtitleCriteria0.add(Restrictions.or(Restrictions.ilike("text", subtitlekey), Restrictions.ilike("textRu", subtitlekey)));
                                List<Subtitle> subtitleList = subtitleCriteria0.list();
                                if (subtitleList != null && !subtitleList.isEmpty()) {
                                    subtitle = subtitleList.get(0);
                                }
                                GSpreadSheetsNpcPhrase subtitleExtPhrase = null;
                                Long subtitleExtPhraseId = null;
                                if (subtitle == null) {
                                    if (EsnDecoder.IsRu(subtitlekey)) {
                                        subtitleTextRu = subtitlekey;
                                        subtitleExtPhraseId = searchTableItemRuIndexed("GSpreadSheetsNpcPhrase", subtitleTextRu);
                                    } else if (EsnDecoder.IsEn(subtitlekey)) {
                                        subtitleText = subtitlekey;
                                        subtitleExtPhraseId = searchTableItemIndexed("GSpreadSheetsNpcPhrase", subtitleText);
                                    } else {
                                        subtitleText = subtitlekey;
                                        subtitleExtPhraseId = searchTableItemUncertainIndexed("GSpreadSheetsNpcPhrase", subtitleText);
                                    }
                                    if (subtitleExtPhraseId != null) {
                                        subtitleExtPhrase = em.find(GSpreadSheetsNpcPhrase.class, subtitleExtPhraseId);
                                    }
                                    Criteria subtitleCriteria = session.createCriteria(Subtitle.class);
                                    subtitleCriteria.add(Restrictions.eq("npc", currentNpc));
                                    if (subtitleExtPhrase != null) {
                                        subtitleCriteria.add(Restrictions.eq("extNpcPhrase", subtitleExtPhrase));
                                    } else if (subtitleText != null) {
                                        subtitleCriteria.add(Restrictions.ilike("text", subtitleText));
                                    } else if (subtitleTextRu != null) {
                                        subtitleCriteria.add(Restrictions.ilike("textRu", subtitleTextRu));
                                    }
                                    subtitleList = subtitleCriteria.list();
                                    if (subtitleList != null && !subtitleList.isEmpty()) {
                                        subtitle = subtitleList.get(0);
                                    }
                                }
                                if (subtitle == null) {
                                    subtitle = new Subtitle(subtitleText, subtitleTextRu, currentNpc);
                                    if (subtitleExtPhrase != null) {
                                        subtitle.setExtNpcPhrase(subtitleExtPhrase);
                                    }
                                    LOG.log(Level.INFO, "new subtitle: {0}|{1}", new String[]{subtitleText, subtitleTextRu});
                                    em.persist(subtitle);
                                }

                            }
                        }

                    }

                }

            }

        }

    }

    @Transactional
    public void newFormatImportSubtitlesWithSublocations(JSONObject source) {
        Pattern nameCasesPattern = Pattern.compile("(.*)\\((.*)\\)");
        Session session = (Session) em.getDelegate();
        JSONObject npcLocationObject = null;
        try {
            npcLocationObject = source.getJSONObject("subtitles");
            Iterator locationsKeys = npcLocationObject.keys();
            while (locationsKeys.hasNext()) {
                String locationName = (String) locationsKeys.next();
                Criteria sheetLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
                sheetLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", locationName), Restrictions.ilike("textRu", locationName)));
                List<GSpreadSheetsLocationName> list = sheetLocationCrit.list();
                if (list != null && !list.isEmpty()) {
                    GSpreadSheetsLocationName sheetsLocationName = list.get(0);
                    Criteria locationCrit = session.createCriteria(Location.class);
                    locationCrit.add(Restrictions.ilike("name", sheetsLocationName.getTextEn()));
                    List<Location> locations = locationCrit.list();
                    Location location = null;
                    if (locations != null && !locations.isEmpty()) {
                        location = locations.get(0);
                        if (EsnDecoder.IsMostlyRu(sheetsLocationName.getTextRu())) {
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
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationName);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        String subLocationName = null;
                        Location subLocation = null;
                        if (subLocationKey.equals(locationName)) {
                            subLocation = location;
                        } else {
                            Matcher subLocationWithCasesMatcher = nameCasesPattern.matcher(subLocationKey);
                            if (subLocationWithCasesMatcher.matches()) {
                                String group1 = subLocationWithCasesMatcher.group(1);
                                String group2 = subLocationWithCasesMatcher.group(2);
                                if (!EsnDecoder.IsMostlyRu(group1)) {
                                    subLocationName = group1.trim();
                                } else {
                                    subLocationName = group2.trim();
                                }
                            } else {
                                subLocationName = subLocationKey;
                            }

                            Criteria sheetSubLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
                            sheetSubLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", subLocationName), Restrictions.ilike("textRu", subLocationName)));
                            List<GSpreadSheetsLocationName> sulLocationList = sheetSubLocationCrit.list();
                            if (sulLocationList != null && !sulLocationList.isEmpty()) {
                                GSpreadSheetsLocationName sheetsSubLocationName = sulLocationList.get(0);
                                Criteria subLocationCrit = session.createCriteria(Location.class);
                                subLocationCrit.add(Restrictions.ilike("name", sheetsSubLocationName.getTextEn()));
                                List<Location> subLocations = subLocationCrit.list();

                                if (subLocations != null && !subLocations.isEmpty()) {
                                    subLocation = subLocations.get(0);
                                    if (EsnDecoder.IsMostlyRu(sheetsSubLocationName.getTextRu())) {
                                        subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                    }
                                } else {
                                    subLocation = new Location();
                                    subLocation.setProgress(BigDecimal.ZERO);

                                }
                                subLocation.setParentLocation(location);
                                if (sheetsSubLocationName != null) {
                                    subLocation.setSheetsLocationName(sheetsSubLocationName);
                                    subLocation.setName(sheetsSubLocationName.getTextEn());
                                    if (!sheetsSubLocationName.getTextEn().equals(sheetsSubLocationName.getTextRu())) {
                                        subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                    }
                                }
                                if (subLocation.getId() == null) {
                                    LOG.log(Level.INFO, "new sublocation: " + subLocation.toString() + "/" + location.toString());
                                    em.persist(subLocation);
                                } else {
                                    LOG.log(Level.INFO, "update sublocation: " + subLocation.toString() + "/" + location.toString());
                                    em.merge(subLocation);
                                }
                            }
                        }

                        JSONObject locationSubtitlesObject = subLocationObject.getJSONObject(subLocationKey);
                        Iterator locationSubtitlesObjectIterator = locationSubtitlesObject.keys();
                        while (locationSubtitlesObjectIterator.hasNext()) {
                            JSONObject subtitleSet = locationSubtitlesObject.getJSONObject((String) locationSubtitlesObjectIterator.next());
                            int subtitleCount = subtitleSet.length();
                            Subtitle[] subtilteArray = new Subtitle[subtitleCount];
                            Iterator subtitleSetIterator = subtitleSet.keys();
                            while (subtitleSetIterator.hasNext()) {
                                String currentKey = (String) subtitleSetIterator.next();
                                Integer currentIndex = Integer.valueOf(currentKey);
                                JSONObject subtitleObject = subtitleSet.getJSONObject(currentKey);
                                String npcNameString = subtitleObject.getString("name");
                                String subtitleTextString = subtitleObject.getString("text");
                                String npcName = null;
                                String npcNameRu = null;
                                Matcher npcWithCasesMatcher = nameCasesPattern.matcher(npcNameString);
                                if (npcWithCasesMatcher.matches()) {
                                    String group1 = npcWithCasesMatcher.group(1);
                                    String group2 = npcWithCasesMatcher.group(2);
                                    if (!EsnDecoder.IsMostlyRu(group1)) {
                                        npcName = group1.trim();
                                    } else {
                                        npcNameRu = group2.trim();
                                    }
                                } else {
                                    if (EsnDecoder.IsMostlyRu(npcNameString)) {
                                        npcNameRu = npcNameString;
                                    } else {
                                        npcName = npcNameString;
                                    }
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
                                        if (EsnDecoder.IsMostlyRu(sheetNpc.getTextRu())) {
                                            npcNameRu = sheetNpc.getTextRu();
                                        }
                                    }
                                }
                                Criteria npcCriteria = session.createCriteria(Npc.class);
                                npcCriteria.add(Restrictions.eq("location", subLocation));
                                if (npcName != null) {
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
                                    currentNpc.setLocation(subLocation);
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
                                String subtitleText = null;
                                String subtitleTextRu = null;
                                Subtitle subtitle = null;
                                Criteria subtitleCriteria0 = session.createCriteria(Subtitle.class);
                                subtitleCriteria0.add(Restrictions.eq("npc", currentNpc));
                                subtitleCriteria0.add(Restrictions.or(Restrictions.ilike("text", subtitleTextString), Restrictions.ilike("textRu", subtitleTextString)));
                                List<Subtitle> subtitleList = subtitleCriteria0.list();
                                if (subtitleList != null && !subtitleList.isEmpty()) {
                                    subtitle = subtitleList.get(0);
                                }
                                GSpreadSheetsNpcPhrase subtitleExtPhrase = null;
                                Long subtitleExtPhraseId = null;

                                if (subtitle == null) {
                                    if (EsnDecoder.IsRu(subtitleTextString)) {
                                        subtitleTextRu = subtitleTextString;
                                        subtitleExtPhraseId = searchTableItemRuIndexed("GSpreadSheetsNpcPhrase", subtitleTextRu);
                                    } else if (EsnDecoder.IsEn(subtitleTextString)) {
                                        subtitleText = subtitleTextString;
                                        subtitleExtPhraseId = searchTableItemIndexed("GSpreadSheetsNpcPhrase", subtitleText);
                                    } else {
                                        subtitleText = subtitleTextString;
                                        subtitleExtPhraseId = searchTableItemUncertainIndexed("GSpreadSheetsNpcPhrase", subtitleText);
                                    }
                                    if (subtitleExtPhraseId != null) {
                                        subtitleExtPhrase = em.find(GSpreadSheetsNpcPhrase.class, subtitleExtPhraseId);
                                    }
                                    Criteria subtitleCriteria = session.createCriteria(Subtitle.class);
                                    subtitleCriteria.add(Restrictions.eq("npc", currentNpc));
                                    if (subtitleExtPhrase != null) {
                                        subtitleCriteria.add(Restrictions.eq("extNpcPhrase", subtitleExtPhrase));
                                    } else if (subtitleText != null) {
                                        subtitleCriteria.add(Restrictions.ilike("text", subtitleText));
                                    } else if (subtitleTextRu != null) {
                                        subtitleCriteria.add(Restrictions.ilike("textRu", subtitleTextRu));
                                    }
                                    subtitleList = subtitleCriteria.list();
                                    if (subtitleList != null && !subtitleList.isEmpty()) {
                                        subtitle = subtitleList.get(0);
                                    }

                                }
                                if (subtitleList == null || subtitleList.isEmpty()) {
                                    subtitle = new Subtitle(subtitleText, subtitleTextRu, currentNpc);
                                    if (subtitleExtPhrase != null) {
                                        subtitle.setExtNpcPhrase(subtitleExtPhrase);
                                    }
                                    LOG.log(Level.INFO, "new subtitle: {0}|{1}", new String[]{subtitleText, subtitleTextRu});
                                    em.persist(subtitle);

                                } else {
                                    subtitle = subtitleList.get(0);
                                    if (subtitleExtPhrase != null) {
                                        subtitle.setExtNpcPhrase(subtitleExtPhrase);
                                    }
                                    if (subtitleTextRu != null && subtitle.getTextRu() == null) {
                                        subtitle.setTextRu(subtitleTextRu);
                                    }
                                    if (subtitleText != null && subtitle.getText() == null) {
                                        subtitle.setText(subtitleText);
                                    }
                                    em.merge(subtitle);
                                }

                                subtilteArray[currentIndex - 1] = subtitle;

                            }
                            for (int i = 1; i < subtilteArray.length; i++) {
                                Subtitle s = subtilteArray[i];
                                if (s.getPreviousSubtitle() == null) {
                                    Subtitle preS = subtilteArray[i - 1];
                                    preS.setNextSubtitle(s);
                                    em.merge(preS);
                                    s.setPreviousSubtitle(preS);
                                    em.merge(s);
                                }
                            }
                        }
                    }

                }
            }
        } catch (JSONException ex) {

        }
    }

    @Transactional
    public void newFormatImportQuestsWithSublocations(JSONObject source) {
        Pattern nameCasesPattern = Pattern.compile("(.*)\\((.*)\\)");
        Session session = (Session) em.getDelegate();
        JSONObject npcLocationObject = null;
        try {
            npcLocationObject = source.getJSONObject("quest");
            Iterator locationsKeys = npcLocationObject.keys();
            while (locationsKeys.hasNext()) {
                String locationName = (String) locationsKeys.next();
                Criteria sheetLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
                sheetLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", locationName), Restrictions.ilike("textRu", locationName)));
                List<GSpreadSheetsLocationName> list = sheetLocationCrit.list();
                if (list != null && !list.isEmpty()) {
                    GSpreadSheetsLocationName sheetsLocationName = list.get(0);
                    Criteria locationCrit = session.createCriteria(Location.class);
                    locationCrit.add(Restrictions.ilike("name", sheetsLocationName.getTextEn()));
                    List<Location> locations = locationCrit.list();
                    Location location = null;
                    if (locations != null && !locations.isEmpty()) {
                        location = locations.get(0);
                        if (EsnDecoder.IsMostlyRu(sheetsLocationName.getTextRu())) {
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
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationName);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        String subLocationName = null;
                        Location subLocation = null;
                        if (subLocationKey.equals(locationName)) {
                            subLocation = location;
                        } else {
                            Matcher subLocationWithCasesMatcher = nameCasesPattern.matcher(subLocationKey);
                            if (subLocationWithCasesMatcher.matches()) {
                                String group1 = subLocationWithCasesMatcher.group(1);
                                String group2 = subLocationWithCasesMatcher.group(2);
                                if (!EsnDecoder.IsMostlyRu(group1)) {
                                    subLocationName = group1.trim();
                                } else {
                                    subLocationName = group2.trim();
                                }
                            } else {
                                subLocationName = subLocationKey;
                            }

                            Criteria sheetSubLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
                            sheetSubLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", subLocationName), Restrictions.ilike("textRu", subLocationName)));
                            List<GSpreadSheetsLocationName> sulLocationList = sheetSubLocationCrit.list();
                            if (sulLocationList != null && !sulLocationList.isEmpty()) {
                                GSpreadSheetsLocationName sheetsSubLocationName = sulLocationList.get(0);
                                Criteria subLocationCrit = session.createCriteria(Location.class);
                                subLocationCrit.add(Restrictions.ilike("name", sheetsSubLocationName.getTextEn()));
                                List<Location> subLocations = subLocationCrit.list();

                                if (subLocations != null && !subLocations.isEmpty()) {
                                    subLocation = subLocations.get(0);
                                    if (EsnDecoder.IsMostlyRu(sheetsSubLocationName.getTextRu())) {
                                        subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                    }
                                } else {
                                    subLocation = new Location();
                                    subLocation.setProgress(BigDecimal.ZERO);

                                }
                                subLocation.setParentLocation(location);
                                if (sheetsSubLocationName != null) {
                                    subLocation.setSheetsLocationName(sheetsSubLocationName);
                                    subLocation.setName(sheetsSubLocationName.getTextEn());
                                    if (!sheetsSubLocationName.getTextEn().equals(sheetsSubLocationName.getTextRu())) {
                                        subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                    }
                                }
                                if (subLocation.getId() == null) {
                                    LOG.log(Level.INFO, "new sublocation: " + subLocation.toString() + "/" + location.toString());
                                    em.persist(subLocation);
                                } else {
                                    LOG.log(Level.INFO, "update sublocation: " + subLocation.toString() + "/" + location.toString());
                                    em.merge(subLocation);
                                }
                            }
                        }

                        JSONObject locationQuestsObject = subLocationObject.getJSONObject(subLocationKey);
                        Iterator locationQuestsObjectIterator = locationQuestsObject.keys();
                        while (locationQuestsObjectIterator.hasNext()) {
                            String questKey = (String) locationQuestsObjectIterator.next();
                            JSONObject questObject = locationQuestsObject.getJSONObject(questKey);
                            String questNameEn = null;
                            String questNameRu = null;
                            if (EsnDecoder.IsMostlyRu(questKey)) {
                                questNameRu = questKey;
                            } else {
                                questNameEn = questKey;
                            }
                            Criteria questNameCrit = session.createCriteria(GSpreadSheetsQuestName.class);
                            if (questNameEn != null) {
                                questNameCrit.add(Restrictions.ilike("textEn", questNameEn));
                            } else {
                                questNameCrit.add(Restrictions.ilike("textRu", questNameRu));
                            }
                            List<GSpreadSheetsQuestName> questNameList = questNameCrit.list();
                            if (questNameList != null && !questNameList.isEmpty()) {
                                GSpreadSheetsQuestName sheetsQuestName = questNameList.get(0);
                                Quest quest = null;
                                Criteria questBySheetCrit = session.createCriteria(Quest.class);
                                questBySheetCrit.add(Restrictions.eq("sheetsQuestName", sheetsQuestName));
                                List<Quest> questBySheet = questBySheetCrit.list();
                                if (questBySheet != null && !questBySheet.isEmpty()) {
                                    quest = questBySheet.get(0);
                                } else {
                                    Criteria questByNameCrit = session.createCriteria(Quest.class);
                                    questByNameCrit.add(Restrictions.ilike("name", sheetsQuestName.getTextEn()));
                                    List<Quest> questByName = questByNameCrit.list();
                                    if (questByName != null && !questByName.isEmpty()) {
                                        quest = questByName.get(0);
                                    }
                                }
                                if (quest != null) {
                                    if (!sheetsQuestName.getTextEn().equals(sheetsQuestName.getTextRu()) && (quest.getNameRu() == null || quest.getNameRu().isEmpty())) {
                                        quest.setNameRu(sheetsQuestName.getTextRu());
                                    }
                                    if (quest.getName() == null || quest.getName().isEmpty()) {
                                        quest.setName(sheetsQuestName.getTextEn());
                                    }
                                    if (quest.getLocation() == null) {
                                        quest.setLocation(location);
                                    }
                                    em.merge(quest);

                                } else {
                                    quest = new Quest();
                                    quest.setLocation(location);
                                    quest.setName(sheetsQuestName.getTextEn());
                                    if (!sheetsQuestName.getTextEn().equals(sheetsQuestName.getTextRu())) {
                                        quest.setNameRu(sheetsQuestName.getTextRu());
                                    }
                                    quest.setProgress(BigDecimal.ZERO);
                                    em.persist(quest);
                                }
                                JSONObject questStepsObject = null;
                                try {
                                    questStepsObject = questObject.getJSONObject("StepText");
                                } catch (JSONException ex) {

                                }
                                if (questStepsObject != null) {
                                    Iterator questStepsIterator = questStepsObject.keys();
                                    while (questStepsIterator.hasNext()) {
                                        String questStep = (String) questStepsIterator.next();
                                        String questStepEn = null;
                                        String questStepRu = null;

                                    }
                                }
                            }

                        }
                    }

                }
            }
        } catch (JSONException ex) {

        }
    }

    @Transactional
    public void newFormatImportQuestsWithSteps(JSONObject source) {
        Pattern nameCasesPattern = Pattern.compile("(.*)\\((.*)\\)");
        Pattern goalWithCounterPattern = Pattern.compile("(.*):.\\d+.\\/.\\d");
        Session session = (Session) em.getDelegate();
        JSONObject locationObject = null;
        try {
            locationObject = source.getJSONObject("quest");
            Iterator locationsKeys = locationObject.keys();
            while (locationsKeys.hasNext()) {
                String locationName = (String) locationsKeys.next();
                Criteria sheetLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
                sheetLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", locationName), Restrictions.ilike("textRu", locationName)));
                List<GSpreadSheetsLocationName> list = sheetLocationCrit.list();
                if (list != null && !list.isEmpty()) {
                    GSpreadSheetsLocationName sheetsLocationName = list.get(0);
                    Criteria locationCrit = session.createCriteria(Location.class);
                    locationCrit.add(Restrictions.ilike("name", sheetsLocationName.getTextEn()));
                    List<Location> locations = locationCrit.list();
                    Location location = null;
                    if (locations != null && !locations.isEmpty()) {
                        location = locations.get(0);
                        if (EsnDecoder.IsMostlyRu(sheetsLocationName.getTextRu())) {
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

                    JSONObject locationQuestsObject = locationObject.getJSONObject(locationName);
                    Iterator locationQuestsObjectIterator = locationQuestsObject.keys();
                    while (locationQuestsObjectIterator.hasNext()) {
                        String questKey = (String) locationQuestsObjectIterator.next();
                        JSONObject questObject = locationQuestsObject.getJSONObject(questKey);
                        String questNameEn = null;
                        String questNameRu = null;
                        if (EsnDecoder.IsMostlyRu(questKey)) {
                            questNameRu = questKey;
                        } else {
                            questNameEn = questKey;
                        }
                        Criteria questNameCrit = session.createCriteria(GSpreadSheetsQuestName.class);
                        if (questNameEn != null) {
                            questNameCrit.add(Restrictions.ilike("textEn", questNameEn));
                        } else {
                            questNameCrit.add(Restrictions.ilike("textRu", questNameRu));
                        }
                        List<GSpreadSheetsQuestName> questNameList = questNameCrit.list();
                        if (questNameList != null && !questNameList.isEmpty()) {
                            GSpreadSheetsQuestName sheetsQuestName = questNameList.get(0);
                            Quest quest = null;
                            Criteria questBySheetCrit = session.createCriteria(Quest.class);
                            questBySheetCrit.add(Restrictions.eq("sheetsQuestName", sheetsQuestName));
                            List<Quest> questBySheet = questBySheetCrit.list();
                            if (questBySheet != null && !questBySheet.isEmpty()) {
                                quest = questBySheet.get(0);
                            } else {
                                Criteria questByNameCrit = session.createCriteria(Quest.class);
                                questByNameCrit.add(Restrictions.ilike("name", sheetsQuestName.getTextEn()));
                                List<Quest> questByName = questByNameCrit.list();
                                if (questByName != null && !questByName.isEmpty()) {
                                    quest = questByName.get(0);
                                }
                            }
                            if (quest != null) {
                                if (!sheetsQuestName.getTextEn().equals(sheetsQuestName.getTextRu()) && (quest.getNameRu() == null || quest.getNameRu().isEmpty())) {
                                    quest.setNameRu(sheetsQuestName.getTextRu());
                                }
                                if (quest.getName() == null || quest.getName().isEmpty()) {
                                    quest.setName(sheetsQuestName.getTextEn());
                                }
                                if (quest.getLocation() == null) {
                                    quest.setLocation(location);
                                }
                                if (quest.getSheetsQuestName() == null) {
                                    quest.setSheetsQuestName(sheetsQuestName);
                                }
                                em.merge(quest);

                            } else {
                                quest = new Quest();
                                quest.setLocation(location);
                                quest.setName(sheetsQuestName.getTextEn());
                                if (!sheetsQuestName.getTextEn().equals(sheetsQuestName.getTextRu())) {
                                    quest.setNameRu(sheetsQuestName.getTextRu());
                                }
                                quest.setProgress(BigDecimal.ZERO);
                                quest.setSheetsQuestName(sheetsQuestName);
                                em.persist(quest);
                            }
                            JSONObject questStepsObject = null;
                            try {
                                questStepsObject = questObject.getJSONObject("steps");
                            } catch (JSONException ex) {

                            }
                            if (questStepsObject != null) {
                                Iterator questStepsIterator = questStepsObject.keys();
                                while (questStepsIterator.hasNext()) {
                                    String stepKey = (String) questStepsIterator.next();
                                    JSONObject stepObject = questStepsObject.getJSONObject(stepKey);
                                    Integer stepWeight = Integer.valueOf(stepKey);
                                    String description = stepObject.getString("description");
                                    if (description != null && !description.isEmpty()) {
                                        Long journalEntryId = searchTableItem("GSpreadSheetsJournalEntry", description);
                                        if (journalEntryId != null) {
                                            GSpreadSheetsJournalEntry journalEntry = em.find(GSpreadSheetsJournalEntry.class, journalEntryId);
                                            QuestStep step = null;
                                            Criteria stepBySheetCrit = session.createCriteria(QuestStep.class);
                                            stepBySheetCrit.add(Restrictions.eq("quest", quest));
                                            stepBySheetCrit.add(Restrictions.eq("sheetsJournalEntry", journalEntry));
                                            List<QuestStep> stepBySheetlist = stepBySheetCrit.list();
                                            if (stepBySheetlist != null && !stepBySheetlist.isEmpty()) {
                                                step = stepBySheetlist.get(0);
                                                if (step.getTextEn() == null && !EsnDecoder.IsMostlyRu(description)) {
                                                    step.setTextEn(description);
                                                }
                                                if (step.getTextRu() == null && EsnDecoder.IsMostlyRu(description)) {
                                                    step.setTextRu(description);
                                                }
                                                if (step.getWeight() == null || (step.getWeight() < stepWeight)) {
                                                    step.setWeight(stepWeight);
                                                }
                                                em.merge(step);
                                            } else {
                                                step = new QuestStep();
                                                step.setQuest(quest);
                                                step.setSheetsJournalEntry(journalEntry);
                                                if (!EsnDecoder.IsMostlyRu(description)) {
                                                    step.setTextEn(description);
                                                } else {
                                                    step.setTextRu(description);
                                                }
                                                em.persist(step);
                                            }
                                            JSONObject goalsObject = null;
                                            try {
                                                goalsObject = stepObject.getJSONObject("goals");
                                            } catch (JSONException ex) {

                                            }
                                            if (goalsObject != null) {
                                                for (QuestDirection.DIRECTION_TYPE t : QuestDirection.DIRECTION_TYPE.values()) {
                                                    JSONObject typedGoalsObject = null;
                                                    try {
                                                        typedGoalsObject = goalsObject.getJSONObject(t.name());
                                                    } catch (JSONException ex) {

                                                    }
                                                    if (typedGoalsObject != null) {
                                                        Iterator typedGoalsIterator = typedGoalsObject.keys();
                                                        while (typedGoalsIterator.hasNext()) {
                                                            String typedGoalKey = (String) typedGoalsIterator.next();
                                                            String goalName = typedGoalsObject.getString(typedGoalKey);
                                                            Matcher goalWithCounterMatcher = goalWithCounterPattern.matcher(goalName);
                                                            if (goalWithCounterMatcher.find()) {
                                                                goalName = goalWithCounterMatcher.group(1);
                                                            }
                                                            Long directionId = searchTableItem("GSpreadSheetsQuestDirection", goalName);
                                                            if (directionId != null) {
                                                                GSpreadSheetsQuestDirection direction = em.find(GSpreadSheetsQuestDirection.class, directionId);
                                                                QuestDirection goal = null;
                                                                Criteria goalByStepCrit = session.createCriteria(QuestDirection.class);
                                                                goalByStepCrit.add(Restrictions.eq("step", step));
                                                                goalByStepCrit.add(Restrictions.eq("sheetsQuestDirection", direction));
                                                                goalByStepCrit.add(Restrictions.eq("directionType", t));
                                                                List<QuestDirection> goalByStepList = goalByStepCrit.list();
                                                                if (goalByStepList != null && !goalByStepList.isEmpty()) {
                                                                    goal = goalByStepList.get(0);
                                                                } else {
                                                                    Criteria goalByQuestCrit = session.createCriteria(QuestDirection.class);
                                                                    goalByQuestCrit.add(Restrictions.eq("sheetsQuestDirection", direction));
                                                                    goalByQuestCrit.add(Restrictions.eq("quest", quest));
                                                                    List<QuestDirection> goalByQuestList = goalByQuestCrit.list();
                                                                    if (goalByQuestList != null && !goalByQuestList.isEmpty()) {
                                                                        goal = goalByQuestList.get(0);
                                                                    }
                                                                }
                                                                if (goal != null) {
                                                                    if (goal.getDirectionType() == null) {
                                                                        goal.setDirectionType(t);
                                                                    }
                                                                    if (!EsnDecoder.IsMostlyRu(goalName)) {
                                                                        goal.setTextEn(goalName);
                                                                    } else {
                                                                        goal.setTextRu(goalName);
                                                                    }
                                                                    goal.setStep(step);
                                                                    em.merge(goal);
                                                                } else {
                                                                    goal = new QuestDirection();
                                                                    goal.setQuest(quest);
                                                                    goal.setStep(step);
                                                                    goal.setSheetsQuestDirection(direction);
                                                                    goal.setDirectionType(t);
                                                                    if (!EsnDecoder.IsMostlyRu(goalName)) {
                                                                        goal.setTextEn(goalName);
                                                                    } else {
                                                                        goal.setTextRu(goalName);
                                                                    }
                                                                    em.persist(goal);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }

                                        }

                                    }

                                }
                            }
                            JSONObject questInfoObject = null;
                            try {
                                questInfoObject = questObject.getJSONObject("info");
                            } catch (JSONException ex) {

                            }
                            if (questInfoObject != null) {
                                try {
                                    JSONObject questDescription = questInfoObject.getJSONObject("description");
                                    if (questDescription != null) {
                                        String descriptionString = questDescription.getString("1");

                                    }
                                } catch (JSONException ex) {

                                }
                                try {
                                    JSONObject questStarter = questInfoObject.getJSONObject("starter");
                                    if (questStarter != null) {
                                        String npcNameString = questStarter.getString("1");
                                        String npcName = null;
                                        String npcNameRu = null;
                                        Matcher npcWithCasesMatcher = nameCasesPattern.matcher(npcNameString);
                                        if (npcWithCasesMatcher.matches()) {
                                            String group1 = npcWithCasesMatcher.group(1);
                                            String group2 = npcWithCasesMatcher.group(2);
                                            if (!EsnDecoder.IsMostlyRu(group1)) {
                                                npcName = group1.trim();
                                            } else {
                                                npcNameRu = group2.trim();
                                            }
                                        } else {
                                            if (EsnDecoder.IsMostlyRu(npcNameString)) {
                                                npcNameRu = npcNameString;
                                            } else {
                                                npcName = npcNameString;
                                            }
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
                                                if (EsnDecoder.IsMostlyRu(sheetNpc.getTextRu())) {
                                                    npcNameRu = sheetNpc.getTextRu();
                                                }
                                            }
                                        }
                                        Criteria npcCriteria = session.createCriteria(Npc.class);
                                        npcCriteria.add(Restrictions.eq("location", location));
                                        if (npcName != null) {
                                            npcCriteria.add(Restrictions.ilike("name", npcName));
                                        } else if (npcNameRu != null) {
                                            npcCriteria.add(Restrictions.ilike("nameRu", npcNameRu));
                                        }
                                        Npc currentNpc = null;
                                        List<Npc> npcList = npcCriteria.list();
                                        if (npcList != null && !npcList.isEmpty()) {
                                            currentNpc = npcList.get(0);
                                        } else {
                                            Criteria npcParentLocCriteria = session.createCriteria(Npc.class);
                                            npcParentLocCriteria.createAlias("location", "location");
                                            npcParentLocCriteria.add(Restrictions.eq("location.parentLocation", location));
                                            if (npcName != null) {
                                                npcParentLocCriteria.add(Restrictions.ilike("name", npcName));
                                            } else if (npcNameRu != null) {
                                                npcParentLocCriteria.add(Restrictions.ilike("nameRu", npcNameRu));
                                            }
                                            npcList = npcParentLocCriteria.list();
                                            if (npcList != null && !npcList.isEmpty()) {
                                                currentNpc = npcList.get(0);
                                            }
                                        }
                                        if (currentNpc == null) {
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
                                        if (quest.getNpcs() == null) {
                                            quest.setNpcs(new HashSet<Npc>());
                                        }
                                        if (!quest.getNpcs().contains(currentNpc)) {
                                            quest.getNpcs().add(currentNpc);
                                            em.merge(quest);
                                        }
                                    }
                                } catch (JSONException ex) {

                                }
                                try {
                                    JSONObject questFinisher = questInfoObject.getJSONObject("finisher");
                                    if (questFinisher != null) {
                                        String npcNameString = questFinisher.getString("1");
                                        String npcName = null;
                                        String npcNameRu = null;
                                        Matcher npcWithCasesMatcher = nameCasesPattern.matcher(npcNameString);
                                        if (npcWithCasesMatcher.matches()) {
                                            String group1 = npcWithCasesMatcher.group(1);
                                            String group2 = npcWithCasesMatcher.group(2);
                                            if (!EsnDecoder.IsMostlyRu(group1)) {
                                                npcName = group1.trim();
                                            } else {
                                                npcNameRu = group2.trim();
                                            }
                                        } else {
                                            if (EsnDecoder.IsMostlyRu(npcNameString)) {
                                                npcNameRu = npcNameString;
                                            } else {
                                                npcName = npcNameString;
                                            }
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
                                                if (EsnDecoder.IsMostlyRu(sheetNpc.getTextRu())) {
                                                    npcNameRu = sheetNpc.getTextRu();
                                                }
                                            }
                                        }
                                        Criteria npcCriteria = session.createCriteria(Npc.class);
                                        npcCriteria.add(Restrictions.eq("location", location));
                                        if (npcName != null) {
                                            npcCriteria.add(Restrictions.ilike("name", npcName));
                                        } else if (npcNameRu != null) {
                                            npcCriteria.add(Restrictions.ilike("nameRu", npcNameRu));
                                        }
                                        Npc currentNpc = null;
                                        List<Npc> npcList = npcCriteria.list();
                                        if (npcList != null && !npcList.isEmpty()) {
                                            currentNpc = npcList.get(0);
                                        } else {
                                            Criteria npcParentLocCriteria = session.createCriteria(Npc.class);
                                            npcParentLocCriteria.createAlias("location", "location");
                                            npcParentLocCriteria.add(Restrictions.eq("location.parentLocation", location));
                                            if (npcName != null) {
                                                npcParentLocCriteria.add(Restrictions.ilike("name", npcName));
                                            } else if (npcNameRu != null) {
                                                npcParentLocCriteria.add(Restrictions.ilike("nameRu", npcNameRu));
                                            }
                                            npcList = npcParentLocCriteria.list();
                                            if (npcList != null && !npcList.isEmpty()) {
                                                currentNpc = npcList.get(0);
                                            }
                                        }
                                        if (currentNpc == null) {
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
                                        if (quest.getNpcs() == null) {
                                            quest.setNpcs(new HashSet<Npc>());
                                        }
                                        if (!quest.getNpcs().contains(currentNpc)) {
                                            quest.getNpcs().add(currentNpc);
                                            em.merge(quest);
                                        }
                                    }
                                } catch (JSONException ex) {

                                }
                            }
                        }

                    }

                }
            }
        } catch (JSONException ex) {

        }
    }

    @Transactional
    public void transferGreetingsToTopics() {
        int counter = 0;
        Session session = (Session) em.getDelegate();
        Criteria greetingCrit = session.createCriteria(Greeting.class);
        greetingCrit.setMaxResults(1000);
        List<Greeting> greetings = greetingCrit.list();
        for (Greeting g : greetings) {
            counter++;
            LOG.log(Level.INFO, "greeting {0}", Integer.toString(counter));
            Topic t = null;
            Criteria topicCrit = session.createCriteria(Topic.class);
            topicCrit.add(Restrictions.eq("npc", g.getNpc()));
            if (g.getText() != null) {
                topicCrit.add(Restrictions.eq("npcText", g.getText()));
            } else if (g.getTextRu() != null) {
                topicCrit.add(Restrictions.eq("npcTextRu", g.getTextRu()));
            }
            List<Topic> topics = topicCrit.list();
            if (topics != null && !topics.isEmpty()) {
                t = topics.get(0);
                if (t.getExtNpcPhrase() == null) {
                    t.setExtNpcPhrase(g.getExtNpcPhrase());
                }
                em.merge(t);
            } else {
                t = new Topic(null, g.getText(), null, g.getTextRu(), g.getNpc());
                t.setExtNpcPhrase(g.getExtNpcPhrase());
                em.persist(t);
            }
            if (t != null) {
                for (TranslatedText tt : g.getTranslations()) {
                    tt.setNpcTopic(t);
                    tt.setGreeting(null);
                    em.merge(tt);
                }
                em.remove(g);
            }
        }
    }

    @Transactional
    public void importBooksWithSublocations(JSONObject source) {
        Pattern nameCasesPattern = Pattern.compile("(.*)\\((.*)\\)");
        Session session = (Session) em.getDelegate();
        JSONObject npcLocationObject = null;
        try {
            npcLocationObject = source.getJSONObject("books");
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
                        if (EsnDecoder.IsMostlyRu(sheetsLocationName.getTextRu())) {
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
                        em.flush();
                    } else {
                        LOG.log(Level.INFO, "update location: {0}", location.toString());
                        em.merge(location);
                    }
                    JSONObject subLocationObject = npcLocationObject.getJSONObject(locationName);
                    Iterator subLocationKeys = subLocationObject.keys();
                    while (subLocationKeys.hasNext()) {
                        String subLocationKey = (String) subLocationKeys.next();
                        String subLocationName = null;
                        Location subLocation = null;
                        if (subLocationKey.equals(locationName)) {
                            subLocation = location;
                        } else {
                            Matcher subLocationWithCasesMatcher = nameCasesPattern.matcher(subLocationKey);
                            if (subLocationWithCasesMatcher.matches()) {
                                String group1 = subLocationWithCasesMatcher.group(1);
                                String group2 = subLocationWithCasesMatcher.group(2);
                                if (!EsnDecoder.IsMostlyRu(group1)) {
                                    subLocationName = group1.trim();
                                } else {
                                    subLocationName = group2.trim();
                                }
                            } else {
                                subLocationName = subLocationKey;
                            }

                            Criteria sheetSubLocationCrit = session.createCriteria(GSpreadSheetsLocationName.class);
                            sheetSubLocationCrit.add(Restrictions.or(Restrictions.ilike("textEn", subLocationName), Restrictions.ilike("textRu", subLocationName)));
                            List<GSpreadSheetsLocationName> sulLocationList = sheetSubLocationCrit.list();
                            if (sulLocationList != null && !sulLocationList.isEmpty()) {
                                GSpreadSheetsLocationName sheetsSubLocationName = sulLocationList.get(0);
                                Criteria subLocationCrit = session.createCriteria(Location.class);
                                subLocationCrit.add(Restrictions.or(Restrictions.ilike("name", sheetsSubLocationName.getTextEn()), Restrictions.ilike("nameRu", sheetsSubLocationName.getTextRu())));
                                List<Location> subLocations = subLocationCrit.list();

                                if (subLocations != null && !subLocations.isEmpty()) {
                                    subLocation = subLocations.get(0);
                                    if (EsnDecoder.IsMostlyRu(sheetsSubLocationName.getTextRu())) {
                                        subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                    }
                                } else {
                                    subLocation = new Location();
                                    subLocation.setProgress(BigDecimal.ZERO);

                                }
                                subLocation.setParentLocation(location);
                                if (sheetsSubLocationName != null) {
                                    subLocation.setSheetsLocationName(sheetsSubLocationName);
                                    subLocation.setName(sheetsSubLocationName.getTextEn());
                                    if (!sheetsSubLocationName.getTextEn().equals(sheetsSubLocationName.getTextRu())) {
                                        subLocation.setNameRu(sheetsSubLocationName.getTextRu());
                                    }
                                }
                                if (subLocation.getId() == null) {
                                    LOG.log(Level.INFO, "new sublocation: " + subLocation.toString() + "/" + location.toString());
                                    em.persist(subLocation);
                                    em.flush();
                                } else {
                                    LOG.log(Level.INFO, "update sublocation: " + subLocation.toString() + "/" + location.toString());
                                    em.merge(subLocation);
                                }
                            }
                        }

                        JSONObject locationBooksObject = subLocationObject.getJSONObject(subLocationKey);
                        Iterator locationBooksObjectIterator = locationBooksObject.keys();
                        while (locationBooksObjectIterator.hasNext()) {
                            String bookKeyString = (String) locationBooksObjectIterator.next();
                            Long bookKey = Long.valueOf(bookKeyString);
                            Book book = null;
                            Criteria bookCriteria = session.createCriteria(Book.class);
                            bookCriteria.add(Restrictions.eq("cId", bookKey));
                            List<Book> bookList = bookCriteria.list();
                            if (bookList != null && !bookList.isEmpty()) {
                                book = bookList.get(0);
                            } else {
                                book = new Book();
                                book.setaId(21337012L);
                                book.setbId(0L);
                                book.setcId(bookKey);
                                BookText bookText = new BookText();
                                bookText.setaId(21337012L);
                                bookText.setbId(0L);
                                bookText.setcId(bookKey);

                                Criteria rawBookCrit = session.createCriteria(EsoRawString.class);
                                rawBookCrit.add(Restrictions.eq("aId", book.getaId()));
                                rawBookCrit.add(Restrictions.eq("bId", book.getbId()));
                                rawBookCrit.add(Restrictions.eq("cId", book.getcId()));
                                List<EsoRawString> rawBookList = rawBookCrit.list();
                                if (rawBookList != null && !rawBookList.isEmpty()) {
                                    EsoRawString rawBook = rawBookList.get(0);
                                    bookText.setTextEn(rawBook.getTextEn());
                                    if (rawBook.getTextRu() != null && !rawBook.getTextEn().equals(rawBook.getTextRu())) {
                                        bookText.setTextRu(rawBook.getTextRu());
                                    }
                                }
                                em.persist(bookText);
                                book.setBookText(bookText);
                                Criteria rawBookNameCrit = session.createCriteria(EsoRawString.class);
                                rawBookNameCrit.add(Restrictions.eq("aId", 51188213L));
                                rawBookNameCrit.add(Restrictions.eq("bId", book.getbId()));
                                rawBookNameCrit.add(Restrictions.eq("cId", book.getcId()));
                                List<EsoRawString> rawBookNameList = rawBookNameCrit.list();
                                if (rawBookNameList != null && !rawBookNameList.isEmpty()) {
                                    EsoRawString rawBook = rawBookNameList.get(0);
                                    book.setNameEn(rawBook.getTextEn());
                                    if (rawBook.getTextRu() != null && !rawBook.getTextEn().equals(rawBook.getTextRu())) {
                                        book.setNameRu(rawBook.getTextRu());
                                    }
                                } else {
                                    book.setNameEn("-no name-");
                                }
                                em.persist(book);
                                bookText.setBook(book);
                            }
                            if (book.getLocations() == null) {
                                book.setLocations(new HashSet<>());
                            }
                            if (!book.getLocations().contains(subLocation) && subLocation != null && subLocation.getId() != null) {
                                book.getLocations().add(subLocation);
                            }

                            em.merge(book);

                        }
                    }

                }
            }
        } catch (JSONException ex) {

        }
    }

    @Transactional
    public Quest getQuest(Quest q) {
        return em.find(Quest.class, q.getId());
    }

    @Transactional
    public Book getBook(Long bookId) {
        return em.find(Book.class, bookId);
    }

    @Transactional
    public List<Book> getBooksFromDate(Date fromDate) {
        List<Book> result = null;
        Session s = (Session) em.getDelegate();
        Criteria c = s.createCriteria(Book.class);
        c.add(Restrictions.ge("changeTime", fromDate));
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        result = c.list();
        return result;
    }

    @Transactional
    public void loadBooks() {
        Session session = (Session) em.getDelegate();
        Criteria rawBookCrit = session.createCriteria(EsoRawString.class);
        rawBookCrit.add(Restrictions.eq("aId", 21337012L));
        rawBookCrit.add(Restrictions.eq("bId", 0L));
        rawBookCrit.list();
        List<EsoRawString> rawBookList = rawBookCrit.list();
        for (EsoRawString r : rawBookList) {
            Criteria bookCriteria = session.createCriteria(Book.class);
            bookCriteria.add(Restrictions.eq("cId", r.getcId()));
            List<Book> bookList = bookCriteria.list();
            if (bookList != null && !bookList.isEmpty()) {
                Book book = bookList.get(0);
                if (r.getTextRu() != null && !r.getTextRu().isEmpty() && !r.getTextRu().equals(r.getTextEn())) {
                    BookText bookText = book.getBookText();
                    bookText.setTextEn(r.getTextEn());
                    if (r.getTextRu() != null && !r.getTextEn().equals(r.getTextRu())) {
                        bookText.setTextRu(r.getTextRu());
                    }
                    em.merge(bookText);
                }
                Criteria rawBookNameCrit = session.createCriteria(EsoRawString.class);
                rawBookNameCrit.add(Restrictions.eq("aId", 51188213L));
                rawBookNameCrit.add(Restrictions.eq("bId", book.getbId()));
                rawBookNameCrit.add(Restrictions.eq("cId", book.getcId()));
                List<EsoRawString> rawBookNameList = rawBookNameCrit.list();
                if (rawBookNameList != null && !rawBookNameList.isEmpty()) {
                    EsoRawString rawBook = rawBookNameList.get(0);
                    book.setNameEn(rawBook.getTextEn());
                    if (rawBook.getTextRu() != null && !rawBook.getTextEn().equals(rawBook.getTextRu())) {
                        book.setNameRu(rawBook.getTextRu());
                    }
                } else {
                    book.setNameEn("-no name-");
                }
                em.merge(book);
            } else {
                Book book = new Book();
                book.setaId(21337012L);
                book.setbId(0L);
                book.setcId(r.getcId());

                BookText bookText = new BookText();
                bookText.setaId(21337012L);
                bookText.setbId(0L);
                bookText.setcId(r.getcId());
                bookText.setTextEn(r.getTextEn());
                if (r.getTextRu() != null && !r.getTextEn().equals(r.getTextRu())) {
                    bookText.setTextRu(r.getTextRu());
                }
                em.persist(bookText);
                book.setBookText(bookText);
                Criteria rawBookNameCrit = session.createCriteria(EsoRawString.class);
                rawBookNameCrit.add(Restrictions.eq("aId", 51188213L));
                rawBookNameCrit.add(Restrictions.eq("bId", book.getbId()));
                rawBookNameCrit.add(Restrictions.eq("cId", book.getcId()));
                List<EsoRawString> rawBookNameList = rawBookNameCrit.list();
                if (rawBookNameList != null && !rawBookNameList.isEmpty()) {
                    EsoRawString rawBook = rawBookNameList.get(0);
                    book.setNameEn(rawBook.getTextEn());
                    if (rawBook.getTextRu() != null && !rawBook.getTextEn().equals(rawBook.getTextRu())) {
                        book.setNameRu(rawBook.getTextRu());
                    }
                } else {
                    book.setNameEn("-no name-");
                }
                em.persist(book);
            }
        }
    }

    @Transactional
    private Long searchTableItem(String tableName, String searchString) {
        Long result = null;
        searchString = searchString.replace("\n", "$");
        Query q = em.createNativeQuery("select id from " + tableName + " where texten ilike :searchString");
        q.setParameter("searchString", searchString);
        List<BigInteger> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            result = resultList.get(0).longValue();
        } else {
            Query q1 = em.createNativeQuery("select id from " + tableName + " where texten like '%<<%' and :searchString ilike regexp_replace(regexp_replace(texten, '<<((player|npc|\\d){[a-z,A-Z\\s]*\\/[a-z,A-Z\\s]*})*\\w*:*\\d*>>', '%','g'),'[\\[\\]]','','g') and char_length(regexp_replace(regexp_replace(textru, '<<((player|npc|\\d){[a-z,A-Z,а-я,А-ЯёЁ\\s]*\\/[a-z,A-Z,а-я,А-ЯёЁ\\s]*})*\\w*:*\\d*>>', '%','g'),'[\\[\\]]','','g'))>10 order by char_length(regexp_replace(texten, '<<\\w*:*\\d*>>', '%','g')) desc");
            q1.setParameter("searchString", searchString);
            List<BigInteger> resultList1 = q1.getResultList();
            if (resultList1 != null && !resultList1.isEmpty()) {
                result = resultList1.get(0).longValue();
            }
        }
        return result;
    }

    @Transactional
    private Long searchTableItemRu(String tableName, String searchString) {
        Long result = null;
        searchString = searchString.replace("\n\r", "\n").replace("\r\n", "\n").replace("\n", "$").replace("\r", "").replace("—", "%").replace("е", "_").replace("«", "_").replace("»", "_").replace("-", "%");
        Logger.getLogger(DBService.class.getName()).log(Level.INFO, searchString);
        Query q = em.createNativeQuery("select id from " + tableName + " where textru like :searchString");
        q.setParameter("searchString", searchString);
        List<BigInteger> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            result = resultList.get(0).longValue();
        } else {
            Query q1 = em.createNativeQuery("select id from " + tableName + " where textru ilike :searchString");
            q1.setParameter("searchString", searchString);
            List<BigInteger> resultList1 = q1.getResultList();
            if (resultList1 != null && !resultList1.isEmpty()) {
                result = resultList1.get(0).longValue();
            } else {
                Query q2 = em.createNativeQuery("select id from " + tableName + " where textru like '%<<%' and (:searchString ilike regexp_replace(regexp_replace(regexp_replace(textru, '<<((player|npc|\\d){([a-z,A-Z,а-я,А-ЯёЁ\\s]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s]*)})>>', '\\3','g'),'<<\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g') or :searchString ilike regexp_replace(regexp_replace(regexp_replace(textru, '<<((player|npc|\\d){([a-z,A-Z,а-я,А-ЯёЁ\\s]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s]*)})>>', '\\4','g'),'<<\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g')) and char_length(regexp_replace(regexp_replace(regexp_replace(textru, '<<((player|npc|\\d){([a-z,A-Z,а-я,А-ЯёЁ\\s]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s]*)})>>', '\\3','g'),'<<\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'))>6 order by char_length(regexp_replace(regexp_replace(regexp_replace(textru, '<<((player|npc|\\d){([a-z,A-Z,а-я,А-ЯёЁ\\s]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s]*)})>>', '\\3','g'),'<<\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g')) desc");
                q2.setParameter("searchString", searchString);
                List<BigInteger> resultList2 = q2.getResultList();
                if (resultList2 != null && !resultList2.isEmpty()) {
                    result = resultList2.get(0).longValue();
                }
            }
        }
        return result;
    }

    @Transactional
    private Long searchTableItemRuIndexed(String tableName, String searchString) {
        Long result = null;
        searchString = searchString.replace("\n\r", "\n").replace("\r\n", "\n").replace("\n", "$").replace("\r", "").replace("»", "\"").replace("«", "\"").replace("—", "-");
        //Logger.getLogger(DBService.class.getName()).log(Level.INFO, searchString);
        Query q = em.createNativeQuery("select id from " + tableName + " where textru like :searchString");
        q.setParameter("searchString", searchString);
        List<BigInteger> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            result = resultList.get(0).longValue();
        } else {
            Query q1 = em.createNativeQuery("select id from " + tableName + " where textru ilike :searchString");
            q1.setParameter("searchString", searchString);
            List<BigInteger> resultList1 = q1.getResultList();
            if (resultList1 != null && !resultList1.isEmpty()) {
                result = resultList1.get(0).longValue();
            } else {
                Query q2 = em.createNativeQuery("select id from " + tableName + " where (:searchString ilike trrumm or :searchString ilike trruff or :searchString ilike trrufm or :searchString ilike trrumf) and char_length(trrumm)>6 order by char_length(trrumm) desc");
                q2.setParameter("searchString", searchString);
                List<BigInteger> resultList2 = q2.getResultList();
                if (resultList2 != null && !resultList2.isEmpty()) {
                    result = resultList2.get(0).longValue();
                }
            }
        }
        return result;
    }

    @Transactional
    private Long searchTableItemIndexed(String tableName, String searchString) {
        Long result = null;
        searchString = searchString.replace("\n\r", "\n").replace("\r\n", "\n").replace("\n", "$").replace("\r", "");
        // Logger.getLogger(DBService.class.getName()).log(Level.INFO, searchString);
        Query q = em.createNativeQuery("select id from " + tableName + " where texten like :searchString");
        q.setParameter("searchString", searchString);
        List<BigInteger> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            result = resultList.get(0).longValue();
        } else {
            Query q1 = em.createNativeQuery("select id from " + tableName + " where texten ilike :searchString");
            q1.setParameter("searchString", searchString);
            List<BigInteger> resultList1 = q1.getResultList();
            if (resultList1 != null && !resultList1.isEmpty()) {
                result = resultList1.get(0).longValue();
            } else {
                Query q2 = em.createNativeQuery("select id from " + tableName + " where :searchString ilike tren and char_length(tren)>6 order by char_length(tren) desc");
                q2.setParameter("searchString", searchString);
                List<BigInteger> resultList2 = q2.getResultList();
                if (resultList2 != null && !resultList2.isEmpty()) {
                    result = resultList2.get(0).longValue();
                }
            }
        }
        return result;
    }

    @Transactional
    private Long searchTableItemUncertainIndexed(String tableName, String searchString) {
        Long result = null;
        searchString = searchString.replace("\n\r", "\n").replace("\r\n", "\n").replace("\n", "$").replace("\r", "").replace("»", "\"").replace("«", "\"").replace("—", "-");
        //Logger.getLogger(DBService.class.getName()).log(Level.INFO, searchString);
        Query q = em.createNativeQuery("select id from " + tableName + " where texten like :searchString or textru like :searchString");
        q.setParameter("searchString", searchString);
        List<BigInteger> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            result = resultList.get(0).longValue();
        } else {
            Query q1 = em.createNativeQuery("select id from " + tableName + " where texten ilike :searchString or textru ilike :searchString");
            q1.setParameter("searchString", searchString);
            List<BigInteger> resultList1 = q1.getResultList();
            if (resultList1 != null && !resultList1.isEmpty()) {
                result = resultList1.get(0).longValue();
            } else {
                Query q2 = em.createNativeQuery("select id from " + tableName + " where (:searchString ilike tren or :searchString ilike trrumm or :searchString ilike trruff or :searchString ilike trrufm or :searchString ilike trrumf) and char_length(trrumm)>6 order by char_length(trrumm) desc");
                q2.setParameter("searchString", searchString);
                List<BigInteger> resultList2 = q2.getResultList();
                if (resultList2 != null && !resultList2.isEmpty()) {
                    result = resultList2.get(0).longValue();
                }
            }
        }
        return result;
    }

    @Transactional
    public void generateSearchIndex() {
        em.createNativeQuery("update gspreadsheetsplayerphrase set tren=regexp_replace(regexp_replace(texten, '<<((player|npc|\\d){[a-z,A-Z\\s]*\\/[a-z,A-Z\\s]*})*\\w*:*\\d*>>', '%','g'),'[\\[\\]]','','g') where texten like '%<<%' or texten like '%[%'").executeUpdate();
        em.createNativeQuery("update gspreadsheetsplayerphrase set trrumm=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\1','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\1','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();
        em.createNativeQuery("update gspreadsheetsplayerphrase set trruff=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\2','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\2','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();
        em.createNativeQuery("update gspreadsheetsplayerphrase set trrumf=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\1','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\2','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();
        em.createNativeQuery("update gspreadsheetsplayerphrase set trrufm=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\2','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\1','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();

        em.createNativeQuery("update gspreadsheetsnpcphrase set tren=regexp_replace(regexp_replace(texten, '<<((player|npc|\\d){[a-z,A-Z\\s]*\\/[a-z,A-Z\\s]*})*\\w*:*\\d*>>', '%','g'),'[\\[\\]]','','g') where texten like '%<<%' or texten like '%[%'").executeUpdate();
        em.createNativeQuery("update gspreadsheetsnpcphrase set trrumm=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\1','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\1','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();
        em.createNativeQuery("update gspreadsheetsnpcphrase set trruff=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\2','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\2','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();
        em.createNativeQuery("update gspreadsheetsnpcphrase set trrumf=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\1','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\2','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();
        em.createNativeQuery("update gspreadsheetsnpcphrase set trrufm=replace(replace(replace(replace(regexp_replace(regexp_replace(regexp_replace(regexp_replace(textru, '<<player{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>', '\\2','g'),'<<npc{([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)\\/([a-z,A-Z,а-я,А-ЯёЁ\\s«»\\-!\\.—\\?;]*)}>>','\\1','g'),'<<(\\d{[a-z,A-Z,а-я,А-ЯёЁ\\s\\/«»\\-!\\.—\\?;]*})*\\w*:*\\d*>>','%','g'),'[\\[\\]]','','g'),'ё','е'),' — ','%'),'«','\"'),'»','\"') where (textru like '%<<%' or textru like '%[%' or textru like '%ё%' or textru like '% — %' or textru like '%«%') and texten!=textru").executeUpdate();
    }

    @Transactional
    public void mergeSubtitles() {
        List<Subtitle> subtitlesToDelete = new ArrayList<>();
        Query dublicateSubtitleQuery = em.createNativeQuery("select s.id as sid,s1.id as s1id from subtitle s join subtitle s1 on s1.extnpcphrase_id=s.extnpcphrase_id and s1.id!=s.id and s1.npc_id=s.npc_id and s1.text_ru is not null where s.text_en is not null");
        List<Object[]> resultList = dublicateSubtitleQuery.getResultList();
        for (Object[] o : resultList) {
            BigInteger sId = (BigInteger) o[0];
            BigInteger s1Id = (BigInteger) o[1];
            Subtitle s = em.find(Subtitle.class, sId.longValue());
            Subtitle s1 = em.find(Subtitle.class, s1Id.longValue());
            if (s.getTextRu() == null) {
                s.setTextRu(s1.getTextRu());
                em.merge(s);
            }
            if (s1.getTranslations() != null) {
                for (TranslatedText t : s1.getTranslations()) {
                    t.setSubtitle(s);
                    em.merge(t);
                }
            }
            Logger.getLogger(DBService.class.getName()).log(Level.INFO, "merge\n{0} with \n{1}", new Object[]{s.getText(), s1.getTextRu()});
            subtitlesToDelete.add(s1);
        }

        for (Subtitle s : subtitlesToDelete) {
            if (s.getPreviousSubtitle() != null) {
                Subtitle s2 = s.getPreviousSubtitle();
                s2.setNextSubtitle(null);
                em.merge(s2);
            }
        }
        for (Subtitle s : subtitlesToDelete) {
            em.remove(s);
        }
    }

    @Transactional
    public void mergeTopics() {
        List<Topic> topicsToDelete = new ArrayList<>();
        List<Topic> undeletable = new ArrayList<>();
        Query dublicateTopicQuery = em.createNativeQuery("select t1.id as id1,t2.id as id2 from topic t1 join topic t2 on t1.npc_id=t2.npc_id and t1.extnpcphrase_id=t2.extnpcphrase_id and t1.extplayerphrase_id=t2.extplayerphrase_id and t1.id!=t2.id where t1.npctext is not null and t1.playertext is not null order by t1.id desc");
        List<Object[]> resultList = dublicateTopicQuery.getResultList();
        for (Object[] o : resultList) {
            BigInteger sId = (BigInteger) o[0];
            BigInteger s1Id = (BigInteger) o[1];
            Topic t1 = em.find(Topic.class, sId.longValue());
            Topic t2 = em.find(Topic.class, s1Id.longValue());
            if (!undeletable.contains(t2)) {
                Logger.getLogger(DBService.class.getName()).log(Level.INFO, "merge\n{0}\nwith\n{1}", new Object[]{t2.getPlayerTextRu(), t1.getPlayerText()});
                undeletable.add(t1);
                topicsToDelete.add(t2);
                if (t1.getNpcTextRu() == null && t2.getNpcTextRu() != null) {
                    t1.setNpcTextRu(t2.getNpcTextRu());
                }
                if (t1.getPlayerTextRu() == null && t2.getPlayerTextRu() != null) {
                    t1.setPlayerTextRu(t2.getPlayerTextRu());
                }
                for (TranslatedText tt : t2.getNpcTranslations()) {
                    tt.setNpcTopic(t1);
                    em.merge(tt);
                }
                t2.getNpcTranslations().clear();
                for (TranslatedText tt : t2.getPlayerTranslations()) {
                    tt.setPlayerTopic(t1);
                    em.merge(tt);
                }
                t2.getPlayerTranslations().clear();
                for (Topic pt : t2.getPreviousTopics()) {
                    if (pt.getNextTopics().contains(t2)) {
                        pt.getNextTopics().remove(t2);
                    }
                    if (!pt.getNextTopics().contains(t1)) {
                        pt.getNextTopics().add(t1);
                    }
                    em.merge(pt);
                }
                t2.getPreviousTopics().clear();
                for (Topic nt : t2.getNextTopics()) {
                    if (nt.getPreviousTopics().contains(t2)) {
                        nt.getPreviousTopics().remove(t2);
                    }
                    if (!nt.getPreviousTopics().contains(t1)) {
                        nt.getPreviousTopics().add(t1);
                    }
                    em.merge(nt);
                }
                t2.getNextTopics().clear();
                em.merge(t2);
                em.merge(t1);
                em.flush();
            }
        }
        for (Topic t : topicsToDelete) {
            em.remove(t);
        }
    }

}
