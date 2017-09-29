/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics.helper;

import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.form.dao.FormDataDao;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormService;
import org.joget.apps.app.service.AppUtil;
/**
 *
 * @author syeda
 */
public class JogetFormHandler {
    public void insertForm(String formId, String key, String[] fields, String values[]) {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        FormDefinitionDao formDefinitionDao = (FormDefinitionDao) AppUtil.getApplicationContext().getBean("formDefinitionDao");
        FormService formService = (FormService) AppUtil.getApplicationContext().getBean("formService");
        FormDataDao formDataDao = (FormDataDao) AppUtil.getApplicationContext().getBean("formDataDao");

        FormDefinition formDef = formDefinitionDao.loadById(formId, appDef);

        Form form = null;
        if (formDef != null) {
            String formJson = formDef.getJson();

            if (formJson != null) {
                form = (Form) formService.createElementFromJson(formJson);
            }

            if (form != null) {
                try {
                    java.util.Date dt = new java.util.Date();

                    FormRowSet rowSet = new FormRowSet();
                    FormRow row = new FormRow();
                    row.setId(key);
                    row.setDateCreated(dt);
                    row.setDateModified(dt);
//                    System.out.println("Test2");
                    if(fields.length == values.length){
//                        System.out.println("Test2.1");
//                        System.out.println("Test2.1" + fields.length);
                        for(int i = 0; i < fields.length; i++){
                            row.put(fields[i] , values[i]);
//                            System.out.println("Test2.2");
                        }
                    }                   
                    
//                    System.out.println("Test3");
                    rowSet.add(row);
//                    System.out.println("Test4");
                    formDataDao.saveOrUpdate(form, rowSet);
//                    System.out.println("Test5");
                } catch (Exception ex) {
//                    Logger.getLogger(this.getClassName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex.getMessage());
                    
                }
            }
        }
    }
}
