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
public interface GSpreadSheetEntity extends TranslatedEntity {

    public Long getaId();

    public Long getbId();

    public Long getcId();

    public Long getRowNum();

    public Integer getWeight();

    public Boolean getDeprecated();

    public Date getChangeTime();

    public String getTranslator();

    public void setRowNum(Long rowNum);

    public void setId(Long id);

    public void setTextEn(String textEn);

    public void setTextRu(String textRu);
    
    public void setTranslator(String translator);
}
