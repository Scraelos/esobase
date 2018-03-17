/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.HasValue;
import com.vaadin.data.TreeData;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.server.ExternalResource;
import com.vaadin.v7.data.Property;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.ItemCollapseAllowedProvider;
import com.vaadin.ui.Panel;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TreeGrid;
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
import java.util.logging.Logger;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.specification.QuestDirectionSpecification;
import org.esn.esobase.data.specification.QuestItemSpecification;
import org.esn.esobase.data.specification.QuestLocationSpecification;
import org.esn.esobase.data.specification.QuestSpecification;
import org.esn.esobase.data.specification.QuestStepSpecification;
import org.esn.esobase.model.GSpreadSheetsItemName;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.QuestDirection;
import org.esn.esobase.model.QuestItem;
import org.esn.esobase.model.QuestStep;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.security.SpringSecurityHelper;
import org.vaadin.addons.comboboxmultiselect.ComboBoxMultiselect;
import org.vaadin.extension.gridscroll.GridScrollExtension;

/**
 *
 * @author scraelos
 */
public class QuestTranslateTab extends VerticalLayout {

    private final DBService service;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private HorizontalLayout questListlayout;
    private ComboBox<Quest> questTable;

    private TabSheet tabSheet;
    private VerticalLayout infoLayout;
    private HorizontalLayout nameHLayout;
    private VerticalLayout nameLayout;
    private VerticalLayout nameTranslateLayout;
    private HorizontalLayout descriptionHLayout;
    private VerticalLayout descriptionLayout;
    private VerticalLayout descriptionTranslateLayout;

    private ComboBox<Location> locationTable;
    private ComboBoxMultiselect translateStatus;
    private CheckBox noTranslations;
    private CheckBox emptyTranslations;
    private TextField searchField;
    private ComboBox<SysAccount> translatorBox;
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
    private TreeGrid stepsGrid;
    private TreeData stepsData;
    private Grid itemsGrid;
    private Quest currentQuest;
    private RowStyleGenerator rowStyleGenerator = new RowStyleGenerator();

    private List<Location> locations = new ArrayList<>();
    private List<Quest> questList = new ArrayList<>();
    private List<QuestItem> itemList = new ArrayList<>();

    private final QuestSpecification questSpecification = new QuestSpecification();
    private final QuestLocationSpecification locationSpecification = new QuestLocationSpecification();
    private final QuestStepSpecification questStepSpecification = new QuestStepSpecification();
    
    public QuestTranslateTab(DBService service_) {
        this.setSizeFull();
        this.setSpacing(false);
        this.setMargin(false);
        this.service = service_;
        QuestChangeListener questChangeListener = new QuestChangeListener();
        FilterChangeListener filterChangeListener = new FilterChangeListener();
        questListlayout = new HorizontalLayout();
        questListlayout.setWidth(100f, Unit.PERCENTAGE);
        questListlayout.setSpacing(false);
        questListlayout.setMargin(false);
        locationTable = new ComboBox("Локация");
        locationTable.setPageLength(30);
        locationTable.setScrollToSelectedItem(true);
        locationTable.setWidth(100f, Unit.PERCENTAGE);
        locationTable.addValueChangeListener(filterChangeListener);
        locationTable.setDataProvider(new ListDataProvider<>(locations));
        questTable = new ComboBox("Квест");
        questTable.setWidth(100f, Unit.PERCENTAGE);
        questTable.setPageLength(15);

        questTable.setWidth(100f, Unit.PERCENTAGE);
        questTable.addValueChangeListener(questChangeListener);
        questTable.setDataProvider(new ListDataProvider<>(questList));
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
                LoadContent();
            }
        });
        noTranslations = new CheckBox("Не переведены полностью");
        noTranslations.setValue(Boolean.FALSE);
        noTranslations.addValueChangeListener(filterChangeListener);
        emptyTranslations = new CheckBox("Не добавлен перевод");
        emptyTranslations.setValue(Boolean.FALSE);
        emptyTranslations.addValueChangeListener(filterChangeListener);
        HorizontalLayout checkBoxlayout = new HorizontalLayout(noTranslations, emptyTranslations);
        checkBoxlayout.setSpacing(false);
        checkBoxlayout.setMargin(false);
        translatorBox = new ComboBox("Переводчик");
        translatorBox.setPageLength(15);
        translatorBox.setDataProvider(new ListDataProvider<SysAccount>(service.getSysAccounts()));
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
        infoLayout.setSpacing(false);
        infoLayout.setMargin(false);
        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        nameLayout = new VerticalLayout();
        nameLayout.setSizeFull();
        nameHLayout = new HorizontalLayout();
        nameHLayout.setSizeFull();
        nameHLayout.setSpacing(false);
        nameHLayout.setMargin(false);
        nameLayout = new VerticalLayout();
        nameLayout.setSizeFull();
        nameLayout.setSpacing(false);
        nameLayout.setMargin(false);
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
        nameTranslateLayout.setSpacing(false);
        nameTranslateLayout.setMargin(false);
        nameHLayout.addComponent(nameTranslateLayout);
        infoLayout.addComponent(nameHLayout);
        descriptionLayout = new VerticalLayout();
        descriptionLayout.setSizeFull();
        descriptionHLayout = new HorizontalLayout();
        descriptionHLayout.setSizeFull();
        descriptionHLayout.setSpacing(false);
        descriptionHLayout.setMargin(false);
        descriptionLayout = new VerticalLayout();
        descriptionLayout.setSizeFull();
        descriptionLayout.setSpacing(false);
        descriptionLayout.setMargin(false);
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
        descriptionTranslateLayout.setSpacing(false);
        descriptionTranslateLayout.setMargin(false);
        descriptionHLayout.addComponent(descriptionTranslateLayout);
        infoLayout.addComponent(descriptionHLayout);
        tabSheet.addTab(infoLayout, "Квест");
        stepsLayout = new VerticalLayout();
        stepsLayout.setSizeFull();
        stepsLayout.setSpacing(false);
        stepsLayout.setMargin(false);
        stepsData = new TreeData();
        stepsGrid = new TreeGrid(new TreeDataProvider(stepsData));
        stepsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        stepsGrid.setRowHeight(250);
        stepsGrid.setHeaderVisible(false);
        stepsGrid.setSizeFull();
        stepsGrid.setItemCollapseAllowedProvider(new ItemCollapseAllowedProvider() {
            @Override
            public boolean test(Object item) {
                return false;
            }
        });
        stepsGrid.addColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                if (source instanceof QuestStep) {
                    return "Стадия";
                }
                if (source instanceof QuestDirection) {
                    return "Цель - " + ((QuestDirection) source).getDirectionType().name();
                }
                return null;
            }
        }).setId("rowType").setCaption("Тип").setWidth(132).setStyleGenerator(rowStyleGenerator);
        stepsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                VerticalLayout result = new VerticalLayout();
                result.setSpacing(false);
                result.setMargin(false);
                if (source instanceof QuestStep) {

                    QuestStep step = (QuestStep) source;
                    if (step.getTextEn() != null && !step.getTextEn().isEmpty()) {
                        TextArea textEnArea = new TextArea("Текст в игре");
                        textEnArea.setValue(step.getTextEn());
                        textEnArea.setReadOnly(true);
                        textEnArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textEnArea);
                    }
                    if (step.getTextRu() != null && !step.getTextRu().isEmpty()) {
                        TextArea textRuArea = new TextArea("Перевод в игре");
                        textRuArea.setValue(step.getTextRu());
                        textRuArea.setReadOnly(true);
                        textRuArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textRuArea);
                    }
                } else if (source instanceof QuestDirection) {
                    QuestDirection d = (QuestDirection) source;
                    if (d.getTextEn() != null && !d.getTextEn().isEmpty()) {
                        TextArea textEnArea = new TextArea("Текст в игре");
                        textEnArea.setValue(d.getTextEn());
                        textEnArea.setRows(2);
                        textEnArea.setReadOnly(true);
                        textEnArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textEnArea);
                    }
                    if (d.getTextRu() != null && !d.getTextRu().isEmpty()) {
                        TextArea textRuArea = new TextArea("Перевод в игре");
                        textRuArea.setValue(d.getTextRu());
                        textRuArea.setRows(2);
                        textRuArea.setReadOnly(true);
                        textRuArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textRuArea);
                    }

                }
                return result;
            }
        }).setId("ingameText").setStyleGenerator(rowStyleGenerator);
        stepsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                VerticalLayout result = new VerticalLayout();
                result.setSpacing(false);
                result.setMargin(false);
                if (source instanceof QuestStep) {

                    QuestStep step = (QuestStep) source;
                    if (step.getSheetsJournalEntry() != null) {
                        TextArea textEnRawArea = new TextArea("Текст в таблицах");
                        textEnRawArea.setValue(step.getSheetsJournalEntry().getTextEn());
                        textEnRawArea.setReadOnly(true);
                        textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textEnRawArea);
                        if (step.getSheetsJournalEntry().getTextRu() != null && !step.getSheetsJournalEntry().getTextRu().equals(step.getSheetsJournalEntry().getTextEn())) {
                            TextArea textRuRawArea = new TextArea("Перевод в таблицах от " + step.getSheetsJournalEntry().getTranslator());
                            textRuRawArea.setValue(step.getSheetsJournalEntry().getTextRu());
                            textRuRawArea.setReadOnly(true);
                            textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                            result.addComponent(textRuRawArea);//, "Перевод в таблицах" 
                        }
                    }

                } else if (source instanceof QuestDirection) {
                    QuestDirection d = (QuestDirection) source;
                    if (d.getSheetsQuestDirection() != null) {
                        TextArea textEnRawArea = new TextArea("Текст в таблицах");
                        textEnRawArea.setValue(d.getSheetsQuestDirection().getTextEn());
                        textEnRawArea.setRows(2);
                        textEnRawArea.setReadOnly(true);
                        textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textEnRawArea);
                        if (d.getSheetsQuestDirection().getTextRu() != null && !d.getSheetsQuestDirection().getTextRu().equals(d.getSheetsQuestDirection().getTextEn())) {
                            TextArea textRuRawArea = new TextArea("Перевод в таблицах от " + d.getSheetsQuestDirection().getTranslator());
                            textRuRawArea.setValue(d.getSheetsQuestDirection().getTextRu());
                            textRuRawArea.setRows(2);
                            textRuRawArea.setReadOnly(true);
                            textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                            result.addComponent(textRuRawArea);
                        }
                    }
                }
                return result;
            }
        }).setId("rawText").setStyleGenerator(rowStyleGenerator);
        stepsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                Panel panel = new Panel();
                panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
                panel.setWidth(100f, Unit.PERCENTAGE);
                panel.setHeight(245f, Unit.PIXELS);
                final VerticalLayout result = new VerticalLayout();
                result.setSpacing(false);
                result.setMargin(false);
                if (source instanceof QuestStep) {

                    Set<TranslatedText> list = new HashSet<>();
                    List<SysAccount> accounts = new ArrayList<>();

                    QuestStep step = (QuestStep) source;
                    String text = step.getTextEn();
                    list.addAll(step.getSheetsJournalEntry().getTranslatedTexts());

                    if (list != null) {
                        for (TranslatedText t : list) {
                            result.addComponent(new TranslationCell(t));
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
                                result.addComponent(new TranslationCell(translatedText));
                                event.getButton().setVisible(false);
                            }
                        });
                        result.addComponent(addTranslation);
                    }
                } else if (source instanceof QuestDirection) {
                    Set<TranslatedText> list = new HashSet<>();
                    List<SysAccount> accounts = new ArrayList<>();

                    QuestDirection d = (QuestDirection) source;
                    String text = d.getTextEn();
                    list.addAll(d.getSheetsQuestDirection().getTranslatedTexts());

                    if (list != null) {
                        for (TranslatedText t : list) {
                            result.addComponent(new TranslationCell(t));
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
                                result.addComponent(new TranslationCell(translatedText));
                                event.getButton().setVisible(false);
                            }
                        });
                        result.addComponent(addTranslation);
                    }
                }
                panel.setContent(result);
                return panel;
            }
        }).setId("translation").setStyleGenerator(rowStyleGenerator);
        stepsGrid.setColumns("rowType", "ingameText", "rawText", "translation");
        stepsLayout.addComponent(stepsGrid);
        tabSheet.addTab(stepsLayout, "Стадии");
        itemsGrid = new Grid(new ListDataProvider(itemList));
        itemsGrid.setSelectionMode(Grid.SelectionMode.NONE);
        itemsGrid.setRowHeight(250);
        itemsGrid.setHeaderVisible(false);
        itemsGrid.setSizeFull();
        itemsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                VerticalLayout result = new VerticalLayout();
                result.setSpacing(false);
                result.setMargin(false);
                if (source instanceof QuestItem) {

                    QuestItem item = (QuestItem) source;
                    if (item.getName() != null) {
                        TextArea textEnRawArea = new TextArea("Название в таблицах");
                        textEnRawArea.setValue(item.getName().getTextEn());
                        textEnRawArea.setReadOnly(true);
                        textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textEnRawArea);
                        if (item.getName().getTextRu() != null && !item.getName().getTextRu().equals(item.getName().getTextEn())) {
                            TextArea textRuRawArea = new TextArea("Перевод названия в таблицах от " + item.getName().getTranslator());
                            textRuRawArea.setValue(item.getName().getTextRu());
                            textRuRawArea.setReadOnly(true);
                            textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                            result.addComponent(textRuRawArea);//, "Перевод в таблицах" 
                        }
                    }

                }
                return result;
            }
        }).setId("rawName");
        itemsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                VerticalLayout result = new VerticalLayout();
                result.setSpacing(false);
                result.setMargin(false);
                if (source instanceof QuestItem) {

                    QuestItem item = (QuestItem) source;
                    if (item.getDescription() != null) {
                        TextArea textEnRawArea = new TextArea("Описание в таблицах");
                        textEnRawArea.setValue(item.getDescription().getTextEn());
                        textEnRawArea.setReadOnly(true);
                        textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                        result.addComponent(textEnRawArea);
                        if (item.getDescription().getTextRu() != null && !item.getDescription().getTextRu().equals(item.getDescription().getTextEn())) {
                            TextArea textRuRawArea = new TextArea("Перевод описания в таблицах от " + item.getDescription().getTranslator());
                            textRuRawArea.setValue(item.getDescription().getTextRu());
                            textRuRawArea.setReadOnly(true);
                            textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                            result.addComponent(textRuRawArea);//, "Перевод в таблицах" 
                        }
                    }

                }
                return result;
            }
        }).setId("rawDescription");
        itemsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                Panel panel = new Panel();
                panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
                panel.setWidth(100f, Unit.PERCENTAGE);
                panel.setHeight(245f, Unit.PIXELS);
                final VerticalLayout result = new VerticalLayout();
                result.setSpacing(false);
                result.setMargin(false);
                if (source instanceof QuestItem) {

                    Set<TranslatedText> list = new HashSet<>();
                    List<SysAccount> accounts = new ArrayList<>();

                    QuestItem item = (QuestItem) source;
                    if (item.getName() != null) {
                        String text = item.getName().getTextEn();
                        list.addAll(item.getName().getTranslatedTexts());

                        if (list != null) {
                            for (TranslatedText t : list) {
                                result.addComponent(new TranslationCell(t));
                                accounts.add(t.getAuthor());
                            }
                        }
                        if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && text != null && !text.isEmpty() && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                            final TranslatedText translatedText = new TranslatedText();
                            translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                            translatedText.setSpreadSheetsItemName(item.getName());
                            Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
                            addTranslation.addClickListener(new Button.ClickListener() {

                                @Override
                                public void buttonClick(Button.ClickEvent event) {

                                    if (translatedText.getSpreadSheetsItemName() != null) {
                                        translatedText.getSpreadSheetsItemName().getTranslatedTexts().add(translatedText);
                                    }
                                    result.addComponent(new TranslationCell(translatedText));
                                    event.getButton().setVisible(false);
                                }
                            });
                            result.addComponent(addTranslation);
                        }
                    }
                }
                panel.setContent(result);
                return panel;
            }
        }).setId("nameTranslation");
        itemsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                Panel panel = new Panel();
                panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
                panel.setWidth(100f, Unit.PERCENTAGE);
                panel.setHeight(245f, Unit.PIXELS);
                final VerticalLayout result = new VerticalLayout();
                result.setSpacing(false);
                result.setMargin(false);
                if (source instanceof QuestItem) {

                    Set<TranslatedText> list = new HashSet<>();
                    List<SysAccount> accounts = new ArrayList<>();

                    QuestItem item = (QuestItem) source;
                    if (item.getDescription() != null) {
                        String text = item.getDescription().getTextEn();
                        list.addAll(item.getDescription().getTranslatedTexts());

                        if (list != null) {
                            for (TranslatedText t : list) {
                                result.addComponent(new TranslationCell(t));
                                accounts.add(t.getAuthor());
                            }
                        }
                        if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && text != null && !text.isEmpty() && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
                            final TranslatedText translatedText = new TranslatedText();
                            translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                            translatedText.setSpreadSheetsItemDescription(item.getDescription());
                            Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
                            addTranslation.addClickListener(new Button.ClickListener() {

                                @Override
                                public void buttonClick(Button.ClickEvent event) {

                                    if (translatedText.getSpreadSheetsItemDescription() != null) {
                                        translatedText.getSpreadSheetsItemDescription().getTranslatedTexts().add(translatedText);
                                    }
                                    result.addComponent(new TranslationCell(translatedText));
                                    event.getButton().setVisible(false);
                                }
                            });
                            result.addComponent(addTranslation);
                        }
                    }
                }
                panel.setContent(result);
                return panel;
            }
        }).setId("descriptionTranslation");
        itemsGrid.addComponentColumn(new ValueProvider() {
            @Override
            public Object apply(Object source) {
                VerticalLayout result=new VerticalLayout();
                result.setMargin(new MarginInfo(true, false, false, false));
                result.setSpacing(false);
                if (source instanceof QuestItem) {
                    GSpreadSheetsItemName name = ((QuestItem) source).getName();
                    if (name.getIcon() != null) {
                        Image image = new Image(null, new ExternalResource("http://esoicons.uesp.net" + name.getIcon().replaceAll(".dds", ".png")));
                        result.addComponent(image);
                        return result;
                    }
                }
                return result;
            }
        }).setId("icon").setWidth(95);
        itemsGrid.setColumns("icon", "rawName", "nameTranslation", "rawDescription", "descriptionTranslation");
        tabSheet.addTab(itemsGrid, "Предметы");
        this.addComponent(tabSheet);
        this.setExpandRatio(tabSheet, 1f);
        GridScrollExtension stepsScrollExtension=new GridScrollExtension(stepsGrid);
        GridScrollExtension itemsScrollExtension=new GridScrollExtension(itemsGrid);
        LoadFilters();
    }
    
    

    private class RowStyleGenerator implements StyleGenerator {

        @Override
        public String apply(Object item) {
            if (item instanceof QuestStep) {
                return "step_row";
            }
            if (item instanceof QuestDirection) {
                return "direction_row";
            }
            return null;
        }

    }
    
    private void LoadContent() {
        Quest q = (Quest) questTable.getValue();
        stepsData.clear();
        if (q != null) {
            currentQuest = service.getQuest(q);
            questStepSpecification.setQuest(currentQuest);
            questStepSpecification.setNoTranslations(noTranslations.getValue());
            questStepSpecification.setEmptyTranslations(emptyTranslations.getValue());
            questStepSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
            questStepSpecification.setTranslator(translatorBox.getValue());
            questStepSpecification.setSearchString(searchField.getValue());
            nameTranslateLayout.removeAllComponents();
            questNameEnArea.setReadOnly(false);
            questNameRuArea.setReadOnly(false);
            questNameRawEnArea.setReadOnly(false);
            questNameRawRuArea.setReadOnly(false);
            if (currentQuest.getName() != null) {
                questNameEnArea.setValue(currentQuest.getName());
            } else {
                questNameEnArea.setValue("");
            }
            if (currentQuest.getNameRu() != null) {
                questNameRuArea.setValue(currentQuest.getNameRu());
            } else {
                questNameRuArea.setValue("");
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
                questNameRawEnArea.setValue("");
                questNameRawRuArea.setValue("");
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
                questDescriptionEnArea.setValue("");
            }
            if (currentQuest.getDescriptionRu() != null) {
                questDescriptionRuArea.setValue(currentQuest.getDescriptionRu());
            } else {
                questDescriptionRuArea.setValue("");
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
                questDescriptionRawEnArea.setValue("");
                questDescriptionRawRuArea.setValue("");
            }
            questDescriptionEnArea.setReadOnly(true);
            questDescriptionRuArea.setReadOnly(true);
            questDescriptionRawEnArea.setReadOnly(true);
            questDescriptionRawRuArea.setReadOnly(true);
            List<QuestStep> steps = service.getQuestStepRepository().findAll(questStepSpecification);
            for (QuestStep qs : steps) {
                stepsData.addItem(null, qs);
                List<QuestDirection> directions = service.getQuestDirectionRepository().findAll(new QuestDirectionSpecification(qs, (Set<TRANSLATE_STATUS>) translateStatus.getValue(), translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue()));
                for (QuestDirection qd : directions) {
                    stepsData.addItem(qs, qd);
                }
            }
            stepsGrid.getDataProvider().refreshAll();
            stepsGrid.expand(stepsData.getRootItems());
            itemList.clear();
            if (q.getItems() != null) {
                itemList.addAll(service.getQuestItemRepository().findAll(new QuestItemSpecification(q, (Set<TRANSLATE_STATUS>) translateStatus.getValue(), translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue())));
            }
            itemsGrid.getDataProvider().refreshAll();
        }
    }

    private void LoadFilters() {
        locations.clear();
        questList.clear();
        questSpecification.setNoTranslations(noTranslations.getValue());
        questSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        questSpecification.setTranslator((SysAccount) translatorBox.getValue());
        questSpecification.setLocation((Location) locationTable.getValue());
        questSpecification.setEmptyTranslations(emptyTranslations.getValue());
        questSpecification.setSearchString(searchField.getValue());
        questList.addAll(service.getQuestRepository().findAll(questSpecification));
        questTable.getDataProvider().refreshAll();
        locationSpecification.setNoTranslations(noTranslations.getValue());
        locationSpecification.setEmptyTranslations(emptyTranslations.getValue());
        locationSpecification.setTranslateStatus((Set<TRANSLATE_STATUS>) translateStatus.getValue());
        locationSpecification.setTranslator((SysAccount) translatorBox.getValue());
        locationSpecification.setSearchString(searchField.getValue());
        locations.addAll(service.getLocationRepository().findAll(locationSpecification));
        locationTable.getDataProvider().refreshAll();
        Long countTranslatedTextFilterResult = service.countTranslatedQuestTextFilterResult((Location) locationTable.getValue(), (Set<TRANSLATE_STATUS>) translateStatus.getValue(), (SysAccount) translatorBox.getValue(), noTranslations.getValue(), emptyTranslations.getValue(), searchField.getValue());
        countLabel.setCaption(countTranslatedTextFilterResult.toString());
    }

    private class QuestChangeListener implements HasValue.ValueChangeListener {

        @Override
        public void valueChange(HasValue.ValueChangeEvent event) {
            if (questTable.getValue() != null) {
                LoadContent();
            }
        }
    }

    private class FilterChangeListener implements HasValue.ValueChangeListener {

        @Override
        public void valueChange(HasValue.ValueChangeEvent event) {
            LoadFilters();
            LoadContent();
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
            this.setSpacing(false);
            this.setMargin(false);
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

            if (translatedText_.getText() != null) {
                translation.setValue(translatedText_.getText());
            }

            translation.addValueChangeListener(new HasValue.ValueChangeListener<String>() {

                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> event) {
                    save.setVisible(true);

                    if (event.getValue() == null || event.getValue().isEmpty()) {
                        save.setCaption("Удалить");
                        save.setIcon(FontAwesome.RECYCLE);
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
            if ((SpringSecurityHelper.hasRole("ROLE_PREAPPROVE")) && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.EDITED)) {
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

            if ((SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.NEW || translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED || translatedText.getStatus() == TRANSLATE_STATUS.EDITED)) {
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

            if ((SpringSecurityHelper.hasRole("ROLE_APPROVE")) && translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED) || (translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED) || (translatedText.getStatus() == TRANSLATE_STATUS.EDITED))) {
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
            if ((SpringSecurityHelper.hasRole("ROLE_PREAPPROVE") || SpringSecurityHelper.hasRole("ROLE_APPROVE") || SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) && translatedText.getId() != null && ((translatedText.getStatus() == TRANSLATE_STATUS.NEW) || (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED) || (translatedText.getStatus() == TRANSLATE_STATUS.CORRECTED) || (translatedText.getStatus() == TRANSLATE_STATUS.EDITED))) {
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
