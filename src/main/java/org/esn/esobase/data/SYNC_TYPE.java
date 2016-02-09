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
public enum SYNC_TYPE {

    TO_SPREADSHEET,
    TO_DB;

    @Override
    public String toString() {
        String result = null;
        switch (this) {
            case TO_DB:
                result = "Загрузить в базу";
                break;
            case TO_SPREADSHEET:
                result = "Загрузить в таблицы";
                break;
        }
        return result;
    }
}
