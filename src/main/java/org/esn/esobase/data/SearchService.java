/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.esn.esobase.model.GSpreadSheetEntity;
import org.esn.esobase.model.GSpreadSheetsNpcName;
import org.esn.esobase.model.NPC_SEX;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author scraelos
 */
public class SearchService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public List<GSpreadSheetEntity> searchInCatalogs(List<String> tableNames, String searchString, Boolean withTranslatedNeighbours, Integer numberOfNeighbours, Boolean isRegexp) {
        List<GSpreadSheetEntity> result = new ArrayList<>();
        if ((searchString != null && searchString.length() > 2) || withTranslatedNeighbours) {
            for (String tableName : tableNames) {
                boolean firstWhere = true;
                StringBuilder sqlBuilder = new StringBuilder();
                if (withTranslatedNeighbours) {
                    sqlBuilder.append("select * from (");
                }
                sqlBuilder.append("select g.id, g.texten,g.textru,g.rownum,g.translator");
                if (tableName.equals("GSpreadSheetsNpcName")) {
                    sqlBuilder.append(",g.sex");
                }
                if (withTranslatedNeighbours) {
                    sqlBuilder.append(", (select count(*) from(select pg.id,pg.texten,pg.textru from ").append(tableName).append(" pg where pg.rownum<g.rownum order by pg.rownum DESC LIMIT ").append(numberOfNeighbours).append(" ) as prev where texten!=textru) as cnt_prev, (select count(*) from(select ng.id,ng.texten,ng.textru from ").append(tableName).append(" ng where ng.rownum>g.rownum order by ng.rownum ASC LIMIT ").append(numberOfNeighbours).append(" ) as nnext where texten!=textru) as cnt_next");
                }
                sqlBuilder.append(" from ").append(tableName).append(" g");
                if (searchString != null && searchString.length() > 2) {
                    if (isRegexp) {
                        addPredicate(sqlBuilder, " (g.texten ~ :searchString or g.textru ~ :searchString)", firstWhere);
                        firstWhere = false;
                    } else {
                        addPredicate(sqlBuilder, " (g.texten ilike :searchString or g.textru ilike :searchString)", firstWhere);
                        firstWhere = false;
                    }

                }
                if (withTranslatedNeighbours) {
                    addPredicate(sqlBuilder, " g.texten=g.textru", firstWhere);
                    firstWhere = false;
                }

                sqlBuilder.append(" order by rownum");
                if (withTranslatedNeighbours) {
                    sqlBuilder.append(") as countneighbours where cnt_prev=").append(numberOfNeighbours.toString()).append(" and cnt_next=").append(numberOfNeighbours.toString()).append(";");

                }
                Query nativeQuery = em.createNativeQuery(sqlBuilder.toString());
                if (searchString != null && searchString.length() > 2) {
                    if (isRegexp) {
                        nativeQuery.setParameter("searchString", searchString);
                    } else {
                        nativeQuery.setParameter("searchString", "%" + searchString + "%");
                    }
                }
                List<Object[]> resultList = nativeQuery.getResultList();
                for (Object[] o : resultList) {
                    BigInteger id = (BigInteger) o[0];
                    String textEn = (String) o[1];
                    String textRu = (String) o[2];
                    BigInteger rowNum = (BigInteger) o[3];
                    String translator = (String) o[4];
                    try {
                        Class<?> clazz = Class.forName("org.esn.esobase.model." + tableName);
                        Constructor<?> ctor = clazz.getConstructor();
                        GSpreadSheetEntity entity = (GSpreadSheetEntity) ctor.newInstance(new Object[]{});
                        entity.setId(id.longValue());
                        entity.setRowNum(rowNum.longValue());
                        entity.setTextEn(textEn);
                        entity.setTextRu(textRu);
                        entity.setTranslator(translator);
                        if (tableName.equals("GSpreadSheetsNpcName")) {
                            String sexString = (String) o[5];
                            if (sexString != null && !sexString.isEmpty()) {
                                NPC_SEX sex = NPC_SEX.valueOf(sexString);
                                GSpreadSheetsNpcName nameEntity = (GSpreadSheetsNpcName) entity;
                                nameEntity.setSex(sex);
                            }
                        }
                        result.add(entity);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SecurityException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(SearchService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return result;
    }

    private void addPredicate(StringBuilder sqlBuilder, String predicate, boolean firstWhere) {
        if (firstWhere) {
            sqlBuilder.append(" where");
        } else {
            sqlBuilder.append(" and");
        }
        sqlBuilder.append(predicate);
    }
}
