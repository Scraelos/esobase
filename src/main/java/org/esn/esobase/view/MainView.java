/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.Command;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.xpoft.vaadin.VaadinView;

import javax.annotation.PostConstruct;
import org.esn.esobase.data.DBService;
import org.esn.esobase.security.SpringSecurityHelper;
import org.esn.esobase.view.tab.ChangePasswordTab;
import org.esn.esobase.view.tab.ImportTab;
import org.esn.esobase.view.tab.QuestsTab;
import org.esn.esobase.view.tab.SearchInCatalogsTab;
import org.esn.esobase.view.tab.SynchronizationTab;
import org.esn.esobase.view.tab.TranslateTab;
import org.esn.esobase.view.tab.UsersTab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * @author xpoft
 */
@Component
@Scope("prototype")
@VaadinView(MainView.NAME)
public class MainView extends Panel implements View, Command {

    @Autowired
    private DBService service;

    public static final String NAME = "";

    private final MenuBar mainMenu = new MenuBar();

    private final GridLayout headerLayout = new GridLayout(10, 2);
    private final Label loginLabel = new Label();
    private Link logoutLink = new Link();

    private TabSheet tabs = new TabSheet();

    private MenuBar.MenuItem importMenuItem;
    private MenuBar.MenuItem syncMenuItem;
    private MenuBar.MenuItem translateMenuItem;
    private MenuBar.MenuItem usersMenuItem;
    private MenuBar.MenuItem changePasswordMenuItem;
    private MenuBar.MenuItem questsMenuItem;
    private MenuBar.MenuItem searchInCatalogsMenuItem;

    private ImportTab importTabContent;
    private TranslateTab translateTabContent;
    private UsersTab usersTabContent;
    private SynchronizationTab synchronizationTabContent;
    private QuestsTab questsTabContent;
    private SearchInCatalogsTab searchInCatalogsTabContent;
    private ChangePasswordTab changePasswordTabContent;

    @PostConstruct
    public void PostConstruct() {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(headerLayout);
        buildHeader();
        layout.addComponent(mainMenu);
        buildMenu();
        layout.addComponent(tabs);
        buildTabs();

        setContent(layout);
    }

    private void buildMenu() {
        mainMenu.setWidth(100f, Unit.PERCENTAGE);
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN") || SpringSecurityHelper.hasRole("ROLE_TRANSLATE")) {
            translateMenuItem = mainMenu.addItem("Перевод", this);
            questsMenuItem = mainMenu.addItem("Квесты", this);
            searchInCatalogsMenuItem = mainMenu.addItem("Поиск в справочниках", this);
            importMenuItem = mainMenu.addItem("Импорт", this);
        }
        if (SpringSecurityHelper.hasRole("ROLE_ADMIN")) {
            syncMenuItem = mainMenu.addItem("Синхронизация", this);
            usersMenuItem = mainMenu.addItem("Пользователи", this);
        }
        changePasswordMenuItem = mainMenu.addItem("Сменить пароль", this);
    }

    private void buildHeader() {
        headerLayout.setWidth(100f, Unit.PERCENTAGE);
        headerLayout.addComponent(loginLabel, 7, 0);
        ExternalResource resource = new ExternalResource("j_spring_security_logout");
        logoutLink.setResource(resource);
        logoutLink.setCaption("Выход");
        headerLayout.addComponent(logoutLink, 8, 0);
    }

    private void buildTabs() {
        tabs.setWidth(100f, Unit.PERCENTAGE);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loginLabel.setCaption(principal.getUsername());
    }

    @Override
    public void menuSelected(MenuBar.MenuItem selectedItem) {
        if (selectedItem == importMenuItem) {
            if (importTabContent == null) {
                importTabContent = new ImportTab(service);
            }
            TabSheet.Tab tab = tabs.addTab(importTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        } else if (selectedItem == translateMenuItem) {
            if (translateTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(translateTabContent);
                if (tab == null) {
                    translateTabContent = new TranslateTab(service);
                }
            } else {
                translateTabContent = new TranslateTab(service);
            }

            TabSheet.Tab tab = tabs.addTab(translateTabContent, selectedItem.getText());
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
            if (searchInCatalogsTabContent == null) {
                searchInCatalogsTabContent = new SearchInCatalogsTab(service);
            }
            Window subWindow = new Window(selectedItem.getText());
            subWindow.setModal(true);

            subWindow.center();
            this.getUI().addWindow(subWindow);
            subWindow.setResizable(false);
            subWindow.setSizeFull();
            subWindow.setContent(searchInCatalogsTabContent);
            searchInCatalogsTabContent.setWidth();
            //TabSheet.Tab tab = tabs.addTab(searchInCatalogsTabContent, selectedItem.getText());
            //tab.setClosable(true);
            //tabs.setSelectedTab(tab);
        } else if (selectedItem == changePasswordMenuItem) {
            if (changePasswordTabContent != null) {
                TabSheet.Tab tab = tabs.getTab(changePasswordTabContent);
                if (tab != null) {
                    tabs.removeTab(tabs.getTab(changePasswordTabContent));
                }
            }
            changePasswordTabContent = new ChangePasswordTab(service);
            TabSheet.Tab tab = tabs.addTab(changePasswordTabContent, selectedItem.getText());
            tab.setClosable(true);
            tabs.setSelectedTab(tab);
        }

    }
}