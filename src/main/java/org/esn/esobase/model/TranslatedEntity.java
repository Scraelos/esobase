/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model;

import java.util.Date;

/**
 *
 * @author scraelos
 */
public interface TranslatedEntity {

    public String getTextEn();

    public String getTextRu();
    
    public Date getChangeTime();
    
    public String getTranslator();
}
