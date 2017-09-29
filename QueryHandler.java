/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author syeda
 */
public class QueryHandler {
    
    public boolean updateFormId(String formTable, String currentID, String newID){
        String query = "UPDATE app_fd_"+ formTable +" SET id = ? WHERE id = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, newID);
                stmt.setString(2, currentID);
                int rSet = stmt.executeUpdate();
//                System.out.println(rSet + " Records have been Modified");
                return rSet == 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateFormColumnValue(String formTable, String columnName, String columnValue, String idValue){
        String query = "UPDATE app_fd_"+ formTable +" SET c_"+ columnName +" = ? WHERE id = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, columnValue);
                stmt.setString(2, idValue);
                int rSet = stmt.executeUpdate();
//                System.out.println(rSet + " Records have been Modified");
                return rSet == 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean isEmpty(String id, String fieldId, String tableName){
        
        String query = "Select c_"+fieldId+" from app_fd_"+tableName+" where id = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try{
            Connection con = ds.getConnection();
            try{
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, id);
                try {
                    ResultSet rSet = stmt.executeQuery();
                    if (rSet.next()) {
                        String result = rSet.getString("c_" + fieldId);
//                        System.out.println(result);
                        return result == null || result.isEmpty();
                    }
                } catch(SQLException ex){
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }catch(SQLException ex){
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean insertInto(String tableName, String paramsName[], String paramsValues[], String id){
        String query = "INSERT INTO app_fd_" + tableName + " (id, dateCreated, dateModified";
        for (String paramName : paramsName) {
            query += ", c_" + paramName;
        }
        query += ") VALUES( ";
        if(id == null || id.isEmpty()){
            query += "UUID(), NOW() , NOW()";
        }else{
            query += "'" + id + "', NOW(), NOW()";
        }
        for (String paramValue : paramsValues) {
            query += ", '" + paramValue + "'";
        }
        query += ")";
        
//        System.out.println("Query String : " + query);
        
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try{
            Connection con = ds.getConnection();
            try{
                PreparedStatement stmt = con.prepareStatement(query);
//                stmt.setString(1, id);
                try {
                    return stmt.execute();
                } catch(SQLException ex){
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }catch(SQLException ex){
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public String[] selectRowUsingId(String id, String params[], String tableName){
        String query = "Select ";
        
        if(params!= null && params.length >= 1){
            for(int i = 0; i < params.length; i++){
                if(i == params.length - 1){
                    query += "c_" + params[i] + " ";
                }else{
                    query += "c_" + params[i] +", ";
                }
            }
        }else{
            query += " * ";
        }
        
        query += " from app_fd_" + tableName + " where id = '"+id+"'";
        
//        System.out.println("Query String : " + query);
        
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                try (ResultSet rSet = stmt.executeQuery()) {
                    if (rSet.next()) {
                        if(params!= null && params.length >= 1){
                            String[] result = new String[params.length];
                            for(int i = 0; i < params.length; i++){
    //                            System.out.println("Query Result: " + rSet.getString("c_" + params[i]));
                                result[i] = rSet.getString("c_" + params[i]);
                            }
                            return result;
                        }else{
                            ResultSetMetaData rMetaData = rSet.getMetaData();
                            int columnCount = rMetaData.getColumnCount();
//                            System.out.println("column count : " + columnCount);
                            
                            String[] result = new String[columnCount];
                            for(int i = 1; i <= columnCount; i++){
//                                System.out.println("Query Result: " + rSet.getString(i));
                                result[i - 1] = rSet.getString(i);
                                
                            }
                            con.close();
                            return result;
                        }
                        
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<String []> selectColumns(String selectColumns[], String tableName, String whereColumns[], String whereValues[], 
            String whereCondition){
        String query = "Select ";
        if(selectColumns.length >= 1){
            for(int i = 0; i < selectColumns.length; i++){
                if(i == selectColumns.length - 1){
                    query += "c_" + selectColumns[i] + " ";
                }else{
                    query += "c_" + selectColumns[i] +", ";
                }
            }
        }else query += " * ";
        
        query += " from app_fd_" + tableName;
//        + " where id = '"+id+"'";
        if(whereColumns.length > 0 && whereColumns.length == whereValues.length){
            query += " where ";
            for(int i = 0; i < whereColumns.length; i++){
                if(i == whereColumns.length - 1){
                    query += " c_" + whereColumns[i] + " = '" + whereValues[i] + "' ";
                }else{
                    query += " c_" + whereColumns[i] + " = '" + whereValues[i] + "' " + whereCondition;
                }
            }
        }
        
//        System.out.println("Query String : " + query);
        List<String []> result = new ArrayList<String []>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                try (ResultSet rSet = stmt.executeQuery()) {
                    
                    while(rSet.next()){
                        ResultSetMetaData rMetaData = rSet.getMetaData();
                        int columnCount = rMetaData.getColumnCount();
                        String[] resultantRow = new String[columnCount];
                        for(int i = 1; i <= columnCount; i++){
                            resultantRow[i-1] = rSet.getString(i);
//                            System.out.println("basic : " + rSet.getString(i));
                        }
                        result.add(resultantRow);
                        
                    }
                    con.close();
                    return result;
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public int DeleteRowUsingId(String formTable, String idValue){
        String query = "Delete from app_fd_"+ formTable +" WHERE id = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setString(1, idValue);
                int rSet = stmt.executeUpdate();
//                System.out.println(rSet + " Records have been Modified");
                return rSet;
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
