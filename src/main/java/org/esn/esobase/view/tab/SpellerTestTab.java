/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.SPELLER_ERROR;
import org.esn.esobase.data.speller.CheckTextRequest;
import org.esn.esobase.data.speller.CheckTextResponse;
import org.esn.esobase.data.speller.SpellError;
import org.esn.esobase.data.speller.SpellService;
import org.esn.esobase.data.speller.SpellServiceSoap;

/**
 *
 * @author Scraelos
 */
public class SpellerTestTab extends VerticalLayout {

    private HorizontalLayout hl;
    private DateField startDate;
    private DateField endDate;
    private Button checkButton;
    private TreeTable resultTable;
    private final DBService service;
    private HierarchicalContainer hc;
    private static final Logger LOG = Logger.getLogger(SpellerTestTab.class.getName());

    public SpellerTestTab(DBService service_) {
        this.service = service_;
        this.setSizeFull();
        hl = new HorizontalLayout();
        startDate = new DateField();
        startDate.setResolution(Resolution.DAY);
        endDate = new DateField();
        endDate.setResolution(Resolution.DAY);
        hl.addComponent(startDate);
        hl.addComponent(endDate);

        checkButton = new Button("Проверить");
        checkButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Date dateEndValue = null;
                if (endDate.getValue() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endDate.getValue());
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                    dateEndValue = cal.getTime();
                }
                hc = service.getTextForSpellCheck(startDate.getValue(), dateEndValue, hc);
                try {
                    SpellService yservice = new SpellService();
                    SpellServiceSoap port = yservice.getSpellServiceSoap12();
                    List<Object> parentIds = new ArrayList<>();
                    for (Object itemId : hc.getItemIds()) {
                        parentIds.add(itemId);
                    }
                    for (Object itemId : parentIds) {
                        CheckTextRequest parameters = new CheckTextRequest();
                        parameters.setLang("ru");
                        parameters.setText((String) hc.getItem(itemId).getItemProperty("textRu").getValue());
                        parameters.setFormat("plain");
                        parameters.setOptions(0);
                        CheckTextResponse result = port.checkText(parameters);
                        if (result != null && result.getSpellResult() != null && result.getSpellResult().getError() != null) {
                            for (SpellError error : result.getSpellResult().getError()) {

                                SPELLER_ERROR e = SPELLER_ERROR.valueOf(error.getCode());
                                String word = error.getWord();
                                boolean existSpellerWord = service.isExistSpellerWord(word);
                                if (!((e == SPELLER_ERROR.ERROR_UNKNOWN_WORD) && (existSpellerWord))) {
                                    Item item = hc.addItem(error);

                                    item.getItemProperty("errorType").setValue(e);
                                    item.getItemProperty("word").setValue(error.getWord());
                                    item.getItemProperty("s").setValue(error.getS());
                                    hc.setParent(error, itemId);
                                    hc.setChildrenAllowed(error, false);
                                }

                            }
                        }

                        resultTable.setCollapsed(itemId, false);
                    }

                    excludeWithoutErrors();

                } catch (Exception ex) {
                    LOG.log(Level.INFO, null, ex);
                }

            }
        });
        hl.addComponent(checkButton);
        this.addComponent(hl);
        hc = new HierarchicalContainer();
        hc.addContainerProperty("textEn", String.class, null);
        hc.addContainerProperty("textRu", String.class, null);
        hc.addContainerProperty("catalogType", String.class, null);
        hc.addContainerProperty("translator", String.class, null);
        hc.addContainerProperty("errorType", SPELLER_ERROR.class, null);
        hc.addContainerProperty("word", String.class, null);
        hc.addContainerProperty("s", List.class, null);
        resultTable = new TreeTable();
        resultTable.setContainerDataSource(hc);
        resultTable.setWidth(100f, Unit.PERCENTAGE);

        resultTable.addGeneratedColumn("actionsColumn", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                if (itemId instanceof SpellError) {
                    Button b = new Button("Добавить в исключения");
                    b.addClickListener(new WordAddClickListener(service, (String) source.getItem(itemId).getItemProperty("word").getValue(), (SpellError) itemId));
                    return b;
                } else {
                    Button b = new Button("Сохранить");
                    b.addClickListener(new SaveItemListener(itemId));
                    return b;
                }

            }
        });
        resultTable.setVisibleColumns(new Object[]{"textEn", "textRu", "catalogType", "translator", "errorType", "word", "s", "actionsColumn"});
        resultTable.setColumnHeaders(new String[]{"Оригинал", "Перевод", "Тип таблицы", "Переводчик", "Тип ошибки", "Слово", "Варианты", ""});
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-table-cell-wrapper {\n"
                + "    /* Do not specify any margins, paddings or borders here */\n"
                + "    white-space: normal;\n"
                + "    /*overflow: hidden;*/\n"
                + "}");
        resultTable.addStyleName("v-table-cell-wrapper");
        resultTable.setColumnExpandRatio("textEn", 3f);
        resultTable.setColumnExpandRatio("textRu", 3f);
        resultTable.setColumnExpandRatio("catalogType", 1f);
        resultTable.setColumnExpandRatio("translator", 0.7f);
        resultTable.setColumnExpandRatio("errorType", 1f);
        resultTable.setColumnExpandRatio("word", 1f);
        resultTable.setColumnExpandRatio("s", 1f);
        resultTable.setColumnExpandRatio("actionsColumn", 1.2f);
        resultTable.setConverter("s", new Converter<String, List>() {

            @Override
            public List convertToModel(String value, Class<? extends List> targetType, Locale locale) throws Converter.ConversionException {
                if (value != null && !value.isEmpty()) {
                    String[] split = value.split("\n");
                    return new ArrayList(Arrays.asList(split));
                }
                return null;
            }

            @Override
            public String convertToPresentation(List value, Class<? extends String> targetType, Locale locale) throws Converter.ConversionException {
                StringBuilder sb = new StringBuilder("");
                if (value != null) {
                    for (int i = 0; i < value.size(); i++) {
                        sb.append(value.get(i));
                        if (i != value.size() - 1) {
                            sb.append("\n");
                        }
                    }
                }

                String joined = sb.toString();
                return joined;
            }

            @Override
            public Class<List> getModelType() {
                return List.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }

        });
        resultTable.setTableFieldFactory(new TableFieldFactory() {

            @Override
            public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                Field result = null;
                if (!(itemId instanceof SpellError) && propertyId.equals("textRu")) {
                    result = new TextArea();
                    result.setPropertyDataSource(container.getItem(itemId).getItemProperty(propertyId));
                    result.setSizeFull();
                }
                if (!(itemId instanceof SpellError) && propertyId.equals("textEn")) {
                    result = new TextArea();
                    result.setPropertyDataSource(container.getItem(itemId).getItemProperty(propertyId));
                    result.setReadOnly(true);
                    result.setSizeFull();
                }
                return result;
            }
        });
        resultTable.setEditable(true);
        this.addComponent(resultTable);
    }

    private void excludeWithWord(String word) {
        List<Object> l = new ArrayList<>();
        for (Object o : hc.getItemIds()) {
            if (!hc.isRoot(o) && hc.getItem(o).getItemProperty("word").getValue().equals(word)) {
                l.add(o);
            }
        }
        for (Object o : l) {
            hc.removeItem(o);
        }
    }

    private void excludeWithoutErrors() {
        List<Object> l = new ArrayList<>();
        for (Object o : hc.getItemIds()) {
            if (hc.isRoot(o) && !hc.hasChildren(o)) {
                l.add(o);
            }
        }
        for (Object o : l) {
            hc.removeItem(o);
        }
    }

    private class WordAddClickListener implements ClickListener {

        private final DBService service;
        private final String word;
        private final SpellError e;

        public WordAddClickListener(DBService service_, String word_, SpellError e_) {
            this.service = service_;
            this.word = word_;
            this.e = e_;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            service.addSpellerWord(word);
            excludeWithWord(word);
            excludeWithoutErrors();
        }

    }

    private class SaveItemListener implements ClickListener {

        private final Object entity;

        public SaveItemListener(Object entity_) {
            this.entity = entity_;
        }

        @Override
        public void buttonClick(Button.ClickEvent event) {
            service.commitTableEntityItem(entity, (String) hc.getContainerProperty(entity, "textRu").getValue());
            hc.removeItemRecursively(entity);
        }

    }

}
