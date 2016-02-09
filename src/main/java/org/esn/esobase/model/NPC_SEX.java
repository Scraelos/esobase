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
public enum NPC_SEX {

    M,
    F,
    N,
    U;

    @Override
    public String toString() {
        switch (this) {
            case M:
                return "Мужской";
            case F:
                return "Женский";
        }
        return "?";
    }

}
