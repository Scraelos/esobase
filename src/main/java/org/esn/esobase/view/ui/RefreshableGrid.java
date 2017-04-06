/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.view.ui;

import com.vaadin.shared.ui.grid.ScrollDestination;

/**
 *
 * @author scraelos
 */
public interface RefreshableGrid {

    public void Refresh();

    public void scrollToRow(int row, ScrollDestination destination);
}
