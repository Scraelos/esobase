/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.esn.esobase.data.DBService;
import org.esn.esobase.data.SysAccountService;
import org.esn.esobase.model.SysAccount;
import org.esn.esobase.security.SpringSecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author scraelos
 */
@Component
@Scope(value = "prototype")
public class ChangePasswordTab extends VerticalLayout {

    private PasswordField oldPassword;
    private PasswordField password;
    private PasswordField passwordRepeat;
    private Button changePassword;
    private TextField apiKeyField;
    private Button newApiKeyButton;

    @Autowired
    private SysAccountService sysAccountService;

    public ChangePasswordTab() {
    }

    public void Init() {
        this.removeAllComponents();
        oldPassword = new PasswordField("Старый пароль");
        oldPassword.setImmediate(true);
        oldPassword.setRequired(true);
        oldPassword.addValidator(new OldPasswordValidator(oldPassword));
        password = new PasswordField("Новый пароль");
        password.setImmediate(true);
        password.setRequired(true);
        passwordRepeat = new PasswordField("Подтверждение нового пароля");
        passwordRepeat.setImmediate(true);
        passwordRepeat.setRequired(true);
        passwordRepeat.addValidator(new DoublePasswordValidator(password, passwordRepeat));
        passwordRepeat.setValidationVisible(false);
        password.addValidator(new DoublePasswordValidator(password, passwordRepeat));
        password.setValidationVisible(false);
        changePassword = new Button("Сменить пароль");
        changePassword.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (oldPassword.isValid() && password.isValid() && passwordRepeat.isValid()) {
                    changePasswordAction();
                }
            }
        });

        this.addComponent(oldPassword);
        this.addComponent(password);
        this.addComponent(passwordRepeat);
        this.addComponent(changePassword);
        apiKeyField = new TextField("Ключ API");
        apiKeyField.setWidth(300f, Unit.PIXELS);
        String apiKey = sysAccountService.getApiKey(SpringSecurityHelper.getSysAccount());
        if (apiKey != null) {
            apiKeyField.setReadOnly(false);
            apiKeyField.setValue(apiKey);
            apiKeyField.setReadOnly(true);
        }

        newApiKeyButton = new Button("Сменить ключ API");
        newApiKeyButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String apiKey=sysAccountService.newApiKey(SpringSecurityHelper.getSysAccount());
                apiKeyField.setReadOnly(false);
                apiKeyField.setValue(apiKey);
                apiKeyField.setReadOnly(true);
            }
        });
        this.addComponent(apiKeyField);
        this.addComponent(newApiKeyButton);
    }

    private void changePasswordAction() {
        sysAccountService.updateUserPassword(SpringSecurityHelper.getSysAccount(), password.getValue());
        Notification n = new Notification("Смена пароля", "Пароль успешно изменён", Notification.Type.HUMANIZED_MESSAGE);
        n.setDelayMsec(2000);
        n.show(getUI().getPage());
        TabSheet tabs = (TabSheet) this.getParent();
        tabs.removeTab(tabs.getTab(this));

    }

    private class DoublePasswordValidator implements Validator {

        private final PasswordField password1;
        private final PasswordField password2;

        public DoublePasswordValidator(PasswordField password1, PasswordField password2) {
            this.password1 = password1;
            this.password2 = password2;
        }

        @Override
        public void validate(Object value) throws Validator.InvalidValueException {
            if (!password1.getValue().equals(password2.getValue())) {
                throw new Validator.InvalidValueException("Пароли не совпадают");
            }
        }

    }

    private class OldPasswordValidator implements Validator {

        private final PasswordField oldPassword;

        public OldPasswordValidator(PasswordField password1) {
            this.oldPassword = password1;
        }

        @Override
        public void validate(Object value) throws Validator.InvalidValueException {
            SysAccount sysAccount = SpringSecurityHelper.getSysAccount();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            if (!passwordEncoder.matches(oldPassword.getValue(), sysAccount.getPassword())) {
                throw new Validator.InvalidValueException("Старый пароль введён неверно");
            }
        }

    }
}
