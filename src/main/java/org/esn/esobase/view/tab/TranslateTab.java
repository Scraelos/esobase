/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.specification.LocationSpecification;
import org.esn.esobase.data.specification.NpcSpecification;
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
    private ComboBox locationTable;
    private ComboBox subLocationTable;
    private ComboBox questTable;
    private ComboBox npcTable;
    private BeanItemContainer<Location> locationContainer;
    private BeanItemContainer<Location> subLocationContainer;
    private BeanItemContainer<Quest> questContainer;
    private BeanItemContainer<Npc> npcContainer;
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
    private BeanItemContainer<SysAccount> sysAccountContainer = new BeanItemContainer<>(SysAccount.class);
    private Button refreshButton;
    private Label countLabel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private final NpcSpecification npcSpecification = new NpcSpecification();
    private final LocationSpecification locationSpecification = new LocationSpecification();

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
        npcListlayout = new HorizontalLayout();
        npcListlayout.setSizeFull();
        npcTable = new ComboBox("NPC");
        npcTable.setPageLength(20);

        npcTable.setWidth(100f, Unit.PERCENTAGE);
        npcTable.addValueChangeListener(new NpcSelectListener());
        npcTable.setFilteringMode(FilteringMode.CONTAINS);
        locationContainer = new BeanItemContainer<>(Location.class);
        locationTable = new ComboBox("Локация");
        locationTable.setPageLength(15);

        locationTable.setWidth(100f, Unit.PERCENTAGE);
        locationTable.addValueChangeListener(filterChangeListener);
        locationTable.setContainerDataSource(locationContainer);
        locationTable.setFilteringMode(FilteringMode.CONTAINS);

        subLocationContainer = new BeanItemContainer<>(Location.class);
        subLocationTable = new ComboBox("Сублокация");
        subLocationTable.setPageLength(15);

        subLocationTable.setWidth(100f, Unit.PERCENTAGE);
        subLocationTable.addValueChangeListener(filterChangeListener);
        subLocationTable.setContainerDataSource(subLocationContainer);
        subLocationTable.setFilteringMode(FilteringMode.CONTAINS);
        questContainer = new BeanItemContainer<>(Quest.class);
        questTable = new ComboBox("Квест");
        questTable.setPageLength(15);

        questTable.setWidth(100f, Unit.PERCENTAGE);
        questTable.addValueChangeListener(filterChangeListener);
        questTable.setContainerDataSource(questContainer);
        questTable.setFilteringMode(FilteringMode.CONTAINS);

        npcContainer = new BeanItemContainer<>(Npc.class);
        npcTable.setContainerDataSource(npcContainer);
        npcContainer.addNestedContainerProperty("location.name");
        npcContainer.addNestedContainerProperty("location.nameRu");
        npcContainer.addNestedContainerProperty("location.parentLocation");

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
        noTranslations = new CheckBox("Не переведены полностью");
        noTranslations.setValue(Boolean.FALSE);
        noTranslations.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LoadFilters();
                LoadNpcContent();
            }
        });
        emptyTranslations = new CheckBox("Не добавлен перевод");
        emptyTranslations.setValue(Boolean.FALSE);
        emptyTranslations.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LoadFilters();
                LoadNpcContent();
            }
        });
        HorizontalLayout checkBoxlayout = new HorizontalLayout(noTranslations, emptyTranslations);
        translatorBox = new ComboBox("Переводчик");
        translatorBox.setPageLength(15);
        sysAccountContainer = service.loadBeanItems(sysAccountContainer);
        translatorBox.setContainerDataSource(sysAccountContainer);
        translatorBox.setFilteringMode(FilteringMode.CONTAINS);
        translatorBox.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
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
        searchField.setNullRepresentation("");
        searchField.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
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
        npcTabSheet = new TabSheet();
        npcTabSheet.setSizeFull();
        npcTabLayout = new VerticalLayout();
        locationName = new TextField("Название локации");
        locationName.setNullRepresentation("");
        npcTabLayout.addComponent(locationName);
        locationNameRu = new TextField("Перевод названия локации");
        locationNameRu.setNullRepresentation("");
        npcTabLayout.addComponent(locationNameRu);
        npcName = new TextField("Имя NPC");
        npcName.setNullRepresentation("");
        npcTabLayout.addComponent(npcName);
        npcNameRu = new TextField("Перевод имени NPC");
        npcNameRu.setNullRepresentation("");
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
    }

    private void LoadFilters() {
        npcContainer.removeAllItems();
        npcSpecification.setNoTranslations(noTranslations.getValue());
        npcSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        npcSpecification.setTranslator((SysAccount) translatorBox.getValue());
        npcSpecification.setQuest((Quest) questTable.getValue());
        npcSpecification.setLocation((Location) locationTable.getValue());
        npcSpecification.setSubLocation((Location) subLocationTable.getValue());
        npcSpecification.setEmptyTranslations(emptyTranslations.getValue());
        npcSpecification.setSearchString(searchField.getValue());
        if (subLocationTable.getValue() != null) {
            npcSpecification.setLocation((Location) subLocationTable.getValue());
        } else {
            npcSpecification.setLocation((Location) locationTable.getValue());
        }
        npcContainer.addAll(service.getNpcRepository().findAll(npcSpecification));
        locationSpecification.setNoTranslations(noTranslations.getValue());
        locationSpecification.setEmptyTranslations(emptyTranslations.getValue());
        locationSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        locationSpecification.setTranslator((SysAccount) translatorBox.getValue());
        locationSpecification.setSearchString(searchField.getValue());
        List<Location> allLocations = service.getLocationRepository().findAll(locationSpecification);
        List<Location> locations = new ArrayList<>();
        List<Location> subLocations = new ArrayList<>();
        for (Location l : allLocations) {
            if (l.getParentLocation() == null) {
                locations.add(l);
                subLocations.add(l);
            }
        }
        for (Location l : allLocations) {
            if (l.getParentLocation() != null) {
                locations.add(l.getParentLocation());
                subLocations.add(l);
            }
        }
        locationContainer.removeAllItems();
        locationContainer.addAll(locations);
        locationContainer.sort(new Object[]{"name"}, new boolean[]{true});
        subLocationContainer.removeAllItems();
        subLocationContainer.addAll(subLocations);
        subLocationContainer.sort(new Object[]{"name"}, new boolean[]{true});
        questContainer = service.loadBeanItems(questContainer);
        questContainer.sort(new Object[]{"name"}, new boolean[]{true});
        Long countTranslatedTextFilterResult = service.countTranslatedTextFilterResult((Location) locationTable.getValue(), (Location) subLocationTable.getValue(), (Quest) questTable.getValue(), (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
        countLabel.setCaption(countTranslatedTextFilterResult.toString());
    }

    private void LoadNpcContent() {
        if (currentNpc != null) {
            locationName.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "location.name"));
            locationNameRu.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "location.nameRu"));
            npcName.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "name"));
            npcNameRu.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "nameRu"));
            topicsContainer = service.getNpcTopics(currentNpc, topicsContainer, (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
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
                textEnArea.setReadOnly(true);
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
                textRuArea.setReadOnly(true);
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
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (topic.getExtNpcPhrase().getTextRu() != null && !topic.getExtNpcPhrase().getTextRu().equals(topic.getExtNpcPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + topic.getExtNpcPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(topic.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setReadOnly(true);
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

    private class TopicPlayerColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            Topic topic = (Topic) itemId;
            if (topic.getPlayerText() != null && !topic.getPlayerText().isEmpty()) {
                Label textEnAreaLabel = new Label("Текст в игре");
                textEnAreaLabel.addStyleName("v-caption-darkblue");
                result.addComponent(textEnAreaLabel);
                Label textEnArea = new Label();
                textEnArea.addStyleName("v-textarea");
                textEnArea.setValue(topic.getPlayerText());
                textEnArea.setReadOnly(true);
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
                textRuArea.setReadOnly(true);
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
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (topic.getExtPlayerPhrase().getTextRu() != null && !topic.getExtPlayerPhrase().getTextRu().equals(topic.getExtPlayerPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + topic.getExtPlayerPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(topic.getExtPlayerPhrase().getTextRu());
                    textRuRawArea.setReadOnly(true);
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
                textEnArea.setReadOnly(true);
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
                textRuArea.setReadOnly(true);
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
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (subtitle.getExtNpcPhrase().getTextRu() != null && !subtitle.getExtNpcPhrase().getTextRu().equals(subtitle.getExtNpcPhrase().getTextEn())) {
                    Label textRuRawAreaLabel = new Label("Перевод в таблицах от " + subtitle.getExtNpcPhrase().getTranslator());
                    textRuRawAreaLabel.addStyleName("v-caption-darkblue");
                    result.addComponent(textRuRawAreaLabel);
                    Label textRuRawArea = new Label();
                    textRuRawArea.addStyleName("v-textarea");
                    textRuRawArea.setValue(subtitle.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setReadOnly(true);
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

    private class NpcSelectListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            currentNpc = (Npc) npcTable.getValue();
            LoadNpcContent();
        }

    }

    private class FilterChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            subLocationContainer.removeAllContainerFilters();
            if (locationTable.getValue() != null) {
                subLocationContainer.addContainerFilter(new Or(
                        new Compare.Equal("parentLocation", locationTable.getValue()),
                        new Compare.Equal("id", ((Location) locationTable.getValue()).getId())
                )
                );
            }
            npcContainer.removeAllItems();
            npcSpecification.setNoTranslations(noTranslations.getValue());
            npcSpecification.setEmptyTranslations(emptyTranslations.getValue());
            npcSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
            npcSpecification.setTranslator((SysAccount) translatorBox.getValue());
            npcSpecification.setQuest((Quest) questTable.getValue());
            npcSpecification.setLocation((Location) locationTable.getValue());
            npcSpecification.setSubLocation((Location) subLocationTable.getValue());
            npcSpecification.setSearchString(searchField.getValue());
            npcContainer.addAll(service.getNpcRepository().findAll(npcSpecification));
            Long countTranslatedTextFilterResult = service.countTranslatedTextFilterResult((Location) locationTable.getValue(), (Location) subLocationTable.getValue(), (Quest) questTable.getValue(), (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
            countLabel.setCaption(countTranslatedTextFilterResult.toString());
        }

    }

    private class TranslationColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final VerticalLayout vl = new VerticalLayout();
            vl.setSizeFull();
            Set<TranslatedText> list = new HashSet<>();
            Set<TranslatedText> list1 = (Set<TranslatedText>) source.getItem(itemId).getItemProperty(columnId).getValue();
            list.addAll(list1);
            List<SysAccount> accounts = new ArrayList<>();

            String text = null;
            if (itemId instanceof Subtitle) {
                text = ((Subtitle) itemId).getText();
                Subtitle s = (Subtitle) itemId;
                if (s.getExtNpcPhrase() != null && s.getExtNpcPhrase().getTranslatedTexts() != null) {
                    list.addAll(s.getExtNpcPhrase().getTranslatedTexts());
                }
            } else if (itemId instanceof Topic) {
                if (columnId.equals("playerTranslations")) {
                    text = ((Topic) itemId).getPlayerText();
                    Topic t = (Topic) itemId;
                    if (t.getExtPlayerPhrase() != null && t.getExtPlayerPhrase().getTranslatedTexts() != null) {
                        list.addAll(t.getExtPlayerPhrase().getTranslatedTexts());
                    }
                } else if (columnId.equals("npcTranslations")) {
                    text = ((Topic) itemId).getNpcText();
                    Topic t = (Topic) itemId;
                    if (t.getExtNpcPhrase() != null && t.getExtNpcPhrase().getTranslatedTexts() != null) {
                        list.addAll(t.getExtNpcPhrase().getTranslatedTexts());
                    }
                }
            }

            if (list != null) {
                for (TranslatedText t : list) {
                    vl.addComponent(new TranslationCell(t));
                    accounts.add(t.getAuthor());
                }
            }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && text != null && !text.isEmpty() && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                final TranslatedText translatedText = new TranslatedText();
                translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                if (itemId instanceof Subtitle) {
                    Subtitle s = (Subtitle) itemId;
                    translatedText.setSubtitle((Subtitle) itemId);
                    if (s.getExtNpcPhrase() != null) {
                        translatedText.setSpreadSheetsNpcPhrase(s.getExtNpcPhrase());
                    }
                } else if (itemId instanceof Topic) {
                    if (columnId.equals("playerTranslations")) {
                        Topic t = (Topic) itemId;
                        translatedText.setPlayerTopic((Topic) itemId);
                        if (t.getExtPlayerPhrase() != null) {
                            translatedText.setSpreadSheetsPlayerPhrase(t.getExtPlayerPhrase());
                        }
                    } else if (columnId.equals("npcTranslations")) {
                        Topic t = (Topic) itemId;
                        translatedText.setNpcTopic((Topic) itemId);
                        if (t.getExtNpcPhrase() != null) {
                            translatedText.setSpreadSheetsNpcPhrase(t.getExtNpcPhrase());
                        }
                    }
                }
                Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
                addTranslation.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {

                        if (translatedText.getSubtitle() != null) {
                            translatedText.getSubtitle().getTranslations().add(translatedText);
                        }
                        if (translatedText.getPlayerTopic() != null) {
                            translatedText.getPlayerTopic().getPlayerTranslations().add(translatedText);
                        }
                        if (translatedText.getNpcTopic() != null) {
                            translatedText.getNpcTopic().getNpcTranslations().add(translatedText);
                        }

                        vl.addComponent(new TranslationCell(translatedText));
                        event.getButton().setVisible(false);
                    }
                });
                vl.addComponent(addTranslation);
            }
            return vl;
        }

    }

    private class TranslationCell extends VerticalLayout {

        private TextArea translation;
        private Button save;
        private Button accept;
        private Button preAccept;
        private Button correct;
        private Button reject;
        private final TranslatedText translatedText;

        public TranslationCell(TranslatedText translatedText_) {
            this.setSizeFull();
            this.translatedText = translatedText_;
            String translatedStatus = "нет";
            if (translatedText.getStatus() != null) {
                translatedStatus = translatedText.getStatus().toString();
            }
            StringBuilder caption = new StringBuilder();
            caption.append("Статус: ").append(translatedStatus).append(", автор: ").append(translatedText.getAuthor().getLogin());
            if (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED && (translatedText.getApprovedBy() != null) && (translatedText.getApptovedTime() != null)) {
                caption.append(", кто принял: ").append(translatedText.getApprovedBy().getLogin());
            }
            if (translatedText.getCreateTime() != null) {
                caption.append(", создано: ").append(sdf.format(translatedText.getCreateTime()));
            }
            if (translatedText.getChangeTime() != null) {
                caption.append(", изменено: ").append(sdf.format(translatedText.getChangeTime()));
            }
            translation = new TextArea(caption.toString());
            translation.setRows(7);
            translation.setSizeFull();
            translation.setNullRepresentation("");
            translation.setImmediate(true);
            translation.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.TIMEOUT);
            translation.setTextChangeTimeout(5000);
            translation.setValue(translatedText_.getText());

            translation.addTextChangeListener(new FieldEvents.TextChangeListener() {

                @Override
                public void textChange(FieldEvents.TextChangeEvent event) {
                    save.setVisible(true);

                    if (event.getText() == null || event.getText().isEmpty()) {
                        save.setCaption("Удалить");
                        save.setIcon(FontAwesome.RECYCLE);
                    } else {
                        translatedText.setText(event.getText());
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

            this.addComponent(save);
            if (translatedText.getStatus() != null && translatedText.getStatus() == TRANSLATE_STATUS.DIRTY) {
                save.setVisible(true);
            } else {
                save.setVisible(false);
            }
            if (SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.NEW)) {
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
                this.addComponent(preAccept);
            }

            if (SpringSecurityHelper.hasRole("ROLE_CORRECTOR") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED)) {
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
                this.addComponent(correct);
            }

            if (SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED) || (translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED))) {
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
                this.addComponent(accept);
            }
            if ((SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") || SpringSecurityHelper.hasRole("ROLE_APPROVE") || SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) && translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED) || (translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED))) {
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
                this.addComponent(reject);
            }

            if (SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.REJECTED || translatedText.getStatus() == TRANSLATE_STATUS.REVOKED)) {
                translation.setReadOnly(false);
            }
        }

    }

}
