/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.model.lib;

import java.io.Serializable;

/**
 *
 * @author scraelos
 */
public abstract class DAO implements Serializable {

    private static final long serialVersionUID = 1L;

    public abstract Long getId();

    public abstract void setId(Long paramObject);

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (getId() == null ? 0 : getId().hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DAO other = (DAO) obj;

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public String toString() {
        if (getId() != null) {
            return getId().toString();
        } else {
            return null;
        }

    }
}
