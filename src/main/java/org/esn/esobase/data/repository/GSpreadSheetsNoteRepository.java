/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.repository;

import java.util.List;
import org.esn.esobase.model.GSpreadSheetsNote;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author scraelos
 */
public interface GSpreadSheetsNoteRepository extends GSpreadSheetsWithDeprecated<GSpreadSheetsNote, Long> {

    List<GSpreadSheetsNote> findAllBy(Pageable pageable);

}
