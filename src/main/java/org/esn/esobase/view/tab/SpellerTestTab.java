/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.tab;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
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

    private TextArea textArea;
    private Button checkButton;
    private Table resultTable;

    public SpellerTestTab() {
        textArea = new TextArea();
        this.addComponent(textArea);
        checkButton = new Button();
        checkButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                resultTable.removeAllItems();
                try {
                    SpellService service = new SpellService();
                    SpellServiceSoap port = service.getSpellServiceSoap12();
                    CheckTextRequest parameters = new CheckTextRequest();
                    parameters.setLang("ru");
                    parameters.setText(textArea.getValue());
                    parameters.setFormat("plain");
                    parameters.setOptions(0);
                    CheckTextResponse result = port.checkText(parameters);
                    for (SpellError error : result.getSpellResult().getError()) {
                        Item item = resultTable.addItem(error);
                        SPELLER_ERROR e = SPELLER_ERROR.valueOf(error.getCode());
                        item.getItemProperty("errorType").setValue(e);
                        item.getItemProperty("word").setValue(error.getWord());
                        item.getItemProperty("s").setValue(error.getS());
                        
                    }
                    System.out.println("Result = " + result);
                } catch (Exception ex) {
                    // TODO handle custom exceptions here
                }

            }
        });
        this.addComponent(checkButton);
        resultTable = new Table();
        resultTable.addContainerProperty("errorType", SPELLER_ERROR.class, null);
        resultTable.addContainerProperty("word", String.class, null);
        resultTable.addContainerProperty("s", List.class, null);
        this.addComponent(resultTable);
    }

}
