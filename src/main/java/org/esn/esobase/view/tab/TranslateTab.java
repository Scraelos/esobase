/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.FieldEvents;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.List;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.Greeting;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.Npc;
import org.esn.esobase.model.Quest;
import org.esn.esobase.model.Subtitle;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.Topic;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.security.SpringSecurityHelper;

/**
 *
 * @author scraelos
 */
public class TranslateTab extends VerticalLayout {

    private final DBService service;
    private HorizontalLayout actionsLayout;
    private VerticalLayout contentLayout;
    private GridLayout npcListlayout;
    private VerticalLayout npcContentLayout;
    private ComboBox locationTable;
    private ComboBox questTable;
    private ComboBox npcTable;
    private BeanItemContainer<Location> locationContainer;
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
    private TabSheet.Tab npcGreetingsTab;
    private Table npcTopicsTable;
    private Table npcSubtitlesTable;
    private Table npcGreetingsTable;
    private BeanItemContainer<Topic> topicsContainer;
    private BeanItemContainer<Greeting> greetingsContainer;
    private BeanItemContainer<Subtitle> subtitlesContainer;
    private Npc currentNpc;
    private CheckBox onlyWithTranslations;

    public TranslateTab(DBService service) {
        TopicNpcColumnGenerator topicNpcColumnGenerator = new TopicNpcColumnGenerator();
        TopicPlayerColumnGenerator topicPlayerColumnGenerator = new TopicPlayerColumnGenerator();
        GreetingColumnGenerator greetingColumnGenerator = new GreetingColumnGenerator();
        SubtitleColumnGenerator subtitleColumnGenerator = new SubtitleColumnGenerator();
        TranslationColumnGenerator translationColumnGenerator = new TranslationColumnGenerator();
        FilterChangeListener filterChangeListener = new FilterChangeListener();
        this.setWidth(100f, Unit.PERCENTAGE);
        this.service = service;
        actionsLayout = new HorizontalLayout();
        contentLayout = new VerticalLayout();
        contentLayout.setWidth(100f, Unit.PERCENTAGE);
        npcListlayout = new GridLayout(3, 2);
        npcListlayout.setWidth(100f, Unit.PERCENTAGE);
        npcTable = new ComboBox("NPC");
        npcTable.addStyleName(ValoTheme.TABLE_COMPACT);
        npcTable.setWidth(100f, Unit.PERCENTAGE);
        npcTable.addValueChangeListener(new NpcSelectListener());
        npcTable.setFilteringMode(FilteringMode.CONTAINS);
        locationContainer = new BeanItemContainer<>(Location.class);
        locationTable = new ComboBox("Локация");
        locationTable.setWidth(100f, Unit.PERCENTAGE);
        locationTable.addValueChangeListener(filterChangeListener);
        locationTable.setContainerDataSource(locationContainer);
        locationTable.setFilteringMode(FilteringMode.CONTAINS);
        questContainer = new BeanItemContainer<>(Quest.class);
        questTable = new ComboBox("Квест");
        questTable.setWidth(100f, Unit.PERCENTAGE);
        questTable.addValueChangeListener(filterChangeListener);
        questTable.setContainerDataSource(questContainer);
        questTable.setFilteringMode(FilteringMode.CONTAINS);

        npcContainer = new BeanItemContainer<>(Npc.class);
        npcTable.setContainerDataSource(npcContainer);
        npcContainer.addNestedContainerProperty("location.name");
        npcContainer.addNestedContainerProperty("location.nameRu");

        npcListlayout.addComponent(locationTable, 0, 0);
        npcListlayout.addComponent(questTable, 1, 0);
        npcListlayout.addComponent(npcTable, 0, 1);
        onlyWithTranslations = new CheckBox("С новыми переводами");
        onlyWithTranslations.setValue(Boolean.FALSE);
        onlyWithTranslations.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                LoadFilters();
                LoadNpcContent();
            }
        });
        npcListlayout.addComponent(onlyWithTranslations, 2, 0);
        contentLayout.addComponent(npcListlayout);
        contentLayout.setExpandRatio(npcListlayout, 0.2f);
        npcContentLayout = new VerticalLayout();
        npcContentLayout.setWidth(100, Unit.PERCENTAGE);
        npcTabSheet = new TabSheet();
        npcTabSheet.setWidth(100f, Unit.PERCENTAGE);
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
        npcTopicsTable.setWidth(100f, Unit.PERCENTAGE);
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
        npcTopicsTable.setColumnWidth("actions", 150);
        npcSubtitlesTable = new Table();
        npcSubtitlesTable.addStyleName(ValoTheme.TABLE_COMPACT);
        npcSubtitlesTable.setWidth(100f, Unit.PERCENTAGE);
        npcSubtitlesTable.setPageLength(0);
        subtitlesContainer = new BeanItemContainer<>(Subtitle.class);
        npcSubtitlesTable.setContainerDataSource(subtitlesContainer);
        npcSubtitlesTable.addGeneratedColumn("textG", subtitleColumnGenerator);
        npcSubtitlesTable.removeGeneratedColumn("translations");
        npcSubtitlesTable.addGeneratedColumn("translations", translationColumnGenerator);
        npcSubtitlesTable.setVisibleColumns(new Object[]{"textG", "translations"});
        npcSubtitlesTable.setColumnHeaders(new String[]{"Реплика", "Перевод"});
        npcSubtitlesTable.setColumnWidth("actions", 150);
        npcGreetingsTable = new Table();
        npcGreetingsTable.addStyleName(ValoTheme.TABLE_COMPACT);
        npcGreetingsTable.setWidth(100f, Unit.PERCENTAGE);
        npcGreetingsTable.setPageLength(0);
        greetingsContainer = new BeanItemContainer<>(Greeting.class);
        npcGreetingsTable.setContainerDataSource(greetingsContainer);
        npcGreetingsTable.addGeneratedColumn("textG", greetingColumnGenerator);
        npcGreetingsTable.removeGeneratedColumn("translations");
        npcGreetingsTable.addGeneratedColumn("translations", translationColumnGenerator);
        npcGreetingsTable.setVisibleColumns(new Object[]{"textG", "translations"});
        npcGreetingsTable.setColumnHeaders(new String[]{"Реплика", "Перевод"});
        npcGreetingsTable.setColumnWidth("actions", 150);
        npcTopicsTab = npcTabSheet.addTab(npcTopicsTable, "Диалоги");
        npcSubtitlesTab = npcTabSheet.addTab(npcSubtitlesTable, "Субтитры");
        npcGreetingsTab = npcTabSheet.addTab(npcGreetingsTable, "Приветствие");
        npcContentLayout.addComponent(npcTabSheet);
        contentLayout.addComponent(npcContentLayout);
        contentLayout.setExpandRatio(npcContentLayout, 0.75f);
        this.addComponent(actionsLayout);
        this.addComponent(contentLayout);
        LoadFilters();
    }

    private void LoadFilters() {
        npcContainer = service.getNpcs(npcContainer, onlyWithTranslations.getValue());
        npcContainer.sort(new Object[]{"name"}, new boolean[]{true});
        List<Location> locations = new ArrayList<>();
        for (Npc npc : npcContainer.getItemIds()) {
            locations.add(npc.getLocation());
        }
        locationContainer.removeAllItems();
        locationContainer.addAll(locations);
        locationContainer.sort(new Object[]{"name"}, new boolean[]{true});
        questContainer = service.loadBeanItems(questContainer);
        questContainer.sort(new Object[]{"name"}, new boolean[]{true});

        
    }

    private void LoadNpcContent() {
        if (currentNpc != null) {
            locationName.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "location.name"));
            locationNameRu.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "location.nameRu"));
            npcName.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "name"));
            npcNameRu.setPropertyDataSource(npcContainer.getContainerProperty(currentNpc, "nameRu"));
            topicsContainer = service.getNpcTopics(currentNpc, topicsContainer, onlyWithTranslations.getValue());
            topicsContainer.sort(new Object[]{"id"}, new boolean[]{true});
            greetingsContainer = service.getNpcGreetings(currentNpc, greetingsContainer, onlyWithTranslations.getValue());
            greetingsContainer.sort(new Object[]{"id"}, new boolean[]{true});
            subtitlesContainer = service.getNpcSubtitles(currentNpc, subtitlesContainer, onlyWithTranslations.getValue());
            subtitlesContainer.sort(new Object[]{"id"}, new boolean[]{true});
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
            } else if (itemId instanceof Greeting) {
                service.assignGreetingToPhrase((Greeting) itemId);
            }
            LoadNpcContent();
        }

    }

    private class TopicNpcColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {

            VerticalLayout result = new VerticalLayout();
            Topic topic = (Topic) itemId;
            if (topic.getNpcText() != null && !topic.getNpcText().isEmpty()) {
                TextArea textEnArea = new TextArea("Текст в игре");
                textEnArea.setValue(topic.getNpcText());
                textEnArea.setReadOnly(true);
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                textEnArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnArea.setNullRepresentation("");
                result.addComponent(textEnArea);// "Текст в игре" 
            }
            if (topic.getNpcTextRu() != null && !topic.getNpcTextRu().isEmpty()) {
                TextArea textRuArea = new TextArea("Перевод в игре");
                textRuArea.setValue(topic.getNpcTextRu());
                textRuArea.setReadOnly(true);
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                textRuArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textRuArea.setNullRepresentation("");
                result.addComponent(textRuArea);//"Перевод в игре" 
            }
            if (topic.getExtNpcPhrase() != null) {
                TextArea textEnRawArea = new TextArea("Текст в таблицах");
                textEnRawArea.setValue(topic.getExtNpcPhrase().getTextEn());
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                textEnRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnRawArea.setNullRepresentation("");
                result.addComponent(textEnRawArea);//"Текст в таблицах"
                if (topic.getExtNpcPhrase().getTextRu() != null && !topic.getExtNpcPhrase().getTextRu().equals(topic.getExtNpcPhrase().getTextEn())) {
                    TextArea textRuRawArea = new TextArea("Перевод в таблицах от "+topic.getExtNpcPhrase().getTranslator());
                    textRuRawArea.setValue(topic.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setReadOnly(true);
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    textRuRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                    textRuRawArea.setNullRepresentation("");
                    result.addComponent(textRuRawArea);//"Перевод в таблицах"
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
                TextArea textEnArea = new TextArea("Текст в игре");
                textEnArea.setValue(topic.getPlayerText());
                textEnArea.setReadOnly(true);
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                textEnArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnArea.setNullRepresentation("");
                result.addComponent(textEnArea);//, "Текст в игре" 
            }
            if (topic.getPlayerTextRu() != null && !topic.getPlayerTextRu().isEmpty()) {
                TextArea textRuArea = new TextArea("Перевод в игре");
                textRuArea.setValue(topic.getPlayerTextRu());
                textRuArea.setReadOnly(true);
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                textRuArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textRuArea.setNullRepresentation("");
                result.addComponent(textRuArea);//, "Перевод в игре"
            }
            if (topic.getExtPlayerPhrase() != null) {
                TextArea textEnRawArea = new TextArea("Текст в таблицах");
                textEnRawArea.setValue(topic.getExtPlayerPhrase().getTextEn());
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                textEnRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnRawArea.setNullRepresentation("");
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (topic.getExtPlayerPhrase().getTextRu() != null && !topic.getExtPlayerPhrase().getTextRu().equals(topic.getExtPlayerPhrase().getTextEn())) {
                    TextArea textRuRawArea = new TextArea("Перевод в таблицах от "+topic.getExtPlayerPhrase().getTranslator());
                    textRuRawArea.setValue(topic.getExtPlayerPhrase().getTextRu());
                    textRuRawArea.setReadOnly(true);
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    textRuRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                    textRuRawArea.setNullRepresentation("");
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

    private class GreetingColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout result = new VerticalLayout();
            Greeting topic = (Greeting) itemId;
            if (topic.getText() != null && !topic.getText().isEmpty()) {
                TextArea textEnArea = new TextArea("Текст в игре");
                textEnArea.setValue(topic.getText());
                textEnArea.setReadOnly(true);
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                textEnArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnArea.setNullRepresentation("");
                result.addComponent(textEnArea);//, "Текст в игре"
            }
            if (topic.getTextRu() != null && !topic.getTextRu().isEmpty()) {
                TextArea textRuArea = new TextArea("Перевод в игре");
                textRuArea.setValue(topic.getTextRu());
                textRuArea.setReadOnly(true);
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                textRuArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textRuArea.setNullRepresentation("");
                result.addComponent(textRuArea);//, "Перевод в игре"
            }
            if (topic.getExtNpcPhrase() != null) {
                TextArea textEnRawArea = new TextArea("Текст в таблицах");
                textEnRawArea.setValue(topic.getExtNpcPhrase().getTextEn());
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                textEnRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnRawArea.setNullRepresentation("");
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (topic.getExtNpcPhrase().getTextRu() != null && !topic.getExtNpcPhrase().getTextRu().equals(topic.getExtNpcPhrase().getTextEn())) {
                    TextArea textRuRawArea = new TextArea("Перевод в таблицах от "+topic.getExtNpcPhrase().getTranslator());
                    textRuRawArea.setValue(topic.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setReadOnly(true);
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    textRuRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                    textRuRawArea.setNullRepresentation("");
                    result.addComponent(textRuRawArea);//, "Перевод в таблицах"
                }
            } else if (topic.getText() != null && !topic.getText().isEmpty()) {
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
            Subtitle subtitle = (Subtitle) itemId;
            if (subtitle.getText() != null && !subtitle.getText().isEmpty()) {
                TextArea textEnArea = new TextArea("Текст в игре");
                textEnArea.setValue(subtitle.getText());
                textEnArea.setReadOnly(true);
                textEnArea.setWidth(100f, Unit.PERCENTAGE);
                textEnArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnArea.setNullRepresentation("");
                result.addComponent(textEnArea);//, "Текст в игре"
            }
            if (subtitle.getTextRu() != null && !subtitle.getTextRu().isEmpty()) {
                TextArea textRuArea = new TextArea("Перевод в игре");
                textRuArea.setValue(subtitle.getTextRu());
                textRuArea.setReadOnly(true);
                textRuArea.setWidth(100f, Unit.PERCENTAGE);
                textRuArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textRuArea.setNullRepresentation("");
                result.addComponent(textRuArea);//, "Перевод в игре"
            }

            if (subtitle.getExtNpcPhrase() != null) {
                TextArea textEnRawArea = new TextArea("Текст в таблицах");
                textEnRawArea.setValue(subtitle.getExtNpcPhrase().getTextEn());
                textEnRawArea.setReadOnly(true);
                textEnRawArea.setWidth(100f, Unit.PERCENTAGE);
                textEnRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                textEnRawArea.setNullRepresentation("");
                result.addComponent(textEnRawArea);//, "Текст в таблицах"
                if (subtitle.getExtNpcPhrase().getTextRu() != null && !subtitle.getExtNpcPhrase().getTextRu().equals(subtitle.getExtNpcPhrase().getTextEn())) {
                    TextArea textRuRawArea = new TextArea("Перевод в таблицах от "+subtitle.getExtNpcPhrase().getTranslator());
                    textRuRawArea.setValue(subtitle.getExtNpcPhrase().getTextRu());
                    textRuRawArea.setReadOnly(true);
                    textRuRawArea.setWidth(100f, Unit.PERCENTAGE);
                    textRuRawArea.addStyleName(ValoTheme.TEXTAREA_TINY);
                    textRuRawArea.setNullRepresentation("");
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

            npcContainer.removeAllContainerFilters();
            if (locationTable.getValue() != null) {
                npcContainer.addContainerFilter(new Compare.Equal("location", locationTable.getValue()));
            }
            if (questTable.getValue() != null) {
                List<Filter> equals = new ArrayList<>();
                for (Npc npc : ((Quest) questTable.getValue()).getNpcs()) {
                    equals.add(new Compare.Equal("id", npc.getId()));
                }
                Filter[] equalsArray = new Filter[equals.size()];
                for (int i = 0; i < equals.size(); i++) {
                    equalsArray[i] = equals.get(i);
                }
                Or orFilter = new Or(equalsArray);
                npcContainer.addContainerFilter(orFilter);
            }

        }

    }

    private class TranslationColumnGenerator implements ColumnGenerator {

        @Override
        public Object generateCell(Table source, Object itemId, Object columnId) {
            final VerticalLayout vl = new VerticalLayout();
            vl.setSizeFull();
            List<TranslatedText> list = (List<TranslatedText>) source.getItem(itemId).getItemProperty(columnId).getValue();
            List<SysAccount> accounts = new ArrayList<>();
            if (list != null) {
                for (TranslatedText t : list) {
                    vl.addComponent(new TranslationCell(t));
                    accounts.add(t.getAuthor());
                }
            }

            String text=null;
            if (itemId instanceof Greeting) {
                    text=((Greeting) itemId).getText();
                } else if (itemId instanceof Subtitle) {
                    text=((Subtitle) itemId).getText();
                } else if (itemId instanceof Topic) {
                    if (columnId.equals("playerTranslations")) {
                        text=((Topic) itemId).getPlayerText();
                    } else if (columnId.equals("npcTranslations")) {
                        text=((Topic) itemId).getNpcText();
                    }
                }
            if (!accounts.contains(SpringSecurityHelper.getSysAccount())&&text!=null&&!text.isEmpty()) {
                final TranslatedText translatedText = new TranslatedText();
                translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
                if (itemId instanceof Greeting) {
                    translatedText.setGreeting((Greeting) itemId);
                } else if (itemId instanceof Subtitle) {
                    translatedText.setSubtitle((Subtitle) itemId);
                } else if (itemId instanceof Topic) {
                    if (columnId.equals("playerTranslations")) {
                        translatedText.setPlayerTopic((Topic) itemId);
                    } else if (columnId.equals("npcTranslations")) {
                        translatedText.setNpcTopic((Topic) itemId);
                    }
                }
                Button addTranslation = new Button("Добавить перевод");
                addTranslation.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {

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
        private Button reject;
        private final TranslatedText translatedText;

        public TranslationCell(TranslatedText translatedText_) {
            this.setSizeFull();
            this.translatedText = translatedText_;
            String translatedStatus = "нет";
            if (translatedText.getStatus() != null) {
                translatedStatus = translatedText.getStatus().toString();
            }
            translation = new TextArea("Статус перевода: " + translatedStatus + ", автор: " + translatedText_.getAuthor().getLogin());
            translation.setSizeFull();
            translation.addStyleName(ValoTheme.TEXTAREA_TINY);
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
                    } else {
                        translatedText.setText(event.getText());
                        service.saveTranslatedTextDirty(translatedText);
                        save.setCaption("Сохранить");
                    }
                    String status = "нет";
                    if (translatedText.getStatus() != null) {

                        status = translatedText.getStatus().toString();
                    }
                    translation.setCaption("Статус перевода: " + status + ", автор: " + translatedText.getAuthor().getLogin());
                }
            });
            if (SpringSecurityHelper.getSysAccount().equals(translatedText_.getAuthor())) {
                translation.setReadOnly(false);
            } else if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_APPROVE")) {
                translation.setReadOnly(false);
            } else {
                translation.setReadOnly(true);
            }
            this.addComponent(translation);
            save = new Button("Сохранить");
            save.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    translatedText.setText(translation.getValue());
                    service.saveTranslatedText(translatedText);
                    LoadNpcContent();
                }
            });

            this.addComponent(save);
            save.setVisible(false);
            if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_APPROVE")) {
                if (translatedText.getId() != null && translatedText.getStatus() == TRANSLATE_STATUS.NEW) {
                    accept = new Button("Принять эту версию");
                    accept.addClickListener(new Button.ClickListener() {

                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            translatedText.setText(translation.getValue());
                            service.acceptTranslatedText(translatedText);
                            service.calculateNpcProgress(currentNpc);
                            service.calculateQuestProgressByNpc(currentNpc);
                            LoadNpcContent();
                            LoadFilters();
                        }
                    });
                    this.addComponent(accept);
                    reject=new Button("Отклонить эту версию");
                    reject.addClickListener( new Button.ClickListener() {

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

            }
        }

    }

}
