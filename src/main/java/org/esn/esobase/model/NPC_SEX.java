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
    m,
    F,
    f,
    N,
    n,
    U,
    p,
    P;

    @Override
    public String toString() {
        switch (this) {
            case M:
            case m:
                return "Мужской";
            case F:
            case f:
                return "Женский";
            case n:
            case N:
                return "Средний";
            case p:
            case P:
                return "множественное число";
        }
        return "?";
    }

}
