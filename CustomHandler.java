/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics.helper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author syeda
 */
public class CustomHandler {
    
    public boolean sendAdroidEventNotification(String eventId, String notificationTitle, String notificationText){
//        System.out.println("Testing");
        
        QueryHandler qH = new QueryHandler();
        AndroidNotificationHandler aNH = new AndroidNotificationHandler();
        
        String selectColumns[] = {"gcm_id"};
        String tableName = "generic_signup";
        String whereColumns[] = {"device_type"};
        String whereValues[] = {"Android"};
        String whereCondition = "and";
        String to = "";
        List<String []> rSet = qH.selectColumns(selectColumns,  tableName,  whereColumns,  whereValues, whereCondition);
//        System.out.println("Rows: " + rSet.size());
        try {
            for(int i = 0; i < rSet.size(); i++){
                to = rSet.get(i)[0];
                if(to != null & !"".equals(to)){
                    JSONObject mainJson = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    
                    
                    dataJson.put("notification_type", "1");
                    dataJson.put("event_id", eventId);
                    dataJson.put("title", notificationTitle);
                    dataJson.put("text", notificationText);
                
//                    mainJson.put("to", to);
                    mainJson.put("to", "fZO7m0ZvyBg:APA91bHmtd8V31aWSn92RYH5TLtB3C4viGa3bS6DMXYBUe_Pef-_SVVkAu0LQnVNO0flPuYNw5jrwP-VLLEbXg5XFKHvWWOTn2qpA8qp7UhlwXSgauhn_MfXhNy6VMAsW129spGVvK7S");
                    mainJson.put("data", dataJson);
                
                    aNH.sendNotification(mainJson.toString());
                }
            }
            return true;
        } catch (JSONException | IOException ex) {
            Logger.getLogger(CustomHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendiOSEventNotification(String eventId, String notificationTitle, String notificationText){
        iOSNotificationHandler iNH = new iOSNotificationHandler();
        
//        gcmPayload = iNH.new GCMPayloadPack();
//        gcmPayload.setGcmId(gcmId);
//        gcmPayload.setDeviceId(id);
//        gcmPayload.setTitle(notificationTitle);
//        gcmPayload.setBody(notificationText);
        // Sending push notification to applicant
//        iNH.prepareAPNSForIOSPartner(gcmPayload);
        return false;
    }
}
