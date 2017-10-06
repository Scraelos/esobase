/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data.repository;

import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 *
 * @author scraelos
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface GSpreadSheetsWithDeprecated<T extends Object, ID extends Serializable> extends JpaRepository<T, ID> {

    Page<T> findByDeprecated(Boolean deprecated, Pageable pageable);

    long countByDeprecated(Boolean deprecated);
}
