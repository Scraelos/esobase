/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

/**
 *
 * @author scraelos
 */
public class OriginalTextMismatchException extends Exception {

    private final Long rowNum;
    private final String textG;
    private final String textP;

    public OriginalTextMismatchException(Long rowNum_, String textG_, String textP_) {
        this.rowNum = rowNum_;
        this.textG = textG_;
        this.textP = textP_;
    }

    @Override
    public String getMessage() {
        return "Text mismatch at rowNum=" + rowNum + " spreadsheets text='" + textG + "' database text='" + textP + "'";
    }

}
