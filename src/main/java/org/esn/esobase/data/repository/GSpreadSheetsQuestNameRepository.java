/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.repository;

import java.util.List;
import org.esn.esobase.model.GSpreadSheetsQuestName;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author scraelos
 */
public interface GSpreadSheetsQuestNameRepository extends JpaRepository<GSpreadSheetsQuestName, Long> {

    List<GSpreadSheetsQuestName> findAllBy(Pageable pageable);

}