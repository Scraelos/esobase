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
public enum RAW_TYPE {

    NPC_PHRASE("Фраза NPC"),
    PLAYER_PHRASE("Фраза игрока"),
    SUBTITLE("Субтитры"),
    GREETINGS("Приветствие");

    private RAW_TYPE(String name_) {
        name = name_;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }

}
