/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.DictionaryService;
import org.esn.esobase.data.InsertExecutor;
import org.esn.esobase.data.SearchService;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.view.tab.BookTranslateTab;
import org.esn.esobase.view.tab.ChangePasswordTab;
import org.esn.esobase.view.tab.DirectTableEditTab;
import org.esn.esobase.view.tab.ImportTab;
import org.esn.esobase.view.tab.PortalInfoTab;
import org.esn.esobase.view.tab.QuestTranslateTab;
import org.esn.esobase.view.tab.QuestsTab;
import org.esn.esobase.view.tab.SearchInCatalogsTab;
import org.esn.esobase.view.tab.SearchInRawStringsTab;
import org.esn.esobase.view.tab.SpellerTestTab;
import org.esn.esobase.view.tab.SynchronizationTab;
import org.esn.esobase.view.tab.SystemSettingsTab;
import org.esn.esobase.view.tab.TranslateTab;
import org.esn.esobase.view.tab.UserStatisticsTab;
import org.esn.esobase.view.tab.UsersTab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * @author xpoft
 */
@Component
@Scope("prototype")
@SpringView(name = MainView.NAME)
public class MainView extends Panel implements View, Command {

    @Autowired
    private DBService service;
    @Autowired
    private SearchService searchService;
    @Autowired
    private DictionaryService dictionaryService;
    @Autowired
    private InsertExecutor insertExecutor;

    public static final String NAME = "";

    private final MenuBar mainMenu = new MenuBar();

    private final GridLayout headerLayout = new GridLayout(10, 2);
    private final Label loginLabel = new Label();
    private Link logoutLink = new Link();

    private TabSheet tabs = new TabSheet();

    private MenuBar.MenuItem importMenuItem;
    private MenuBar.MenuItem syncMenuItem;
    private MenuBar.MenuItem translateMenuItem;
    private MenuBar.MenuItem questTranslateMenuItem;
    private MenuBar.MenuItem usersMenuItem;
    private MenuBar.MenuItem changePasswordMenuItem;
    private MenuBar.MenuItem questsMenuItem;
    private MenuBar.MenuItem searchInCatalogsMenuItem;
    private MenuBar.MenuItem searchInRawStringsMenuItem;
    private MenuBar.MenuItem directTableEditMenuItem;
    private MenuBar.MenuItem portalInfoMenuItem;
    private MenuBar.MenuItem userStatisticsMenuItem;
    private MenuBar.MenuItem systemSettingsMenuItem;
    private MenuBar.MenuItem spellerTestMenuItem;
    private MenuBar.MenuItem bookTanslateMenuItem;

    @Autowired
    private ImportTab importTabContent;
    @Autowired
    private TranslateTab translateTabContent;
    private UsersTab usersTabContent;
    private SynchronizationTab synchronizationTabContent;
    private QuestsTab questsTabContent;
    private SearchInCatalogsTab searchInCatalogsTabContent;
    @Autowired
    private ChangePasswordTab changePasswordTabContent;
    private DirectTableEditTab directTableEditTabContent;
    private PortalInfoTab portalInfoTabContent;
    private SystemSettingsTab systemSettingsTabContent;
    private SearchInRawStringsTab searchInRawStringsTabContent;
    private SpellerTestTab spellerTestTabContent;
    private QuestTranslateTab questTranslateTabContent;
    private BookTranslateTab bookTranslateTabContent;
    @Autowired
    private UserStatisticsTab userStatisticsTabContent;

    //protected final ShortcutListener shiftTwoListener;
    //protected final ShortcutListener shiftThreeListener;
    public MainView() {
        /*this.shiftTwoListener = new ShortcutListener("Search in catalogs(shift+2)", KeyCode.NUM2, new int[]{ModifierKey.SHIFT}) {

         @Override
         public void handleAction(Object sender, Object target) {
         openSearchInCatalogs();
         }

         };
         this.shiftThreeListener = new ShortcutListener("Search in catalogs(shift+3)", KeyCode.NUM3, new int[]{ModifierKey.SHIFT}) {

         @Override
         public void handleAction(Object sender, Object target) {
         openSearchInRaw();
         }

         };*/
    }

    @PostConstruct
    public void PostConstruct() {
        setSizeFull();
        this.setHeight(100f, Unit.PERCENTAGE);
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-label {\n"
                + "    white-space: pre-line;\n"
                + "    overflow: hidden;\n"
                + "}"
                + ".v-caption-darkblue {\n"
                + "	font-weight:bold;margin-bottom:0.33em;margin-top:0.55em;\n"
                + "}"
                + ".my-grid .v-grid-body .v-grid-cell { height: 100px; }"
                + ".v-treegrid-cell.step_row {background-color: #d7e7d5 !important;}"
                + ".v-treegrid-cell.direction_row {background-color: #ffffff !important;}"
                + ".v-table, .v-table * { overflow-anchor: none; };");
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight(100f, Unit.PERCENTAGE);
        layout.setSpacing(false);
        layout.setMargin(true);
        layout.addComponent(headerLayout);
        buildHeader();
        layout.addComponent(mainMenu);
        buildMenu();
        layout.addComponent(tabs);
        layout.setExpandRatio(tabs, 40f);
        buildTabs();

        setContent(layout);

    }

    private void buildMenu() {
        mainMenu.setWidth(100f, Unit.PERCENTAGE);
        translateMenuItem = mainMenu.addItem("Перевод диалогов", this);
        questTranslateMenuItem = mainMenu.addItem("Перевод квестов", this);
        bookTanslateMenuItem = mainMenu.addItem("Перевод книг", this);
        directTableEditMenuItem = mainMenu.addItem("Таблицы", this);
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
            questsMenuItem = mainMenu.addItem("Квесты", this);
            importMenuItem = mainMenu.addItem("Импорт", this);
        }
        searchInCatalogsMenuItem = mainMenu.addItem("Поиск в справочниках", this);
        searchInRawStringsMenuItem = mainMenu.addItem("Поиск в разных переводах", this);
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN")) {
            syncMenuItem = mainMenu.addItem("Синхронизация", this);
        }
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_MANAGE_USERS")) {
            usersMenuItem = mainMenu.addItem("Пользователи", this);
        }
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN")) {
            systemSettingsMenuItem = mainMenu.addItem("Настройки", this);
        }
        if (SpringSecurityHelper.hasRole("ROLE_SPELL_CHECK")) {
            spellerTestMenuItem = mainMenu.addItem("Проверка орфографии", this);
        }
        changePasswordMenuItem = mainMenu.addItem("Сменить пароль", this);
        portalInfoMenuItem = mainMenu.addItem("Инфо", this);
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN")) {
            userStatisticsMenuItem = mainMenu.addItem("Статистика", this);
        }
        //this.addShortcutListener(shiftTwoListener);
        //this.addShortcutListener(shiftThreeListener);
    }

    private void buildHeader() {
        headerLayout.setMargin(false);
        headerLayout.setSpacing(false);
        headerLayout.setWidth(100f, Unit.PERCENTAGE);
        headerLayout.addComponent(loginLabel, 7, 0);
        ExternalResource resource = new ExternalResource("j_spring_security_logout");
        logoutLink.setResource(resource);
        logoutLink.setCaption("Выход");
        headerLayout.addComponent(logoutLink, 8, 0);
    }

    private void buildTabs() {
        tabs.setWidth(100f, Unit.PERCENTAGE);
        tabs.setHeight(100f, Unit.PERCENTAGE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loginLabel.setCaption(principal.getUsername());
    }

    private void openSearchInCatalogs() {
        if (searchInCatalogsTabContent == null) {
            searchInCatalogsTabContent = new SearchInCatalogsTab(service, dictionaryService);
        }
        Window subWindow = new Window(searchInCatalogsMenuItem.getText());
        subWindow.setModal(true);

        subWindow.center();
        this.getUI().addWindow(subWindow);
        subWindow.setResizable(false);
        subWindow.setSizeFull();
        subWindow.setContent(searchInCatalogsTabContent);
        searchInCatalogsTabContent.setWidth();
    }

    private void openSearchInRaw() {
        if (searchInRawStringsTabContent == null) {
            searchInRawStringsTabContent = new SearchInRawStringsTab(service);
        }
        Window subWindow = new Window(searchInRawStringsMenuItem.getText());
        subWindow.setModal(true);

        subWindow.center();
        this.getUI().addWindow(subWindow);
        subWindow.setResizable(false);
        subWindow.setSizeFull();
        subWindow.setContent(searchInRawStringsTabContent);
        searchInRawStringsTabContent.setWidth();
    }

    @Override
    public void menuSelected(MenuBar.MenuItem selectedItem) {
        if (selectedItem == importMenuItem) {
            TabSheet.Tab tab = tabs.getTab(importTabContent);
            if (tab == null) {
                importTabContent.Init();
                tab = tabs.addTab(importTabContent, selectedItem.getText());
            }
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == translateMenuItem) {
            TabSheet.Tab tab = tabs.getTab(translateTabContent);
            if (tab == null) {
                translateTabContent.Init();
                tab = tabs.addTab(translateTabContent, selectedItem.getText());
            }
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == questTranslateMenuItem) {
            if (questTranslateTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(questTranslateTabContent);
                if (tab == null) {
                    questTranslateTabContent = new QuestTranslateTab(service);
                }
            } else {
                questTranslateTabContent = new QuestTranslateTab(service);
            }
            TabSheet.Tab tab = tabs.addTab(questTranslateTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == bookTanslateMenuItem) {
            if (bookTranslateTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(bookTranslateTabContent);
                if (tab == null) {
                    bookTranslateTabContent = new BookTranslateTab(service);
                }
            } else {
                bookTranslateTabContent = new BookTranslateTab(service);
            }
            TabSheet.Tab tab = tabs.addTab(bookTranslateTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == directTableEditMenuItem) {
            if (directTableEditTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(directTableEditTabContent);
                if (tab == null) {
                    directTableEditTabContent = new DirectTableEditTab(service, searchService);
                }
            } else {
                directTableEditTabContent = new DirectTableEditTab(service, searchService);
            }
            TabSheet.Tab tab = tabs.addTab(directTableEditTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == questsMenuItem) {
            if (questsTabContent == null) {
                questsTabContent = new QuestsTab(service);
            }
            TabSheet.Tab tab = tabs.addTab(questsTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == usersMenuItem) {
            if (usersTabContent == null) {
                usersTabContent = new UsersTab(service);
            }
            TabSheet.Tab tab = tabs.addTab(usersTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == syncMenuItem) {
            if (synchronizationTabContent == null) {
                synchronizationTabContent = new SynchronizationTab(service);
            }
            TabSheet.Tab tab = tabs.addTab(synchronizationTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == searchInCatalogsMenuItem) {
            openSearchInCatalogs();
        } else if (selectedItem == searchInRawStringsMenuItem) {
            openSearchInRaw();
        } else if (selectedItem == changePasswordMenuItem) {
            TabSheet.Tab tab = tabs.getTab(changePasswordTabContent);
            if (tab == null) {
                changePasswordTabContent.Init();
                tab = tabs.addTab(changePasswordTabContent, selectedItem.getText());
            }
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == portalInfoMenuItem) {
            if (portalInfoTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(portalInfoTabContent);
                if (tab != null) {
                    tabs.removeTab(tabs.getTab(portalInfoTabContent));
                }
            }
            portalInfoTabContent = new PortalInfoTab(service);
            TabSheet.Tab tab = tabs.addTab(portalInfoTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == systemSettingsMenuItem) {
            if (systemSettingsTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(systemSettingsTabContent);
                if (tab != null) {
                    tabs.removeTab(tabs.getTab(systemSettingsTabContent));
                }
            }
            systemSettingsTabContent = new SystemSettingsTab(service);
            TabSheet.Tab tab = tabs.addTab(systemSettingsTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == spellerTestMenuItem) {
            if (spellerTestTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(spellerTestTabContent);
                if (tab != null) {
                    tabs.removeTab(tabs.getTab(spellerTestTabContent));
                }
            }
            spellerTestTabContent = new SpellerTestTab(service);
            TabSheet.Tab tab = tabs.addTab(spellerTestTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == userStatisticsMenuItem) {
            TabSheet.Tab tab = tabs.getTab(userStatisticsTabContent);
            if (tab == null) {
                tab = tabs.addTab(userStatisticsTabContent, selectedItem.getText());
                userStatisticsTabContent.Init();
                tab.setClosable(true);
            }
            tabs.setSelectedTab(tab);
        }

    }
}
