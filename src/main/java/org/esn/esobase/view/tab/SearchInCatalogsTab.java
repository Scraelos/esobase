/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;
import java.util.List;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.DictionaryService;
import org.esn.esobase.model.BookText;
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
import org.esn.esobase.model.GSpreadSheetsQuestEndTip;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.esn.esobase.model.GSpreadSheetsQuestStartTip;
import org.esn.esobase.model.TesDictionary;
import org.esn.esobase.model.TranslatedEntity;

/**
 *
 * @author scraelos
 */
public class SearchInCatalogsTab extends VerticalLayout {

    private final DBService service;
    private final DictionaryService dictionaryService;

    private final TextField searchField;
    private final Button searchButton;
    private final Table resultTable;
    private HierarchicalContainer hc = new HierarchicalContainer();

    public SearchInCatalogsTab(DBService service_, DictionaryService dictionaryService_) {
        this.dictionaryService = dictionaryService_;
        this.service = service_;
        this.setSizeFull();
        GridLayout hl = new GridLayout(2, 1);
        hl.setHeight(100, Unit.PIXELS);
        searchField = new TextField();
        searchField.setWidth(500, Unit.PIXELS);
        searchField.addShortcutListener(new ShortcutListener("Search shortcut", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                search();
            }
        });
        hl.addComponent(searchField, 0, 0);
        searchButton = new Button("Поиск");
        searchButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                search();

            }
        });
        hl.addComponent(searchButton, 1, 0);
        this.addComponent(hl);
        resultTable = new Table("");
        resultTable.setSizeFull();

        Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-table-cell-wrapper {\n"
                + "    /* Do not specify any margins, paddings or borders here */\n"
                + "    white-space: normal;\n"
                + "    overflow: hidden;\n"
                + "}");
        resultTable.addStyleName("v-table-cell-wrapper");
        resultTable.setPageLength(0);
        hc.addContainerProperty("textEn", String.class, null);
        hc.addContainerProperty("textRu", String.class, null);
        hc.addContainerProperty("catalogType", String.class, null);
        hc.addContainerProperty("translator", String.class, null);
        hc.addContainerProperty("weight", Integer.class, null);
        resultTable.setContainerDataSource(hc);
        resultTable.setVisibleColumns(new Object[]{"textEn", "textRu", "catalogType", "translator"});
        resultTable.setColumnHeaders(new String[]{"Текст", "Русский текст", "Тип", "Переводчик"});
        resultTable.setColumnExpandRatio("textEn", 4f);
        resultTable.setColumnExpandRatio("textRu", 4f);
        resultTable.setColumnExpandRatio("catalogType", 1f);
        resultTable.setColumnExpandRatio("translator", 1f);

        this.addComponent(resultTable);
        this.setExpandRatio(hl, 5);
        this.setExpandRatio(resultTable, 95);

    }

    /*private void search() {
        if (searchField.getValue() != null && searchField.getValue().length() > 2) {
            hc = service.searchInCatalogs(searchField.getValue(), hc);
        }
    }*/
    private void search() {
        hc.removeAllItems();
        if (searchField.getValue() != null && searchField.getValue().length() > 2) {
            List<TranslatedEntity> result = dictionaryService.search("%"+searchField.getValue()+"%");
            for (TranslatedEntity e : result) {
                Item item = hc.addItem(e);
                item.getItemProperty("textEn").setValue(e.getTextEn());
                item.getItemProperty("textRu").setValue(e.getTextRu());
                String catalogType = null;
                if (e instanceof GSpreadSheetEntity) {
                    item.getItemProperty("translator").setValue(((GSpreadSheetEntity) e).getTranslator());
                }
                if (e instanceof GSpreadSheetsAbilityDescription) {
                    catalogType = "Описание способности";
                } else if (e instanceof GSpreadSheetsAchievement) {
                    catalogType = "Достижение";
                } else if (e instanceof GSpreadSheetsAchievementDescription) {
                    catalogType = "Описание достижения";
                } else if (e instanceof GSpreadSheetsActivator) {
                    catalogType = "Активатор";
                } else if (e instanceof GSpreadSheetsCollectible) {
                    catalogType = "Коллекционный предмет";
                } else if (e instanceof GSpreadSheetsCollectibleDescription) {
                    catalogType = "Описание коллекционного предмета";
                } else if (e instanceof GSpreadSheetsItemDescription) {
                    catalogType = "Описание предмета";
                } else if (e instanceof GSpreadSheetsItemName) {
                    catalogType = "Название предмета";
                } else if (e instanceof GSpreadSheetsJournalEntry) {
                    catalogType = "Запись журнала";
                } else if (e instanceof GSpreadSheetsLoadscreen) {
                    catalogType = "Загрузочный экран";
                } else if (e instanceof GSpreadSheetsLocationName) {
                    catalogType = "Локация";
                } else if (e instanceof GSpreadSheetsNote) {
                    catalogType = "Письмо";
                } else if (e instanceof GSpreadSheetsNpcName) {
                    catalogType = "NPC";
                    item.getItemProperty("textEn").setValue(((GSpreadSheetsNpcName) e).getTextEn() + "(" + ((GSpreadSheetsNpcName) e).getSex().toString().substring(0, 1) + ")");
                } else if (e instanceof GSpreadSheetsNpcPhrase) {
                    catalogType = "Фраза NPC";
                } else if (e instanceof GSpreadSheetsPlayerPhrase) {
                    catalogType = "Фраза игрока";
                } else if (e instanceof GSpreadSheetsQuestDescription) {
                    catalogType = "Описание квеста";
                } else if (e instanceof GSpreadSheetsQuestDirection) {
                    catalogType = "Цель квеста";
                } else if (e instanceof GSpreadSheetsQuestEndTip) {
                    catalogType = "Завершённая цепочка";
                } else if (e instanceof GSpreadSheetsQuestName) {
                    catalogType = "Название квеста";
                } else if (e instanceof GSpreadSheetsQuestStartTip) {
                    catalogType = "Начатая цепочка";
                } else if (e instanceof BookText) {
                    catalogType = "Текст книги";
                } else if (e instanceof TesDictionary) {
                    catalogType = ((TesDictionary) e).getDescription();
                    item.getItemProperty("translator").setValue(((TesDictionary) e).getGame());
                }
                item.getItemProperty("catalogType").setValue(catalogType);
            }
        }
    }

    public void setWidth() {
        resultTable.setWidth(this.getUI().getWidth() - 5f, this.getUI().getWidthUnits());
    }

}
