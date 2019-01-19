/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.esn.esncomboextension.NoAutcompleteComboBoxExtension;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.specification.LocationSpecification;
import org.esn.esobase.data.specification.NpcSpecification;
import org.esn.esobase.data.specification.QuestSpecification;
import org.esn.esobase.data.specification.SubLocationSpecification;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.Topic;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.security.SpringSecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.addons.comboboxmultiselect.ComboBoxMultiselect;
import org.vaadin.viritin.layouts.MMarginInfo;
import org.vaadin.viritin.util.HtmlElementPropertySetter;

/**
 *
 * @author scraelos
 */
@Component
@Scope(value = "prototype")
public class TranslateTab extends VerticalLayout {

    @Autowired
    private DBService service;
    private HorizontalLayout npcListlayout;
    private VerticalLayout npcContentLayout;
    private ComboBox<Location> locationTable;
    private ComboBox<Location> subLocationTable;
    private ComboBox<Quest> questTable;
    private ComboBox<Npc> npcTable;
    private TabSheet npcTabSheet;
    private TabSheet.Tab npcTab;
    private VerticalLayout npcTabLayout;
    private TextField locationName;
    private TextField locationNameRu;
    private TextField npcName;
    private TextField npcNameRu;
    private TabSheet.Tab npcTopicsTab;
    private TabSheet.Tab npcSubtitlesTab;
    private Table npcTopicsTable;
    private Table npcSubtitlesTable;
    private BeanItemContainer<Topic> topicsContainer;
    private BeanItemContainer<Subtitle> subtitlesContainer;
    private Npc currentNpc;
    private ComboBoxMultiselect translateStatus;
    private CheckBox noTranslations;
    private CheckBox emptyTranslations;
    private TextField searchField;
    private ComboBox translatorBox;
    private Button refreshButton;
    private Label countLabel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private final NpcSpecification npcSpecification = new NpcSpecification();
    private final LocationSpecification locationSpecification = new LocationSpecification();
    private final SubLocationSpecification subLocationSpecification = new SubLocationSpecification();
    private final QuestSpecification questSpecification = new QuestSpecification();
    private List<Location> locations = new ArrayList<>();
    private List<Location> subLocations = new ArrayList<>();
    private List<Npc> npcList = new ArrayList<>();
    private List<Quest> questList = new ArrayList<>();
    private List<Topic> topicList = new ArrayList<>();
    private List<Subtitle> subtitleList = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(TranslateTab.class.getName());

    public TranslateTab() {

    }

    public void Init() {
        removeAllComponents();
        TopicNpcColumnGenerator topicNpcColumnGenerator = new TopicNpcColumnGenerator();
        TopicPlayerColumnGenerator topicPlayerColumnGenerator = new TopicPlayerColumnGenerator();
        SubtitleColumnGenerator subtitleColumnGenerator = new SubtitleColumnGenerator();
        TranslationColumnGenerator translationColumnGenerator = new TranslationColumnGenerator();
        FilterChangeListener filterChangeListener = new FilterChangeListener();
        this.setSizeFull();
        this.setSpacing(false);
        this.setMargin(false);
        npcListlayout = new HorizontalLayout();
        npcListlayout.setSpacing(false);
        npcListlayout.setMargin(false);
        npcListlayout.setSizeFull();
        npcTable = new ComboBox("NPC");
        npcTable.setPageLength(30);
        npcTable.setScrollToSelectedItem(true);
        npcTable.setWidth(100f, Unit.PERCENTAGE);
        npcTable.addValueChangeListener(new NpcSelectListener());
        npcTable.setScrollToSelectedItem(true);
        npcTable.setEmptySelectionAllowed(true);
        locationTable = new ComboBox("Локация");
        locationTable.setPageLength(30);
        locationTable.setScrollToSelectedItem(true);
        locationTable.setWidth(100f, Unit.PERCENTAGE);
        locationTable.addValueChangeListener(filterChangeListener);
        locationTable.setDataProvider(new ListDataProvider<>(locations));
        locationTable.setEmptySelectionAllowed(true);

        subLocationTable = new ComboBox("Сублокация");
        subLocationTable.setPageLength(30);
        subLocationTable.setScrollToSelectedItem(true);
        subLocationTable.setWidth(100f, Unit.PERCENTAGE);
        subLocationTable.addValueChangeListener(filterChangeListener);
        subLocationTable.setDataProvider(new ListDataProvider<>(subLocations));
        subLocationTable.setEmptySelectionAllowed(true);
        questTable = new ComboBox("Квест");
        questTable.setPageLength(30);
        questTable.setScrollToSelectedItem(true);

        questTable.setWidth(100f, Unit.PERCENTAGE);
        questTable.addValueChangeListener(filterChangeListener);
        questTable.setDataProvider(new ListDataProvider<>(questList));
        npcTable.setDataProvider(new ListDataProvider<>(npcList));

        FormLayout locationAndNpc = new FormLayout(questTable, locationTable, subLocationTable, npcTable);
        locationAndNpc.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        locationAndNpc.setSizeFull();

        npcListlayout.addComponent(locationAndNpc);

        translateStatus = new ComboBoxMultiselect("Статус перевода", Arrays.asList(TRANSLATE_STATUS.values()));
        translateStatus.setClearButtonCaption("Очистить");
        translateStatus.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LoadFilters();
                LoadNpcContent();
            }
        });
        translateStatus.setPageLength(20);
        noTranslations = new CheckBox("Не переведены полностью");
        noTranslations.setValue(Boolean.FALSE);
        noTranslations.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<Boolean> event) {
                LoadFilters();
                LoadNpcContent();
            }
        });

        emptyTranslations = new CheckBox("Не добавлен перевод");
        emptyTranslations.setValue(Boolean.FALSE);
        emptyTranslations.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<Boolean> event) {
                LoadFilters();
                LoadNpcContent();
            }
        });
        HorizontalLayout checkBoxlayout = new HorizontalLayout(noTranslations, emptyTranslations);
        translatorBox = new ComboBox("Переводчик");
        translatorBox.setPageLength(15);
        translatorBox.setScrollToSelectedItem(true);
        translatorBox.setDataProvider(new ListDataProvider(service.getSysAccounts()));
        translatorBox.addValueChangeListener(new HasValue.ValueChangeListener() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent event) {
                LoadFilters();
                LoadNpcContent();
            }
        });
        refreshButton = new Button("Обновить");
        refreshButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LoadFilters();
                LoadNpcContent();
            }
        });
        countLabel = new Label();
        searchField = new TextField("Искомая строка");
        searchField.setSizeFull();
        searchField.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<String> event) {
                LoadFilters();
                LoadNpcContent();
            }
        });

        FormLayout questAndWithNewTranslations = new FormLayout(translateStatus, translatorBox, checkBoxlayout, searchField);
        questAndWithNewTranslations.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        questAndWithNewTranslations.setSizeFull();
        npcListlayout.addComponent(questAndWithNewTranslations);
        npcListlayout.addComponent(refreshButton);
        npcListlayout.addComponent(countLabel);
        npcListlayout.setExpandRatio(locationAndNpc, 0.4f);
        npcListlayout.setExpandRatio(questAndWithNewTranslations, 0.4f);
        npcListlayout.setExpandRatio(refreshButton, 0.1f);
        npcListlayout.setExpandRatio(countLabel, 0.1f);
        npcContentLayout = new VerticalLayout();
        npcContentLayout.setSizeFull();
        npcContentLayout.setSpacing(false);
        npcContentLayout.setMargin(false);
        npcTabSheet = new TabSheet();
        npcTabSheet.setSizeFull();
        npcTabLayout = new VerticalLayout();
        locationName = new TextField("Название локации");
        npcTabLayout.addComponent(locationName);
        locationNameRu = new TextField("Перевод названия локации");
        npcTabLayout.addComponent(locationNameRu);
        npcName = new TextField("Имя NPC");
        npcTabLayout.addComponent(npcName);
        npcNameRu = new TextField("Перевод имени NPC");
        npcTabLayout.addComponent(npcNameRu);
        npcTab = npcTabSheet.addTab(npcTabLayout, "Инфо");
        npcTopicsTable = new Table();
        npcTopicsTable.addStyleName(ValoTheme.TABLE_COMPACT);
        npcTopicsTable.setSizeFull();
        npcTopicsTable.setPageLength(0);
        topicsContainer = new BeanItemContainer<>(Topic.class);
        npcTopicsTable.setContainerDataSource(topicsContainer);
        npcTopicsTable.addGeneratedColumn("npcTextG", topicNpcColumnGenerator);
        npcTopicsTable.addGeneratedColumn("playerTextG", topicPlayerColumnGenerator);
        npcTopicsTable.removeGeneratedColumn("playerTranslations");
        npcTopicsTable.addGeneratedColumn("playerTranslations", translationColumnGenerator);
        npcTopicsTable.removeGeneratedColumn("npcTranslations");
        npcTopicsTable.addGeneratedColumn("npcTranslations", translationColumnGenerator);
        npcTopicsTable.setVisibleColumns(new Object[]{"playerTextG", "playerTranslations", "npcTextG", "npcTranslations"});
        npcTopicsTable.setColumnHeaders(new String[]{"Реплика игрока", "Перевод", "Реплика NPC", "Перевод"});
        npcTopicsTable.setColumnExpandRatio("playerTextG", 1f);
        npcTopicsTable.setColumnExpandRatio("playerTranslations", 1.5f);
        npcTopicsTable.setColumnExpandRatio("npcTextG", 1.5f);
        npcTopicsTable.setColumnExpandRatio("npcTranslations", 1.5f);
        npcTopicsTable.setColumnWidth("actions", 150);
        npcSubtitlesTable = new Table();
        npcSubtitlesTable.addStyleName(ValoTheme.TABLE_COMPACT);
        npcSubtitlesTable.setSizeFull();
        npcSubtitlesTable.setPageLength(0);
        subtitlesContainer = new BeanItemContainer<>(Subtitle.class);
        npcSubtitlesTable.setContainerDataSource(subtitlesContainer);
        npcSubtitlesTable.addGeneratedColumn("textG", subtitleColumnGenerator);
        npcSubtitlesTable.removeGeneratedColumn("translations");
        npcSubtitlesTable.addGeneratedColumn("translations", translationColumnGenerator);
        npcSubtitlesTable.setVisibleColumns(new Object[]{"textG", "translations"});
        npcSubtitlesTable.setColumnHeaders(new String[]{"Реплика", "Перевод"});
        npcSubtitlesTable.setColumnExpandRatio("textG", 1f);
        npcSubtitlesTable.setColumnExpandRatio("translations", 1f);
        npcSubtitlesTable.setColumnWidth("actions", 150);

        npcTopicsTab = npcTabSheet.addTab(npcTopicsTable, "Диалоги");
        npcSubtitlesTab = npcTabSheet.addTab(npcSubtitlesTable, "Субтитры");
        npcContentLayout.addComponent(npcTabSheet);
        this.addComponent(npcListlayout);
        this.addComponent(npcContentLayout);
        this.npcListlayout.setHeight(105f, Unit.PIXELS);
        this.setExpandRatio(npcContentLayout, 1f);
        LoadFilters();
        new NoAutcompleteComboBoxExtension(questTable);
        new NoAutcompleteComboBoxExtension(locationTable);
        new NoAutcompleteComboBoxExtension(subLocationTable);
        new NoAutcompleteComboBoxExtension(npcTable);
        new NoAutcompleteComboBoxExtension(translatorBox);
    }

    private void LoadFilters() {
        npcSpecification.setNoTranslations(noTranslations.getValue());
        npcSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        npcSpecification.setTranslator((SysAccount) translatorBox.getValue());
        npcSpecification.setQuest((Quest) questTable.getValue());
        npcSpecification.setLocation(locationTable.getValue());
        npcSpecification.setSubLocation(subLocationTable.getValue());
        npcSpecification.setEmptyTranslations(emptyTranslations.getValue());
        npcSpecification.setSearchString(searchField.getValue());
        if (subLocationTable.getValue() != null) {
            npcSpecification.setLocation(subLocationTable.getValue());
        } else {
            npcSpecification.setLocation(locationTable.getValue());
        }
        if (locationTable.getValue() != null) {
            questSpecification.setLocation(locationTable.getValue());
        } else if (subLocationTable.getValue() != null) {
            if (subLocationTable.getValue().getParentLocation() != null) {
                questSpecification.setLocation(subLocationTable.getValue().getParentLocation());
            } else {
                questSpecification.setLocation(subLocationTable.getValue());
            }

        } else {
            questSpecification.setLocation(null);
        }
        npcList.clear();
        npcList.addAll(service.getNpcRepository().findAll(npcSpecification));
        questList.clear();
        questList.addAll(service.getQuestRepository().findAll(questSpecification));
        locationSpecification.setNoTranslations(noTranslations.getValue());
        locationSpecification.setEmptyTranslations(emptyTranslations.getValue());
        locationSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        locationSpecification.setTranslator((SysAccount) translatorBox.getValue());
        locationSpecification.setSearchString(searchField.getValue());
        locations.clear();
        for (Location l : service.getLocationRepository().findAll(locationSpecification)) {
            if (l.getParentLocation() == null) {
                if (!locations.contains(l)) {
                    locations.add(l);
                }
            } else {
                if (!locations.contains(l.getParentLocation())) {
                    locations.add(l.getParentLocation());
                }
            }
        }
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location t, Location t1) {
                String name = t.getName();
                String name1 = t1.getName();
                if (name == null) {
                    name = t.getNameRu();
                }
                if (name1 == null) {
                    name1 = t1.getNameRu();
                }
                if (name1 == null || name == null) {
                    return 0;
                }
                return name.compareTo(name1);
            }

        });
        subLocationSpecification.setNoTranslations(noTranslations.getValue());
        subLocationSpecification.setEmptyTranslations(emptyTranslations.getValue());
        subLocationSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        subLocationSpecification.setTranslator((SysAccount) translatorBox.getValue());
        subLocationSpecification.setSearchString(searchField.getValue());
        subLocationSpecification.setParentLocation(locationTable.getValue());
        subLocations.clear();
        subLocations.addAll(service.getLocationRepository().findAll(subLocationSpecification));
        locationTable.getDataProvider().refreshAll();
        subLocationTable.getDataProvider().refreshAll();
        npcTable.getDataProvider().refreshAll();
        questTable.getDataProvider().refreshAll();
        Long countTranslatedTextFilterResult = service.countTranslatedTextFilterResult((Location) locationTable.getValue(), (Location) subLocationTable.getValue(), (Quest) questTable.getValue(), (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
        countLabel.setCaption(countTranslatedTextFilterResult.toString());
    }

    private void LoadNpcContent() {
        if (currentNpc != null) {
            if (currentNpc.getLocation().getName() != null) {
                locationName.setValue(currentNpc.getLocation().getName());
            } else {
                locationName.clear();
            }
            if (currentNpc.getLocation().getNameRu() != null) {
                locationNameRu.setValue(currentNpc.getLocation().getNameRu());
            } else {
                locationNameRu.clear();
            }
            if (currentNpc.getName() != null) {
                npcName.setValue(currentNpc.getName());
            } else {
                npcName.clear();
            }
            if (currentNpc.getNameRu() != null) {
                npcNameRu.setValue(currentNpc.getNameRu());
            } else {
                npcNameRu.clear();
            }
            //topicList.clear();
            //topicList.addAll(service.getNpcTopics(currentNpc, (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue()));
            topicsContainer.removeAllItems();
            topicsContainer.addAll(service.getNpcTopics(currentNpc, (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue()));
            npcTabSheet.getTab(npcTopicsTable).setCaption("Диалоги(" + topicsContainer.size() + ")");
            subtitlesContainer = service.getNpcSubtitles(currentNpc, subtitlesContainer, (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
            npcTabSheet.getTab(npcSubtitlesTable).setCaption("Субтитры(" + subtitlesContainer.size() + ")");
        }
    }

    private class AssignClickListener implements Button.ClickListener {

        private final Object itemId;

        public AssignClickListener(Object itemId) {
            this.itemId = itemId;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            if (itemId instanceof Topic) {
                service.assignTopicToPhrase((Topic) itemId);
            } else if (itemId instanceof Subtitle) {
                service.assignSubtitleToPhrase((Subtitle) itemId);
            }
            LoadNpcContent();
        }

    }

    private class NpcTopicValueProvider implements ValueProvider<Topic, VerticalLayout> {

        @Override
        public VerticalLayout apply(Topic source) {
            VerticalLayout result = new VerticalLayout();
            result.addStyleName("v-scrollable");
            result.setSpacing(false);
            result.setMargin(new MMarginInfo(false, false, false, true));
            if (source.getNpcText() != null && !source.getNpcText().isEmpty()) {
                Label textEnAreaLabel = new Label("Текст в игре");
                textEnAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnAreaLabel);
                Label textEnArea = new Label();
                textEnArea.addStyleName("v-textarea");
                textEnArea.setValue(source.getNpcText());
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnArea);//, "Текст в игре"
            }
            if (source.getNpcTextRu() != null && !source.getNpcTextRu().isEmpty()) {
                Label textRuAreaLabel = new Label("Перевод в игре");
                textRuAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textRuAreaLabel);
                Label textRuArea = new Label();
                textRuArea.addStyleName("v-textarea");
                textRuArea.setValue(source.getNpcTextRu());
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textRuArea);//, "Перевод в игре"
            }
            if (source.getExtNpcPhrase() != null) {
                Label textEnRawArealabel = new Label("Текст в таблицах");
                textEnRawArealabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnRawArealabel);
                Label textEnRawArea = new Label();
                textEnRawArea.addStyleName("v-textarea");
                textEnRawArea.setValue(source.getExtNpcPhrase().getTextEn());
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (source.getExtNpcPhrase().getTextRu() != null && !source.getExtNpcPhrase().getTextRu().equals(source.getExtNpcPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + source.getExtNpcPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(source.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    result.addComponent(textRuRawArea);//, "Перевод в таблицах"
                }
            } else if (source.getNpcText() != null && !source.getNpcText().isEmpty()) {
                Button getRawButton = new Button("Искать RAW");
                getRawButton.addClickListener(new AssignClickListener(source));
                result.addComponent(getRawButton);
            }
            return result;
        }

    }

    private class TopicNpcColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {

            VerticalLayout result = new VerticalLayout();
            result.setMargin(new MMarginInfo(false, false, false, true));
            Topic topic = (Topic) itemId;
            if (topic.getNpcText() != null && !topic.getNpcText().isEmpty()) {
                Label textEnAreaLabel = new Label("Текст в игре");
                textEnAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnAreaLabel);
                Label textEnArea = new Label();
                textEnArea.addStyleName("v-textarea");
                textEnArea.setValue(topic.getNpcText());
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnArea);//, "Текст в игре"
            }
            if (topic.getNpcTextRu() != null && !topic.getNpcTextRu().isEmpty()) {
                Label textRuAreaLabel = new Label("Перевод в игре");
                textRuAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textRuAreaLabel);
                Label textRuArea = new Label();
                textRuArea.addStyleName("v-textarea");
                textRuArea.setValue(topic.getNpcTextRu());
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textRuArea);//, "Перевод в игре"
            }
            if (topic.getExtNpcPhrase() != null) {
                Label textEnRawArealabel = new Label("Текст в таблицах");
                textEnRawArealabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnRawArealabel);
                Label textEnRawArea = new Label();
                textEnRawArea.addStyleName("v-textarea");
                textEnRawArea.setValue(topic.getExtNpcPhrase().getTextEn());
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (topic.getExtNpcPhrase().getTextRu() != null && !topic.getExtNpcPhrase().getTextRu().equals(topic.getExtNpcPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + topic.getExtNpcPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(topic.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    result.addComponent(textRuRawArea);//, "Перевод в таблицах"
                }
            } else if (topic.getNpcText() != null && !topic.getNpcText().isEmpty()) {
                Button getRawButton = new Button("Искать RAW");
                getRawButton.addClickListener(new AssignClickListener(itemId));
                result.addComponent(getRawButton);
            }
            return result;
        }
    }

    private class PlayerTopicValueProvider implements ValueProvider<Topic, VerticalLayout> {

        @Override
        public VerticalLayout apply(Topic source) {
            VerticalLayout result = new VerticalLayout();
            result.addStyleName("v-scrollable");
            result.setSpacing(false);
            if (source.getPlayerText() != null && !source.getPlayerText().isEmpty()) {
                Label textEnAreaLabel = new Label("Текст в игре");
                textEnAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnAreaLabel);
                Label textEnArea = new Label();
                textEnArea.addStyleName("v-textarea");
                textEnArea.setValue(source.getPlayerText());
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnArea);//, "Текст в игре"
            }
            if (source.getPlayerTextRu() != null && !source.getPlayerTextRu().isEmpty()) {
                Label textRuAreaLabel = new Label("Перевод в игре");
                textRuAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textRuAreaLabel);
                Label textRuArea = new Label();
                textRuArea.addStyleName("v-textarea");
                textRuArea.setValue(source.getPlayerTextRu());
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textRuArea);//, "Перевод в игре"
            }
            if (source.getExtPlayerPhrase() != null) {
                Label textEnRawArealabel = new Label("Текст в таблицах");
                textEnRawArealabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnRawArealabel);
                Label textEnRawArea = new Label();
                textEnRawArea.addStyleName("v-textarea");
                textEnRawArea.setValue(source.getExtPlayerPhrase().getTextEn());
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (source.getExtPlayerPhrase().getTextRu() != null && !source.getExtPlayerPhrase().getTextRu().equals(source.getExtPlayerPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + source.getExtPlayerPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(source.getExtPlayerPhrase().getTextRu());
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    result.addComponent(textRuRawArea);//, "Перевод в таблицах"
                }
            } else if (source.getPlayerText() != null && !source.getPlayerText().isEmpty()) {
                Button getRawButton = new Button("Искать RAW");
                getRawButton.addClickListener(new AssignClickListener(source));
                result.addComponent(getRawButton);
            }

            return result;
        }

    }

    private class TopicPlayerColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            result.setMargin(new MMarginInfo(false, false, false, true));
            Topic topic = (Topic) itemId;
            if (topic.getPlayerText() != null && !topic.getPlayerText().isEmpty()) {
                Label textEnAreaLabel = new Label("Текст в игре");
                textEnAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnAreaLabel);
                Label textEnArea = new Label();
                textEnArea.addStyleName("v-textarea");
                textEnArea.setValue(topic.getPlayerText());
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnArea);//, "Текст в игре"
            }
            if (topic.getPlayerTextRu() != null && !topic.getPlayerTextRu().isEmpty()) {
                Label textRuAreaLabel = new Label("Перевод в игре");
                textRuAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textRuAreaLabel);
                Label textRuArea = new Label();
                textRuArea.addStyleName("v-textarea");
                textRuArea.setValue(topic.getPlayerTextRu());
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textRuArea);//, "Перевод в игре"
            }
            if (topic.getExtPlayerPhrase() != null) {
                Label textEnRawArealabel = new Label("Текст в таблицах");
                textEnRawArealabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnRawArealabel);
                Label textEnRawArea = new Label();
                textEnRawArea.addStyleName("v-textarea");
                textEnRawArea.setValue(topic.getExtPlayerPhrase().getTextEn());
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (topic.getExtPlayerPhrase().getTextRu() != null && !topic.getExtPlayerPhrase().getTextRu().equals(topic.getExtPlayerPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + topic.getExtPlayerPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(topic.getExtPlayerPhrase().getTextRu());
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    result.addComponent(textRuRawArea);//, "Перевод в таблицах"
                }
            } else if (topic.getPlayerText() != null && !topic.getPlayerText().isEmpty()) {
                Button getRawButton = new Button("Искать RAW");
                getRawButton.addClickListener(new AssignClickListener(itemId));
                result.addComponent(getRawButton);
            }

            return result;
        }

    }

    private class SubtitleColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            result.setMargin(new MarginInfo(false, false, true, false));
            MarginInfo areaMarginInfo = new MarginInfo(true, false, false, false);
            Subtitle subtitle = (Subtitle) itemId;
            String labelText = subtitle.getNpc().toString();
            if (subtitle.getPreviousSubtitle() == null && subtitle.getNextSubtitle() != null) {
                labelText = labelText + " - Начало диалога";
            }
            if (subtitle.getNextSubtitle() == null && subtitle.getPreviousSubtitle() != null) {
                labelText = labelText + " - Конец диалога";
            }
            Label l = new Label(labelText);
            l.addStyleName(ValoTheme.LABEL_COLORED);
            l.addStyleName(ValoTheme.LABEL_H3);
            result.addComponent(l);
            if (subtitle.getText() != null && !subtitle.getText().isEmpty()) {
                Label textEnAreaLabel = new Label("Текст в игре");
                textEnAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnAreaLabel);
                Label textEnArea = new Label();
                textEnArea.addStyleName("v-textarea");
                textEnArea.setValue(subtitle.getText());
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnArea);//, "Текст в игре"
            }
            if (subtitle.getTextRu() != null && !subtitle.getTextRu().isEmpty()) {
                Label textRuAreaLabel = new Label("Перевод в игре");
                textRuAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textRuAreaLabel);
                Label textRuArea = new Label();
                textRuArea.addStyleName("v-textarea");
                textRuArea.setValue(subtitle.getTextRu());
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textRuArea);//, "Перевод в игре"
            }

            if (subtitle.getExtNpcPhrase() != null) {
                Label textEnRawArealabel = new Label("Текст в таблицах");
                textEnRawArealabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnRawArealabel);
                Label textEnRawArea = new Label();
                textEnRawArea.addStyleName("v-textarea");
                textEnRawArea.setValue(subtitle.getExtNpcPhrase().getTextEn());
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (subtitle.getExtNpcPhrase().getTextRu() != null && !subtitle.getExtNpcPhrase().getTextRu().equals(subtitle.getExtNpcPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + subtitle.getExtNpcPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(subtitle.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    result.addComponent(textRuRawArea);//, "Перевод в таблицах"
                }

            } else if (subtitle.getText() != null && !subtitle.getText().isEmpty()) {
                Button getRawButton = new Button("Искать RAW");
                getRawButton.addClickListener(new AssignClickListener(itemId));
                result.addComponent(getRawButton);
            }

            return result;
        }

    }

    private class NpcSelectListener implements Property.ValueChangeListener, HasValue.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            currentNpc = (Npc) npcTable.getValue();
            LoadNpcContent();
        }

        @Override
        public void valueChange(HasValue.ValueChangeEvent event) {
            currentNpc = (Npc) npcTable.getValue();
            LoadNpcContent();
        }

    }

    private class FilterChangeListener implements Property.ValueChangeListener, HasValue.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            subLocationSpecification.setNoTranslations(noTranslations.getValue());
            subLocationSpecification.setEmptyTranslations(emptyTranslations.getValue());
            subLocationSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
            subLocationSpecification.setTranslator((SysAccount) translatorBox.getValue());
            subLocationSpecification.setSearchString(searchField.getValue());
            subLocationSpecification.setParentLocation(locationTable.getValue());
            subLocations.clear();
            subLocations.addAll(service.getLocationRepository().findAll(subLocationSpecification));
            subLocationTable.getDataProvider().refreshAll();
            npcList.clear();
            npcSpecification.setNoTranslations(noTranslations.getValue());
            npcSpecification.setEmptyTranslations(emptyTranslations.getValue());
            npcSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
            npcSpecification.setTranslator((SysAccount) translatorBox.getValue());
            npcSpecification.setQuest(questTable.getValue());
            npcSpecification.setLocation(locationTable.getValue());
            npcSpecification.setSubLocation(subLocationTable.getValue());
            npcSpecification.setSearchString(searchField.getValue());
            npcList.addAll(service.getNpcRepository().findAll(npcSpecification));
            npcTable.getDataProvider().refreshAll();

            if (locationTable.getValue() != null) {
                questSpecification.setLocation(locationTable.getValue());
            } else if (subLocationTable.getValue() != null) {
                if (subLocationTable.getValue().getParentLocation() != null) {
                    questSpecification.setLocation(subLocationTable.getValue().getParentLocation());
                } else {
                    questSpecification.setLocation(subLocationTable.getValue());
                }

            } else {
                questSpecification.setLocation(null);
            }
            questList.clear();

            questList.addAll(service.getQuestRepository().findAll(questSpecification));
            questTable.getDataProvider().refreshAll();
            Long countTranslatedTextFilterResult = service.countTranslatedTextFilterResult((Location) locationTable.getValue(), (Location) subLocationTable.getValue(), (Quest) questTable.getValue(), (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
            countLabel.setCaption(countTranslatedTextFilterResult.toString());
        }

        @Override
        public void valueChange(HasValue.ValueChangeEvent event) {
            subLocationSpecification.setNoTranslations(noTranslations.getValue());
            subLocationSpecification.setEmptyTranslations(emptyTranslations.getValue());
            subLocationSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
            subLocationSpecification.setTranslator((SysAccount) translatorBox.getValue());
            subLocationSpecification.setSearchString(searchField.getValue());
            subLocationSpecification.setParentLocation(locationTable.getValue());
            subLocations.clear();
            subLocations.addAll(service.getLocationRepository().findAll(subLocationSpecification));
            subLocationTable.getDataProvider().refreshAll();
            npcList.clear();
            npcSpecification.setNoTranslations(noTranslations.getValue());
            npcSpecification.setEmptyTranslations(emptyTranslations.getValue());
            npcSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
            npcSpecification.setTranslator((SysAccount) translatorBox.getValue());
            npcSpecification.setQuest((Quest) questTable.getValue());
            npcSpecification.setLocation((Location) locationTable.getValue());
            npcSpecification.setSubLocation((Location) subLocationTable.getValue());
            npcSpecification.setSearchString(searchField.getValue());
            npcList.addAll(service.getNpcRepository().findAll(npcSpecification));
            npcTable.getDataProvider().refreshAll();
            if (locationTable.getValue() != null) {
                questSpecification.setLocation(locationTable.getValue());
            } else if (subLocationTable.getValue() != null) {
                if (subLocationTable.getValue().getParentLocation() != null) {
                    questSpecification.setLocation(subLocationTable.getValue().getParentLocation());
                } else {
                    questSpecification.setLocation(subLocationTable.getValue());
                }

            } else {
                questSpecification.setLocation(null);
            }
            questList.clear();

            questList.addAll(service.getQuestRepository().findAll(questSpecification));
            questTable.getDataProvider().refreshAll();
            Long countTranslatedTextFilterResult = service.countTranslatedTextFilterResult((Location) locationTable.getValue(), (Location) subLocationTable.getValue(), (Quest) questTable.getValue(), (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
            countLabel.setCaption(countTranslatedTextFilterResult.toString());
        }

    }

    private class TranslationColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final VerticalLayout vl = new VerticalLayout();
            vl.setSizeFull();
            vl.setSpacing(false);
            vl.setMargin(false);
            Set<TranslatedText> list = new HashSet<>();
            Set<TranslatedText> list1 = (Set<TranslatedText>) source.getItem(itemId).getItemProperty(columnId).getValue();
            list.addAll(list1);
            List<SysAccount> accounts = new ArrayList<>();

            String text = null;
            boolean isAssigned = false;
            boolean isSubtitle = false;
            if (itemId instanceof Subtitle) {
                isSubtitle = true;
                text = ((Subtitle) itemId).getText();
                if (text == null) {
                    text = ((Subtitle) itemId).getTextRu();
                }
                Subtitle s = (Subtitle) itemId;
                if (s.getExtNpcPhrase() != null) {
                    isAssigned = true;
                }
                if (s.getExtNpcPhrase() != null && s.getExtNpcPhrase().getTranslatedTexts() != null) {
                    list.addAll(s.getExtNpcPhrase().getTranslatedTexts());
                }
            } else if (itemId instanceof Topic) {
                if (columnId.equals("playerTranslations")) {
                    text = ((Topic) itemId).getPlayerText();
                    if (text == null) {
                        text = ((Topic) itemId).getPlayerTextRu();
                    }
                    Topic t = (Topic) itemId;
                    if (t.getExtPlayerPhrase() != null) {
                        isAssigned = true;
                    }
                    if (t.getExtPlayerPhrase() != null && t.getExtPlayerPhrase().getTranslatedTexts() != null) {
                        list.addAll(t.getExtPlayerPhrase().getTranslatedTexts());
                    }
                } else if (columnId.equals("npcTranslations")) {
                    text = ((Topic) itemId).getNpcText();
                    if (text == null) {
                        text = ((Topic) itemId).getNpcTextRu();
                    }
                    Topic t = (Topic) itemId;
                    if (t.getExtNpcPhrase() != null) {
                        isAssigned = true;
                    }
                    if (t.getExtNpcPhrase() != null && t.getExtNpcPhrase().getTranslatedTexts() != null) {
                        list.addAll(t.getExtNpcPhrase().getTranslatedTexts());
                    }
                }
            }

            if (list != null) {
                for (TranslatedText t : list) {
                    vl.addComponent(new TranslationCell(t, isSubtitle));
                    accounts.add(t.getAuthor());
                }
            }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && isAssigned && (SpringSecurityHelper.hasRole("ROLE_TRANSLATE") || SpringSecurityHelper.hasRole("ROLE_SANDBOX"))) {
                final TranslatedText translatedText = new TranslatedText();
                translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                if (itemId instanceof Subtitle) {
                    isSubtitle = true;
                    Subtitle s = (Subtitle) itemId;
                    if (s.getExtNpcPhrase() != null) {
                        translatedText.setSpreadSheetsNpcPhrase(s.getExtNpcPhrase());
                    }
                } else if (itemId instanceof Topic) {
                    if (columnId.equals("playerTranslations")) {
                        Topic t = (Topic) itemId;
                        if (t.getExtPlayerPhrase() != null) {
                            translatedText.setSpreadSheetsPlayerPhrase(t.getExtPlayerPhrase());
                        }
                    } else if (columnId.equals("npcTranslations")) {
                        Topic t = (Topic) itemId;
                        if (t.getExtNpcPhrase() != null) {
                            translatedText.setSpreadSheetsNpcPhrase(t.getExtNpcPhrase());
                        }
                    }
                }
                Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
                addTranslation.addClickListener(new AddTranslationClickListener(translatedText, vl, isSubtitle));
                vl.addComponent(addTranslation);
            }
            return vl;
        }

    }

    private class AddTranslationClickListener implements Button.ClickListener {

        private TranslatedText translatedText;
        private VerticalLayout vl;
        private boolean isSubtitle;

        public AddTranslationClickListener(TranslatedText translatedText, VerticalLayout vl, boolean isSubtitle) {
            this.translatedText = translatedText;
            this.vl = vl;
            this.isSubtitle = isSubtitle;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            if (translatedText.getSpreadSheetsNpcPhrase() != null) {
                translatedText.getSpreadSheetsNpcPhrase().getTranslatedTexts().add(translatedText);
            }
            if (translatedText.getSpreadSheetsPlayerPhrase() != null) {
                translatedText.getSpreadSheetsPlayerPhrase().getTranslatedTexts().add(translatedText);
            }
            vl.addComponent(new TranslationCell(translatedText, isSubtitle));
            event.getButton().setVisible(false);
        }
    }

    private class TranslationCell extends VerticalLayout {

        private TextArea translation;
        private Button npc;
        private Button player;
        private Button save;
        private Button accept;
        private Button preAccept;
        private Button correct;
        private Button reject;
        private HorizontalLayout translationActions;
        private final TranslatedText translatedText;
        private boolean isSubtitle;

        public TranslationCell(TranslatedText translatedText_, boolean isSubtitle) {
            this.setSizeFull();
            this.setSpacing(false);
            this.setMargin(false);
            this.translatedText = translatedText_;
            String translatedStatus = "нет";
            if (translatedText.getStatus() != null) {
                translatedStatus = translatedText.getStatus().toString();
            }
            StringBuilder caption = new StringBuilder();
            StringBuilder description = new StringBuilder();
            caption.append("Статус: ").append(translatedStatus).append(", автор: ").append(translatedText.getAuthor().getLogin());
            description.append("Статус: ").append(translatedStatus).append(", автор: ").append(translatedText.getAuthor().getLogin());
            if (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED && (translatedText.getApprovedBy() != null) && (translatedText.getApptovedTime() != null)) {
                description.append(", кто принял: ").append(translatedText.getApprovedBy().getLogin());
            }
            if (translatedText.getCreateTime() != null) {
                description.append(", создано: ").append(sdf.format(translatedText.getCreateTime()));
            }
            if (translatedText.getChangeTime() != null) {
                description.append(", изменено: ").append(sdf.format(translatedText.getChangeTime()));
            }
            translation = new TextArea();
            translation.setDescription(description.toString());
            translation.setCaption(caption.toString());
            translation.setRows(7);
            translation.setSizeFull();
            if (translatedText_.getText() != null) {
                translation.setValue(translatedText_.getText());
            }

            translation.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> event) {
                    save.setVisible(true);
                    if (!isSubtitle) {
                        npc.setVisible(true);
                    }
                    player.setVisible(true);

                    if (event.getValue() == null || event.getValue().isEmpty()) {
                        save.setCaption("Удалить");
                        save.setIcon(FontAwesome.RECYCLE);
                        npc.setVisible(false);
                        player.setVisible(false);
                    } else {
                        translatedText.setText(event.getValue());
                        service.saveTranslatedTextDirty(translatedText);
                        save.setCaption("Сохранить");
                        save.setIcon(FontAwesome.SAVE);
                    }
                    String status = "нет";
                    if (translatedText.getStatus() != null) {

                        status = translatedText.getStatus().toString();
                    }
                    StringBuilder caption = new StringBuilder();
                    caption.append("Статус: ").append(status).append(", автор: ").append(translatedText.getAuthor().getLogin());
                    if (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED && (translatedText.getApprovedBy() != null) && (translatedText.getApptovedTime() != null)) {
                        caption.append(", кто принял: ").append(translatedText.getApprovedBy().getLogin());
                    }
                    if (translatedText.getCreateTime() != null) {
                        caption.append(", создано: ").append(sdf.format(translatedText.getCreateTime()));
                    }
                    if (translatedText.getChangeTime() != null) {
                        caption.append(", изменено: ").append(sdf.format(translatedText.getChangeTime()));
                    }
                    translation.setCaption(caption.toString());
                }
            });
            if (SpringSecurityHelper.getSysAccount().equals(translatedText_.getAuthor())) {
                translation.setReadOnly(false);
            } else {
                translation.setReadOnly(true);
            }
            this.addComponent(translation);
            translationActions = new HorizontalLayout();
            this.addComponent(translationActions);
            save = new Button("Сохранить", FontAwesome.SAVE);
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translatedText.setText(translation.getValue());
                    service.saveTranslatedText(translatedText);
                    LoadNpcContent();
                    LoadFilters();
                }
            });
            translationActions.addComponent(save);
            save.setVisible(false);
            npc = new Button("N{}");
            npc.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
            npc.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translation.setValue(translation.getValue() + "<<npc{/}>>");
                }
            });
            translationActions.addComponent(npc);
            npc.setVisible(false);
            player = new Button("P{}");
            player.addStyleNames(ValoTheme.BUTTON_SMALL, ValoTheme.BUTTON_TINY);
            player.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translation.setValue(translation.getValue() + "<<player{/}>>");
                }
            });
            translationActions.addComponent(player);
            player.setVisible(false);

            if (translatedText.getStatus() == TRANSLATE_STATUS.DIRTY && (SpringSecurityHelper.hasRole("ROLE_APPROVE") || SpringSecurityHelper.hasRole("ROLE_CORRECTOR") || SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") || translatedText.getAuthor().equals(SpringSecurityHelper.getSysAccount()))) {
                save.setVisible(true);
                if (!isSubtitle) {
                    npc.setVisible(true);
                }
                player.setVisible(true);
            }
            if (translatedText.getStatus() == null) {
                if (!isSubtitle) {
                    npc.setVisible(true);
                }
                player.setVisible(true);
            }
            if (SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.EDITED)) {
                translation.setReadOnly(false);
                preAccept = new Button("Перевод верен", FontAwesome.CHECK);
                preAccept.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.preAcceptTranslatedText(translatedText);
                        LoadNpcContent();
                        LoadFilters();
                    }
                });
                translationActions.addComponent(preAccept);
            }

            if (SpringSecurityHelper.hasRole("ROLE_CORRECTOR") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED || translatedText.getStatus() == TRANSLATE_STATUS.EDITED)) {
                translation.setReadOnly(false);
                correct = new Button("Текст корректен", FontAwesome.PENCIL);
                correct.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.correctTranslatedText(translatedText);
                        LoadNpcContent();
                        LoadFilters();
                    }
                });
                translationActions.addComponent(correct);
            }

            if (SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.SANDBOX) || (translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED) || (translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED) || (translatedText.getStatus() == TRANSLATE_STATUS.EDITED))) {
                translation.setReadOnly(false);
                accept = new Button("Принять эту версию", FontAwesome.THUMBS_UP);
                accept.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.acceptTranslatedText(translatedText);
                        service.calculateQuestProgressByNpc(currentNpc);
                        LoadNpcContent();
                        LoadFilters();
                    }
                });
                translationActions.addComponent(accept);
            }
            if ((SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getStatus() == TRANSLATE_STATUS.SANDBOX) || (SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") || SpringSecurityHelper.hasRole("ROLE_APPROVE") || SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) && translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED) || (translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED) || (translatedText.getStatus() == TRANSLATE_STATUS.EDITED))) {
                reject = new Button("Отклонить эту версию", FontAwesome.THUMBS_DOWN);
                reject.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.rejectTranslatedText(translatedText);
                        LoadNpcContent();
                        LoadFilters();
                    }
                });
                translationActions.addComponent(reject);
            }

            if (SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.SANDBOX || translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.REJECTED || translatedText.getStatus() == TRANSLATE_STATUS.REVOKED)) {
                translation.setReadOnly(false);
            }
        }

    }

}
