/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

/**
 *
 * @author scraelos
 */
public enum TRANSLATE_STATUS {

    DIRTY("Черновик"),
    NEW("Новый"),
    PREACCEPTED("Перевод верен"),
    ACCEPTED("Принят"),
    REJECTED("Отклонён");

    private TRANSLATE_STATUS(String name_) {
        name = name_;
    }
    private final String name;

    @Override
    public String toString() {
        return name;
    }

}
