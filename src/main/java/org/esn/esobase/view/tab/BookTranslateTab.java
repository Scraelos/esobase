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
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.specification.BookSpecification;
import org.esn.esobase.model.Book;
import org.esn.esobase.model.Location;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.TRANSLATE_STATUS;
import org.esn.esobase.model.TranslatedText;
import org.esn.esobase.security.SpringSecurityHelper;

/**
 *
 * @author scraelos
 */
public class BookTranslateTab extends VerticalLayout {

    private final DBService service;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private HorizontalLayout bookListlayout;
    private TabSheet bookContentLayout;
    private ComboBox locationTable;
    private ComboBox subLocationTable;
    private ComboBox bookTable;
    private BeanItemContainer<Location> locationContainer = new BeanItemContainer<>(Location.class);
    private BeanItemContainer<Location> subLocationContainer = new BeanItemContainer<>(Location.class);
    private BeanItemContainer<Book> bookContainer = new BeanItemContainer<>(Book.class);
    private BookSpecification bookSpecification = new BookSpecification();

    private HorizontalLayout bookNameLayout;
    private VerticalLayout bookNameOrigLayout;
    private VerticalLayout bookNameTranslationsLayout;
    private TextField bookNameEn;
    private TextField bookNameRu;
    private HorizontalLayout bookTextLayout;
    private HorizontalLayout bookTextOrigLayout;
    private VerticalLayout bookTextTranslationsLayout;
    private TextArea bookTextEn;
    private TextArea bookTextRu;

    private Book currentBook;

    public BookTranslateTab(DBService service) {
        this.service = service;
        this.setSizeFull();
        FilterChangeListener filterChangeListener = new FilterChangeListener();
        bookListlayout = new HorizontalLayout();
        bookListlayout = new HorizontalLayout();
        bookListlayout.setSizeFull();
        bookTable = new ComboBox("Книга");
        bookTable.setPageLength(20);
        bookTable.addValueChangeListener(new BookSelectListener());

        bookTable.setWidth(100f, Unit.PERCENTAGE);
        bookTable.setFilteringMode(FilteringMode.CONTAINS);
        locationContainer = new BeanItemContainer<>(Location.class);
        locationTable = new ComboBox("Локация");
        locationTable.setPageLength(15);

        locationTable.setWidth(100f, Unit.PERCENTAGE);
        locationTable.setContainerDataSource(locationContainer);
        locationTable.setFilteringMode(FilteringMode.CONTAINS);
        locationTable.addValueChangeListener(filterChangeListener);

        subLocationContainer = new BeanItemContainer<>(Location.class);
        subLocationTable = new ComboBox("Сублокация");
        subLocationTable.setPageLength(15);
        subLocationTable.addValueChangeListener(filterChangeListener);

        subLocationTable.setWidth(100f, Unit.PERCENTAGE);
        subLocationTable.setContainerDataSource(subLocationContainer);
        subLocationTable.setFilteringMode(FilteringMode.CONTAINS);

        bookTable.setContainerDataSource(bookContainer);

        FormLayout locationAndNpc = new FormLayout(locationTable, subLocationTable, bookTable);
        locationAndNpc.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        locationAndNpc.setSizeFull();

        bookListlayout.addComponent(locationAndNpc);

        bookContentLayout = new TabSheet();
        bookContentLayout.setSizeFull();
        bookNameLayout = new HorizontalLayout();
        bookNameLayout.setSizeFull();
        bookNameOrigLayout = new VerticalLayout();
        bookNameEn = new TextField("Название");
        bookNameEn.setWidth(500f, Unit.PIXELS);
        bookNameRu = new TextField("Перевод названия");
        bookNameRu.setWidth(500f, Unit.PIXELS);
        bookNameRu.setNullRepresentation("");
        bookNameOrigLayout.addComponent(bookNameEn);
        bookNameOrigLayout.addComponent(bookNameRu);
        bookNameLayout.addComponent(bookNameOrigLayout);
        bookNameTranslationsLayout = new VerticalLayout();
        bookNameTranslationsLayout.setSizeFull();
        bookNameLayout.addComponent(bookNameTranslationsLayout);
        bookContentLayout.addTab(bookNameLayout, "Название");

        bookTextLayout = new HorizontalLayout();
        bookTextLayout.setSizeFull();
        bookTextOrigLayout = new HorizontalLayout();
        bookTextOrigLayout.setSizeFull();
        bookTextEn = new TextArea("Текст");
        bookTextEn.setRows(20);
        bookTextEn.setSizeFull();
        bookTextRu = new TextArea("Перевод текста");
        bookTextRu.setRows(20);
        bookTextRu.setSizeFull();
        bookTextRu.setNullRepresentation("");
        bookTextOrigLayout.addComponent(bookTextEn);
        bookTextOrigLayout.addComponent(bookTextRu);
        bookTextLayout.addComponent(bookTextOrigLayout);
        bookTextTranslationsLayout = new VerticalLayout();
        bookTextTranslationsLayout.setSizeFull();
        bookTextLayout.addComponent(bookTextTranslationsLayout);
        bookTextLayout.setExpandRatio(bookTextOrigLayout, 2f);
        bookTextLayout.setExpandRatio(bookTextTranslationsLayout, 1f);
        bookContentLayout.addTab(bookTextLayout, "Текст");
        bookNameEn.setReadOnly(true);
        bookNameRu.setReadOnly(true);
        bookTextEn.setReadOnly(true);
        bookTextRu.setReadOnly(true);
        this.addComponent(bookListlayout);
        this.addComponent(bookContentLayout);
        this.bookListlayout.setHeight(105f, Unit.PIXELS);
        this.setExpandRatio(bookContentLayout, 1f);
        LoadFilters();
    }

    private void LoadFilters() {
        bookContainer.removeAllItems();
        bookContainer.addAll(service.getBookRepository().findAll());
        bookContainer.sort(new Object[]{"nameEn"}, new boolean[]{true});
        List<Location> locations = new ArrayList<>();
        for (Book book : bookContainer.getItemIds()) {
            for (Location loc : book.getLocations()) {
                if (loc.getParentLocation() == null) {
                    locations.add(loc);
                }
            }

        }
        locationContainer.removeAllItems();
        locationContainer.addAll(locations);
        locationContainer.sort(new Object[]{"name"}, new boolean[]{true});
        List<Location> subLocations = new ArrayList<>();
        for (Book book : bookContainer.getItemIds()) {
            for (Location loc : book.getLocations()) {
                if (loc.getParentLocation() != null) {
                    subLocations.add(loc.getParentLocation());
                    subLocations.add(loc);
                }
            }

        }
        subLocationContainer.removeAllItems();
        subLocationContainer.addAll(subLocations);
        subLocationContainer.sort(new Object[]{"name"}, new boolean[]{true});

    }

    private void LoadBookContent() {
        if (currentBook != null) {
            currentBook = service.getBook(currentBook.getId());
            bookNameEn.setReadOnly(false);
            bookNameEn.setValue(currentBook.getNameEn());
            bookNameEn.setReadOnly(true);
            bookNameRu.setReadOnly(false);
            bookNameRu.setValue(currentBook.getNameRu());
            bookNameRu.setReadOnly(true);
            bookTextEn.setReadOnly(false);
            bookTextEn.setValue(currentBook.getBookText().getTextEn());
            bookTextEn.setReadOnly(true);
            bookTextRu.setReadOnly(false);
            bookTextRu.setValue(currentBook.getBookText().getTextRu());
            bookTextRu.setReadOnly(true);
            loadNameTranslations();
            loadTextTranslations();
        }
    }

    private void loadNameTranslations() {
        bookNameTranslationsLayout.removeAllComponents();
        List<SysAccount> accounts = new ArrayList<>();
        for (TranslatedText t : currentBook.getNameTranslations()) {
            NameTranslationCell tc = new NameTranslationCell(t);
            bookNameTranslationsLayout.addComponent(tc);
            accounts.add(t.getAuthor());
        }
        if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
            final TranslatedText translatedText = new TranslatedText();
            translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
            translatedText.setBookName(currentBook);
            Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
            addTranslation.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {

                    if (translatedText.getBookName() != null) {
                        translatedText.getBookName().getNameTranslations().add(translatedText);
                    }
                    NameTranslationCell tc = new NameTranslationCell(translatedText);
                    bookNameTranslationsLayout.addComponent(tc);
                    event.getButton().setVisible(false);
                }
            });
            bookNameTranslationsLayout.addComponent(addTranslation);
        }
    }

    private void loadTextTranslations() {
        bookTextTranslationsLayout.removeAllComponents();
        List<SysAccount> accounts = new ArrayList<>();
        for (TranslatedText t : currentBook.getBookText().getTranslations()) {
            TextTranslationCell tc = new TextTranslationCell(t);
            bookTextTranslationsLayout.addComponent(tc);
            bookTextTranslationsLayout.setExpandRatio(tc, 1f);
            accounts.add(t.getAuthor());
        }
        if (!accounts.contains(SpringSecurityHelper.getSysAccount()) && SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
            final TranslatedText translatedText = new TranslatedText();
            translatedText.setAuthor(SpringSecurityHelper.getSysAccount());
            translatedText.setBook(currentBook.getBookText());
            Button addTranslation = new Button("Добавить перевод", FontAwesome.PLUS_SQUARE);
            addTranslation.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {

                    if (translatedText.getBook() != null) {
                        translatedText.getBook().getTranslations().add(translatedText);
                    }
                    TextTranslationCell tc = new TextTranslationCell(translatedText);
                    bookTextTranslationsLayout.addComponent(tc);
                    bookTextTranslationsLayout.setExpandRatio(tc, 1f);
                    event.getButton().setVisible(false);
                }
            });
            bookTextTranslationsLayout.addComponent(addTranslation);
        }
    }

    private class BookSelectListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            currentBook = (Book) bookTable.getValue();
            LoadBookContent();
        }

    }

    private class FilterChangeListener implements Property.ValueChangeListener {

        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            if (locationTable.getValue() != null) {
                bookSpecification.setLocation((Location) locationTable.getValue());
            } else {
                bookSpecification.setLocation(null);
            }
            if (subLocationTable.getValue() != null) {
                bookSpecification.setSubLocation((Location) subLocationTable.getValue());
            } else {
                bookSpecification.setSubLocation(null);
            }
            if (locationTable.getValue() != null) {
                subLocationContainer.removeAllContainerFilters();
                subLocationContainer.addContainerFilter(new Or(
                        new Compare.Equal("parentLocation", locationTable.getValue()),
                        new Compare.Equal("id", ((Location) locationTable.getValue()).getId())
                )
                );
            }
            bookContainer.removeAllItems();
            bookContainer.addAll(service.getBookRepository().findAll(bookSpecification));
            bookContainer.sort(new Object[]{"nameEn"}, new boolean[]{true});
        }

    }

    private class NameTranslationCell extends VerticalLayout {

        private TextField translation;
        private Button save;
        private Button accept;
        private Button preAccept;
        private Button correct;
        private Button reject;
        private final TranslatedText translatedText;

        public NameTranslationCell(TranslatedText translatedText_) {
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
            translation = new TextField(caption.toString());
            translation.setWidth(300f, Unit.PIXELS);
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
                    LoadBookContent();
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
                        LoadBookContent();
                        //LoadFilters();
                    }
                });
                this.addComponent(preAccept);
            }
            if ((SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED||translatedText.getStatus() == TRANSLATE_STATUS.NEW)) {
                translation.setReadOnly(false);
                correct = new Button("Текст корректен", FontAwesome.PENCIL);
                correct.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.correctTranslatedText(translatedText);
                        LoadBookContent();
                        //LoadFilters();
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
                        LoadBookContent();
                        //LoadFilters();
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
                        LoadBookContent();
                        //LoadFilters();
                    }
                });
                this.addComponent(reject);
            }
            if (SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.REJECTED)) {
                translation.setReadOnly(false);
            }
        }

    }

    private class TextTranslationCell extends VerticalLayout {

        private TextArea translation;
        private Button save;
        private Button accept;
        private Button preAccept;
        private Button correct;
        private Button reject;
        private final TranslatedText translatedText;

        public TextTranslationCell(TranslatedText translatedText_) {
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
            translation.setRows(20);
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

                    LoadBookContent();
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
                        LoadBookContent();
                        //LoadFilters();
                    }
                });
                this.addComponent(preAccept);
            }
            if ((SpringSecurityHelper.hasRole("ROLE_CORRECTOR")) && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.PREACCEPTED||translatedText.getStatus() == TRANSLATE_STATUS.NEW)) {
                translation.setReadOnly(false);
                correct = new Button("Текст корректен", FontAwesome.PENCIL);
                correct.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        translatedText.setText(translation.getValue());
                        service.correctTranslatedText(translatedText);
                        LoadBookContent();
                        //LoadFilters();
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
                        LoadBookContent();
                        //LoadFilters();
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
                        LoadBookContent();
                        //LoadFilters();
                    }
                });
                this.addComponent(reject);
            }
            if (SpringSecurityHelper.hasRole("ROLE_APPROVE") && translatedText.getId() != null && (translatedText.getStatus() == TRANSLATE_STATUS.ACCEPTED || translatedText.getStatus() == TRANSLATE_STATUS.REJECTED)) {
                translation.setReadOnly(false);
            }
        }

    }

}
