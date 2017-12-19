/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.HasValue;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.specification.QuestDirectionSpecification;
import org.esn.esobase.data.specification.QuestLocationSpecification;
import org.esn.esobase.data.specification.QuestSpecification;
import org.esn.esobase.data.specification.QuestStepSpecification;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.QuestDirection;
import org.esn.esobase.model.QuestStep;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.security.SpringSecurityHelper;
import org.vaadin.addons.comboboxmultiselect.ComboBoxMultiselect;

/**
 *
 * @author scraelos
 */
public class QuestTranslateTab extends VerticalLayout {

    private final DBService service;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private HorizontalLayout questListlayout;
    private ComboBox questTable;
    private BeanItemContainer<Quest> questContainer;

    private TabSheet tabSheet;
    private VerticalLayout infoLayout;
    private HorizontalLayout nameHLayout;
    private VerticalLayout nameLayout;
    private VerticalLayout nameTranslateLayout;
    private HorizontalLayout descriptionHLayout;
    private VerticalLayout descriptionLayout;
    private VerticalLayout descriptionTranslateLayout;

    private ComboBox locationTable;
    private BeanItemContainer<Location> locationContainer;
    private ComboBoxMultiselect translateStatus;
    private CheckBox noTranslations;
    private CheckBox emptyTranslations;
    private TextField searchField;
    private ComboBox translatorBox;
    private BeanItemContainer<SysAccount> sysAccountContainer = new BeanItemContainer<>(SysAccount.class);
    private Button refreshButton;
    private Label countLabel;

    private TextArea questNameEnArea;
    private TextArea questNameRuArea;
    private TextArea questNameRawEnArea;
    private TextArea questNameRawRuArea;
    private TextArea questDescriptionEnArea;
    private TextArea questDescriptionRuArea;
    private TextArea questDescriptionRawEnArea;
    private TextArea questDescriptionRawRuArea;
    private VerticalLayout stepsLayout;
    private Table stepsTable;
    private BeanItemContainer<QuestStep> stepContainer;
    private Quest currentQuest;
    private StepDirectionNameColumnGenerator stepDirectionNameColumnGenerator;
    private DirectionTranslationColumnGenerator directionTranslationColumnGenerator;

    private final QuestSpecification questSpecification = new QuestSpecification();
    private final QuestLocationSpecification locationSpecification = new QuestLocationSpecification();
    private final QuestStepSpecification questStepSpecification = new QuestStepSpecification();

    public QuestTranslateTab(DBService service_) {
        this.setSizeFull();
        this.service = service_;
        QuestChangeListener questChangeListener = new QuestChangeListener();
        FilterChangeListener filterChangeListener = new FilterChangeListener();
        stepDirectionNameColumnGenerator = new StepDirectionNameColumnGenerator();
        directionTranslationColumnGenerator = new DirectionTranslationColumnGenerator();
        questListlayout = new HorizontalLayout();
        questListlayout.setWidth(100f, Unit.PERCENTAGE);
        locationContainer = new BeanItemContainer<>(Location.class);
        locationTable = new ComboBox("Локация");
        locationTable.setPageLength(15);

        locationTable.setWidth(100f, Unit.PERCENTAGE);
        locationTable.addValueChangeListener(filterChangeListener);
        locationTable.setContainerDataSource(locationContainer);
        locationTable.setFilteringMode(FilteringMode.CONTAINS);
        questContainer = new BeanItemContainer<>(Quest.class);
        questTable = new ComboBox("Квест");
        questTable.setWidth(100f, Unit.PERCENTAGE);
        questTable.setPageLength(15);

        questTable.setWidth(100f, Unit.PERCENTAGE);
        questTable.addValueChangeListener(questChangeListener);
        questTable.setContainerDataSource(questContainer);
        questTable.setFilteringMode(FilteringMode.CONTAINS);
        questListlayout.addComponent(questTable);
        FormLayout locationAndQuestLayout = new FormLayout(locationTable, questTable);
        locationAndQuestLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        locationAndQuestLayout.setSizeFull();
        questListlayout.addComponent(locationAndQuestLayout);
        translateStatus = new ComboBoxMultiselect("Статус перевода", Arrays.asList(TRANSLATE_STATUS.values()));
        translateStatus.setClearButtonCaption("Очистить");
        translateStatus.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LoadFilters();
            }
        });
        noTranslations = new CheckBox("Не переведены полностью");
        noTranslations.setValue(Boolean.FALSE);
        noTranslations.addValueChangeListener(filterChangeListener);
        emptyTranslations = new CheckBox("Не добавлен перевод");
        emptyTranslations.setValue(Boolean.FALSE);
        emptyTranslations.addValueChangeListener(filterChangeListener);
        HorizontalLayout checkBoxlayout = new HorizontalLayout(noTranslations, emptyTranslations);
        translatorBox = new ComboBox("Переводчик");
        translatorBox.setPageLength(15);
        sysAccountContainer = service.loadBeanItems(sysAccountContainer);
        translatorBox.setContainerDataSource(sysAccountContainer);
        translatorBox.setFilteringMode(FilteringMode.CONTAINS);
        translatorBox.addValueChangeListener(filterChangeListener);
        refreshButton = new Button("Обновить");
        refreshButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LoadFilters();
                LoadContent();
            }
        });
        countLabel = new Label();
        searchField = new TextField("Искомая строка");
        searchField.setSizeFull();
        searchField.setNullRepresentation("");
        searchField.addValueChangeListener(filterChangeListener);

        FormLayout filtersLayout = new FormLayout(translateStatus, translatorBox, checkBoxlayout, searchField);
        filtersLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        filtersLayout.setSizeFull();
        questListlayout.addComponent(filtersLayout);
        questListlayout.addComponent(refreshButton);
        questListlayout.addComponent(countLabel);
        questListlayout.setExpandRatio(locationAndQuestLayout, 0.4f);
        questListlayout.setExpandRatio(filtersLayout, 0.4f);
        questListlayout.setExpandRatio(refreshButton, 0.1f);
        questListlayout.setExpandRatio(countLabel, 0.1f);
        questListlayout.setHeight(105f, Unit.PIXELS);
        this.addComponent(questListlayout);
        infoLayout = new VerticalLayout();
        infoLayout.setSizeFull();
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        nameLayout = new VerticalLayout();
        nameLayout.setSizeFull();
        nameHLayout = new HorizontalLayout();
        nameHLayout.setSizeFull();
        nameLayout = new VerticalLayout();
        nameLayout.setSizeFull();
        questNameEnArea = new TextArea("Название");
        questNameEnArea.setSizeFull();
        questNameEnArea.setRows(1);
        questNameEnArea.setReadOnly(true);
        questNameRuArea = new TextArea("Название Ru");
        questNameRuArea.setSizeFull();
        questNameRuArea.setRows(1);
        questNameRuArea.setReadOnly(true);
        questNameRawEnArea = new TextArea("Название в таблицах");
        questNameRawEnArea.setSizeFull();
        questNameRawEnArea.setRows(1);
        questNameRawEnArea.setReadOnly(true);
        questNameRawRuArea = new TextArea("Название в таблицах Ru");
        questNameRawRuArea.setSizeFull();
        questNameRawRuArea.setRows(1);
        questNameRawRuArea.setReadOnly(true);
        nameLayout.addComponents(questNameEnArea, questNameRuArea, questNameRawEnArea, questNameRawRuArea);
        nameHLayout.addComponent(nameLayout);
        nameTranslateLayout = new VerticalLayout();
        nameTranslateLayout.setSizeFull();
        nameHLayout.addComponent(nameTranslateLayout);
        infoLayout.addComponent(nameHLayout);
        descriptionLayout = new VerticalLayout();
        descriptionLayout.setSizeFull();
        descriptionHLayout = new HorizontalLayout();
        descriptionHLayout.setSizeFull();
        descriptionLayout = new VerticalLayout();
        descriptionLayout.setSizeFull();
        questDescriptionEnArea = new TextArea("Описание");
        questDescriptionEnArea.setSizeFull();
        questDescriptionEnArea.setRows(4);
        questDescriptionEnArea.setReadOnly(true);
        questDescriptionRuArea = new TextArea("Описание Ru");
        questDescriptionRuArea.setSizeFull();
        questDescriptionRuArea.setRows(4);
        questDescriptionRuArea.setReadOnly(true);
        questDescriptionRawEnArea = new TextArea("Описание в таблицах");
        questDescriptionRawEnArea.setSizeFull();
        questDescriptionRawEnArea.setRows(4);
        questDescriptionRawEnArea.setReadOnly(true);
        questDescriptionRawRuArea = new TextArea("Описание в таблицах Ru");
        questDescriptionRawRuArea.setSizeFull();
        questDescriptionRawRuArea.setRows(4);
        questDescriptionRawRuArea.setReadOnly(true);
        descriptionLayout.addComponents(questDescriptionEnArea, questDescriptionRuArea, questDescriptionRawEnArea, questDescriptionRawRuArea);
        descriptionHLayout.addComponent(descriptionLayout);
        descriptionTranslateLayout = new VerticalLayout();
        descriptionTranslateLayout.setSizeFull();
        descriptionHLayout.addComponent(descriptionTranslateLayout);
        infoLayout.addComponent(descriptionHLayout);
        tabSheet.addTab(infoLayout, "Квест");
        stepsLayout = new VerticalLayout();
        stepsLayout.setSizeFull();
        stepsTable = new Table();
        stepsTable.addStyleName(ValoTheme.TABLE_COMPACT);
        stepsTable.setSizeFull();
        stepContainer = new BeanItemContainer<>(QuestStep.class);
        stepsTable.setContainerDataSource(stepContainer);
        //stepsTable.setPageLength(0);
        //stepsTable.setCacheRate(0);
        stepsTable.addGeneratedColumn("stepDescription", new StepDescriptionColumnGenerator());
        stepsTable.addGeneratedColumn("stepDescriptionTranslation", new StepTranslationColumnGenerator());
        stepsTable.addGeneratedColumn("stepDirections", new StepDirectionsColumnGenerator());
        stepsTable.setColumnExpandRatio("stepDescription", 1f);
        stepsTable.setColumnExpandRatio("stepDescriptionTranslation", 1f);
        stepsTable.setColumnExpandRatio("stepDirections", 2f);
        stepsTable.setVisibleColumns("stepDescription", "stepDescriptionTranslation", "stepDirections");
        stepsTable.setColumnHeaders("", "", "");
        stepsLayout.addComponent(stepsTable);
        tabSheet.addTab(stepsLayout, "Стадии");
        this.addComponent(tabSheet);
        this.setExpandRatio(tabSheet, 1f);
        LoadFilters();
    }

    private void LoadContent() {
        Quest q = (Quest) questTable.getValue();
        stepContainer.removeAllItems();
        if (q != null) {
            currentQuest = service.getQuest(q);
            questStepSpecification.setQuest(currentQuest);
            questStepSpecification.setNoTranslations(noTranslations.getValue());
            questStepSpecification.setEmptyTranslations(emptyTranslations.getValue());
            questStepSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
            questStepSpecification.setTranslator((SysAccount) translatorBox.getValue());
            questStepSpecification.setSearchString(searchField.getValue());
            nameTranslateLayout.removeAllComponents();
            questNameEnArea.setReadOnly(false);
            questNameRuArea.setReadOnly(false);
            questNameRawEnArea.setReadOnly(false);
            questNameRawRuArea.setReadOnly(false);
            if (currentQuest.getName() != null) {
                questNameEnArea.setValue(currentQuest.getName());
            } else {
                questNameEnArea.setValue(null);
            }
            if (currentQuest.getNameRu() != null) {
                questNameRuArea.setValue(currentQuest.getNameRu());
            } else {
                questNameRuArea.setValue(null);
            }
            if (currentQuest.getSheetsQuestName() != null) {
                questNameRawEnArea.setValue(currentQuest.getSheetsQuestName().getTextEn());
                questNameRawRuArea.setValue(currentQuest.getSheetsQuestName().getTextRu());
                List<SysAccount> accounts = new ArrayList<SysAccount>();
                for (TranslatedText t : currentQuest.getSheetsQuestName().getTranslatedTexts()) {
                    accounts.add(t.getAuthor());
                    TranslationCell cell = new TranslationCell(t);
                    nameTranslateLayout.addComponent(cell);
                }
                if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                    final TranslatedText translatedText = new TranslatedText();
                    translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                    translatedText.setSpreadSheetsQuestName(currentQuest.getSheetsQuestName());
                    Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
                    addTranslation.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {

                            if (translatedText.getSpreadSheetsQuestName() != null) {
                                translatedText.getSpreadSheetsQuestName().getTranslatedTexts().add(translatedText);
                            }
                            TranslationCell cell = new TranslationCell(translatedText);
                            nameTranslateLayout.addComponent(cell);
                            event.getButton().setVisible(false);
                        }
                    });
                    nameTranslateLayout.addComponent(addTranslation);
                }
            } else {
                questNameRawEnArea.setValue(null);
                questNameRawRuArea.setValue(null);
            }
            questNameEnArea.setReadOnly(true);
            questNameRuArea.setReadOnly(true);
            questNameRawEnArea.setReadOnly(true);
            questNameRawRuArea.setReadOnly(true);
            descriptionTranslateLayout.removeAllComponents();
            questDescriptionEnArea.setReadOnly(false);
            questDescriptionRuArea.setReadOnly(false);
            questDescriptionRawEnArea.setReadOnly(false);
            questDescriptionRawRuArea.setReadOnly(false);
            if (currentQuest.getDescriptionEn() != null) {
                questDescriptionEnArea.setValue(currentQuest.getDescriptionEn());
            } else {
                questDescriptionEnArea.setValue(null);
            }
            if (currentQuest.getDescriptionRu() != null) {
                questDescriptionRuArea.setValue(currentQuest.getDescriptionRu());
            } else {
                questDescriptionRuArea.setValue(null);
            }
            if (currentQuest.getSheetsQuestDescription() != null) {
                questDescriptionRawEnArea.setValue(currentQuest.getSheetsQuestDescription().getTextEn());
                questDescriptionRawRuArea.setValue(currentQuest.getSheetsQuestDescription().getTextRu());
                List<SysAccount> accounts = new ArrayList<SysAccount>();
                for (TranslatedText t : currentQuest.getSheetsQuestDescription().getTranslatedTexts()) {
                    accounts.add(t.getAuthor());
                    TranslationCell cell = new TranslationCell(t);
                    descriptionTranslateLayout.addComponent(cell);
                }
                if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                    final TranslatedText translatedText = new TranslatedText();
                    translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                    translatedText.setSpreadSheetsQuestDescription(currentQuest.getSheetsQuestDescription());
                    Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
                    addTranslation.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {

                            if (translatedText.getSpreadSheetsQuestDescription() != null) {
                                translatedText.getSpreadSheetsQuestDescription().getTranslatedTexts().add(translatedText);
                            }
                            descriptionTranslateLayout.addComponent(new TranslationCell(translatedText));
                            event.getButton().setVisible(false);
                        }
                    });
                    descriptionTranslateLayout.addComponent(addTranslation);
                }
            } else {
                questDescriptionRawEnArea.setValue(null);
                questDescriptionRawRuArea.setValue(null);
            }
            questDescriptionEnArea.setReadOnly(true);
            questDescriptionRuArea.setReadOnly(true);
            questDescriptionRawEnArea.setReadOnly(true);
            questDescriptionRawRuArea.setReadOnly(true);
            stepContainer.addAll(service.getQuestStepRepository().findAll(questStepSpecification));
            stepsTable.setPageLength(4);
        }
    }

    private void LoadFilters() {
        locationContainer.removeAllItems();
        questContainer.removeAllItems();
        questSpecification.setNoTranslations(noTranslations.getValue());
        questSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        questSpecification.setTranslator((SysAccount) translatorBox.getValue());
        questSpecification.setLocation((Location) locationTable.getValue());
        questSpecification.setEmptyTranslations(emptyTranslations.getValue());
        questSpecification.setSearchString(searchField.getValue());
        questContainer.addAll(service.getQuestRepository().findAll(questSpecification));
        locationSpecification.setNoTranslations(noTranslations.getValue());
        locationSpecification.setEmptyTranslations(emptyTranslations.getValue());
        locationSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        locationSpecification.setTranslator((SysAccount) translatorBox.getValue());
        locationSpecification.setSearchString(searchField.getValue());
        locationContainer.addAll(service.getLocationRepository().findAll(locationSpecification));
        Long countTranslatedTextFilterResult = service.countTranslatedQuestTextFilterResult((Location) locationTable.getValue(), (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
        countLabel.setCaption(countTranslatedTextFilterResult.toString());
    }

    private class QuestChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            if (questTable.getValue() != null) {
                LoadContent();
            }
        }
    }

    private class FilterChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            LoadFilters();
            LoadContent();
        }
    }

    private class StepDescriptionColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            QuestStep topic = (QuestStep) itemId;
            if (topic.getTextEn() != null && !topic.getTextEn().isEmpty()) {
                TextArea textEnArea = new TextArea("Текст в игре");
                textEnArea.setValue(topic.getTextEn());
                textEnArea.setReadOnly(true);
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                textEnArea.setNullRepresentation("");
                result.addComponent(textEnArea);
            }
            if (topic.getTextRu() != null && !topic.getTextRu().isEmpty()) {
                TextArea textRuArea = new TextArea("Перевод в игре");
                textRuArea.setValue(topic.getTextRu());
                textRuArea.setReadOnly(true);
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                textRuArea.setNullRepresentation("");
                result.addComponent(textRuArea);
            }
            if (topic.getSheetsJournalEntry() != null) {
                TextArea textEnRawArea = new TextArea("Текст в таблицах");
                textEnRawArea.setValue(topic.getSheetsJournalEntry().getTextEn());
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                textEnRawArea.setNullRepresentation("");
                result.addComponent(textEnRawArea);
                if (topic.getSheetsJournalEntry().getTextRu() != null && !topic.getSheetsJournalEntry().getTextRu().equals(topic.getSheetsJournalEntry().getTextEn())) {
                    TextArea textRuRawArea = new TextArea("Перевод в таблицах от " + topic.getSheetsJournalEntry().getTranslator());
                    textRuRawArea.setValue(topic.getSheetsJournalEntry().getTextRu());
                    textRuRawArea.setReadOnly(true);
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    textRuRawArea.setNullRepresentation("");
                    result.addComponent(textRuRawArea);//, "Перевод в таблицах" 
                }
            }

            return result;
        }

    }

    private class StepDirectionNameColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            QuestDirection d = (QuestDirection) itemId;
            if (d.getDirectionType() != null) {
                Label l = new Label(d.getDirectionType().name());
                result.addComponent(l);
            }
            if (d.getTextEn() != null && !d.getTextEn().isEmpty()) {
                TextArea textEnArea = new TextArea("Текст в игре");
                textEnArea.setValue(d.getTextEn());
                textEnArea.setRows(2);
                textEnArea.setReadOnly(true);
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                textEnArea.setNullRepresentation("");
                result.addComponent(textEnArea);
            }
            if (d.getTextRu() != null && !d.getTextRu().isEmpty()) {
                TextArea textRuArea = new TextArea("Перевод в игре");
                textRuArea.setValue(d.getTextRu());
                textRuArea.setRows(2);
                textRuArea.setReadOnly(true);
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                textRuArea.setNullRepresentation("");
                result.addComponent(textRuArea);
            }
            if (d.getSheetsQuestDirection() != null) {
                TextArea textEnRawArea = new TextArea("Текст в таблицах");
                textEnRawArea.setValue(d.getSheetsQuestDirection().getTextEn());
                textEnRawArea.setRows(2);
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                textEnRawArea.setNullRepresentation("");
                result.addComponent(textEnRawArea);
                if (d.getSheetsQuestDirection().getTextRu() != null && !d.getSheetsQuestDirection().getTextRu().equals(d.getSheetsQuestDirection().getTextEn())) {
                    TextArea textRuRawArea = new TextArea("Перевод в таблицах от " + d.getSheetsQuestDirection().getTranslator());
                    textRuRawArea.setValue(d.getSheetsQuestDirection().getTextRu());
                    textRuRawArea.setRows(2);
                    textRuRawArea.setReadOnly(true);
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    textRuRawArea.setNullRepresentation("");
                    result.addComponent(textRuRawArea);
                }
            }

            return result;
        }

    }

    private class StepDirectionsColumnGenerator implements Table.ColumnGenerator {

        private Table directionsTable;
        private BeanItemContainer<QuestDirection> directionsContainer;

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            QuestStep step = (QuestStep) itemId;
            directionsTable = new Table();
            //directionsTable.setSizeFull();
            directionsTable.setWidth(100f, Unit.PERCENTAGE);
            directionsTable.setPageLength(0);
            directionsContainer = new BeanItemContainer<>(QuestDirection.class);
            directionsContainer.addAll(service.getQuestDirectionRepository().findAll(new QuestDirectionSpecification(step, (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue())));
            directionsTable.setContainerDataSource(directionsContainer);
            directionsTable.addGeneratedColumn("stepDirectionName", stepDirectionNameColumnGenerator);
            directionsTable.addGeneratedColumn("directionTranslation", directionTranslationColumnGenerator);
            directionsTable.setColumnExpandRatio("stepDirectionName", 1f);
            directionsTable.setColumnExpandRatio("directionTranslation", 1f);
            directionsTable.setVisibleColumns("stepDirectionName", "directionTranslation");
            directionsTable.setColumnHeaders("", "");
            return directionsTable;
        }

    }

    private class StepTranslationColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final VerticalLayout vl = new VerticalLayout();
            vl.setSizeFull();
            Set<TranslatedText> list = new HashSet<>();
            List<SysAccount> accounts = new ArrayList<>();

            QuestStep step = (QuestStep) itemId;
            String text = step.getTextEn();
            list.addAll(step.getSheetsJournalEntry().getTranslatedTexts());

            if (list != null) {
                for (TranslatedText t : list) {
                    vl.addComponent(new TranslationCell(t));
                    accounts.add(t.getAuthor());
                }
            }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && text != null && !text.isEmpty() && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                final TranslatedText translatedText = new TranslatedText();
                translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                translatedText.setSpreadSheetsJournalEntry(step.getSheetsJournalEntry());
                Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
                addTranslation.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {

                        if (translatedText.getSpreadSheetsJournalEntry() != null) {
                            translatedText.getSpreadSheetsJournalEntry().getTranslatedTexts().add(translatedText);
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

    private class DirectionTranslationColumnGenerator implements Table.ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final VerticalLayout vl = new VerticalLayout();
            vl.setSizeFull();
            Set<TranslatedText> list = new HashSet<>();
            List<SysAccount> accounts = new ArrayList<>();

            QuestDirection d = (QuestDirection) itemId;
            String text = d.getTextEn();
            list.addAll(d.getSheetsQuestDirection().getTranslatedTexts());

            if (list != null) {
                for (TranslatedText t : list) {
                    vl.addComponent(new TranslationCell(t));
                    accounts.add(t.getAuthor());
                }
            }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && text != null && !text.isEmpty() && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                final TranslatedText translatedText = new TranslatedText();
                translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                translatedText.setSpreadSheetsQuestDirection(d.getSheetsQuestDirection());
                Button addTranslation = new Button("Добавить перевод");
                addTranslation.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {

                        if (translatedText.getSpreadSheetsQuestDirection() != null) {
                            translatedText.getSpreadSheetsQuestDirection().getTranslatedTexts().add(translatedText);
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
            this.setExpandRatio(translation, 1f);
            save = new Button("Сохранить", FontAwesome.SAVE);
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translatedText.setText(translation.getValue());
                    service.saveTranslatedText(translatedText);
                    LoadContent();
                }
            });

            this.addComponent(save);
            if (translatedText.getStatus() != null && translatedText.getStatus() == TRANSLATE_STATUS.DIRTY) {
                save.setVisible(true);
            } else {
                save.setVisible(false);
            }
            if ((SpringSecurityHelper.hasRole("ROLE_PREAPPROVE")) && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.NEW)) {
                translation.setReadOnly(false);
                preAccept = new Button("Перевод верен", FontAwesome.CHECK);
                preAccept.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.preAcceptTranslatedText(translatedText);
                        LoadContent();
                        LoadFilters();
                    }
                });
                this.addComponent(preAccept);
            }

            if ((SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED)) {
                translation.setReadOnly(false);
                correct = new Button("Текст корректен", FontAwesome.PENCIL);
                correct.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.correctTranslatedText(translatedText);
                        LoadContent();
                        LoadFilters();
                    }
                });
                this.addComponent(correct);
            }

            if ((SpringSecurityHelper.hasRole("ROLE_APPROVE")) && translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED) || (translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED))) {
                translation.setReadOnly(false);
                accept = new Button("Принять эту версию", FontAwesome.THUMBS_UP);
                accept.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.acceptTranslatedText(translatedText);
                        LoadContent();
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
                        LoadContent();
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
