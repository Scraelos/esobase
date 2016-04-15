/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

/**
 *
 * @author Scraelos
 */
public enum SPELLER_ERROR {
    ERROR_UNKNOWN_WORD(1, "Слова нет в словаре"),
    ERROR_REPEAT_WORD(2, "Повтор слова"),
    ERROR_CAPITALIZATION(3, "Неверное употребление прописных и строчных букв"),
    ERROR_TOO_MANY_ERRORS(4, "Текст содержит слишком много ошибок");

    private SPELLER_ERROR(int code_, String description_) {
        this.code = code_;
        this.description = description_;
    }

    public static SPELLER_ERROR valueOf(int code_) {
        for (SPELLER_ERROR e : values()) {
            if (code_ == e.code) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return description;
    }

    private final int code;
    private final String description;
}
