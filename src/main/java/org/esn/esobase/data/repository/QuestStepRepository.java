/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.repository;

import org.esn.esobase.model.QuestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author scraelos
 */
public interface QuestStepRepository extends JpaRepository<QuestStep, Long>, JpaSpecificationExecutor<QuestStep> {

}
