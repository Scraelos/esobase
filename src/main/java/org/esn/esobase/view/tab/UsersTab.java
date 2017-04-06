/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.esn.esobase.data.DBService;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.model.SysAccountRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author scraelos
 */
public class UsersTab extends VerticalLayout {

    private HorizontalLayout actions;
    private Button refreshButton;
    private Button addButton;
    private HorizontalLayout tableAndForm;
    private Table table;
    private BeanItemContainer<SysAccount> container;
    private FormLayout form;
    private FieldGroup fieldGroup;
    private TextField login;
    private PasswordField password;
    private PasswordField passwordRepeat;
    private TwinColSelect roles;
    private CheckBox isBlocked;
    private Button saveButton;

    private BeanItem currentUserItem;

    private final DBService service;

    public UsersTab(DBService service) {
        this.service = service;
        actions = new HorizontalLayout();
        refreshButton = new Button("Обновить");
        refreshButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                LoadTable();
            }
        });
        actions.addComponent(refreshButton);
        addButton = new Button("Создать");
        addButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                AddUser();
            }
        });
        actions.addComponent(addButton);
        this.addComponent(actions);
        tableAndForm = new HorizontalLayout();
        tableAndForm.setSizeFull();
        table = new Table();
        table.setSizeFull();
        container = new BeanItemContainer<>(SysAccount.class);
        table.setContainerDataSource(container);
        table.setVisibleColumns(new Object[]{"login", "roles"});
        table.addItemClickListener(new TableClickListener());
        LoadTable();
        tableAndForm.addComponent(table);
        tableAndForm.setExpandRatio(table, 0.5f);
        form = new FormLayout();
        form.setSizeFull();
        login = new TextField("Логин");
        login.setNullRepresentation("");
        login.setRequired(true);
        form.addComponent(login);
        password = new PasswordField("Пароль");
        form.addComponent(password);
        passwordRepeat = new PasswordField("Повтор пароля");
        passwordRepeat.addValidator(new PasswordValidator(password, passwordRepeat));
        form.addComponent(passwordRepeat);
        roles = new TwinColSelect("Роли");
        BeanItemContainer<SysAccountRole> rolesContainer = new BeanItemContainer<>(SysAccountRole.class);
        rolesContainer = service.loadBeanItems(rolesContainer);
        roles.setContainerDataSource(rolesContainer);
        roles.setWidth(900f, Unit.PIXELS);
        Page.Styles styles = Page.getCurrent().getStyles();
        styles.add(".v-font-size {\n"
                + "    font-size: 11px;\n"
                + "}");
        roles.addStyleName("v-font-size");
        form.addComponent(roles);
        isBlocked=new CheckBox("Заблокирован");
        form.addComponent(isBlocked);
        saveButton = new Button("Сохранить");
        saveButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                SaveForm();
            }
        });
        form.addComponent(saveButton);
        form.setVisible(false);
        tableAndForm.addComponent(form);
        tableAndForm.setExpandRatio(form, 0.5f);
        this.addComponent(tableAndForm);
    }

    private void LoadTable() {
        container = service.loadBeanItems(container);
    }

    private void AddUser() {
        SysAccount sysAccount = new SysAccount();
        sysAccount.setIsBlocked(Boolean.FALSE);
        BeanItem<SysAccount> beanItem = new BeanItem<>(sysAccount);
        currentUserItem = beanItem;
        OpenForm();
    }

    private void OpenForm() {
        form.setVisible(true);
        fieldGroup = new FieldGroup(currentUserItem);
        fieldGroup.bind(login, "login");
        fieldGroup.bind(roles, "roles");
        fieldGroup.bind(isBlocked, "isBlocked");

    }

    private void CloseForm() {
        form.setVisible(false);
    }

    private void SaveForm() {
        try {
            fieldGroup.commit();
            SysAccount sysAccount = (SysAccount) currentUserItem.getBean();
            if (password.getValue() != null && !password.getValue().isEmpty() && passwordRepeat.isValid()) {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(password.getValue());
                sysAccount.setPassword(hashedPassword);
            }
            if (!sysAccount.getPassword().isEmpty()) {
                service.saveEntity(sysAccount);
            }
            CloseForm();
            LoadTable();
        } catch (FieldGroup.CommitException ex) {
            Logger.getLogger(UsersTab.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class TableClickListener implements ItemClickEvent.ItemClickListener {

        @Override
        public void itemClick(ItemClickEvent event) {
            currentUserItem = (BeanItem) event.getItem();
            OpenForm();
        }

    }

    private class PasswordValidator implements Validator {

        private final PasswordField password1;
        private final PasswordField password2;

        public PasswordValidator(PasswordField password1, PasswordField password2) {
            this.password1 = password1;
            this.password2 = password2;
        }

        @Override
        public void validate(Object value) throws InvalidValueException {
            if (!password1.getValue().equals(password2.getValue())) {
                throw new InvalidValueException("Пароли не совпадают");
            }
        }

    }

}
