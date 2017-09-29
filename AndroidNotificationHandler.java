/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.opendynamics.DummyWebService;

/**
 *
 * @author syeda
 */
public class AndroidNotificationHandler {
    public void sendNotification(String message) throws IOException {
        
        String GOOGLE_SERVER_KEY = "AAAA45NLWEo:APA91bHq0Dz6_qGT-b7ijjPRPT9azcAZnsp0F3a_CmTCo5Flw8NXTeE2qNsD4UQ5ZLZ__yVDcpVcUdFaq1LOl4_oVGcAfkv_sgYeNyrKUYYupWdZfbMFAoOiObPUf_KAtDfJ30TKSzQK";
        String MESSAGE_KEY = "Test-1330";

       try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            // 2. Open connection          
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 3. Specify POST method        
            conn.setRequestMethod("POST");
            // 4. Set the headers      
           conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + GOOGLE_SERVER_KEY);
            //conn.setRequestProperty("Authorization", "key=" + apiKey);          
           conn.setDoOutput(true);
            // 5. Add JSON data into POST request body        
           //`5.1 Use Jackson object mapper to convert Contnet object into JSON    
           // 5.2 Get connection output stream        
           
//           System.out.println(" --> " + GOOGLE_SERVER_KEY);
//            System.out.println(" --> " + message);
            // 5.3 Copy Content "JSON" into      
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                // 5.3 Copy Content "JSON" into    
               wr.write(message.getBytes());
                // 5.4 Send the request  
               wr.flush();
                // 5.5 close        
            }
            // 6. Get the response    
           int responseCode = conn.getResponseCode();
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            // 7. Print result        
            System.out.println(response.toString());
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }
    
    public void sendEventCreatedNotification( String title, String message) throws IOException{
        QueryHandler qH = new QueryHandler();
//        AndroidNotificationHandler aNH = new AndroidNotificationHandler();
        
        String[] column = {"gcm_id"};
        String[] deviceType = {"syedabdullah76@yahoo.com"};
        String[] deviceTypeColumn = {"email"};
        List<String []> gcimIds = qH.selectColumns(column, "generic_signup", deviceTypeColumn, deviceType, "");
        
        
        
        
        for(String[] gcim: gcimIds){
//            System.out.println("Length of Array: " + gcim.length);
            if(gcim[0] != null && !gcim[0].equals("")){
//                System.out.println("Sending Message to this GCIM ID" + gcim[0]);
                try{
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("title", title);
                    dataJson.put("message", message);
                    dataJson.put("articleId", "111");
                    dataJson.put("action", "0");
                    dataJson.put("id", "");
                    dataJson.put("type", "NOTIFICATION");

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("message", dataJson);


                    JSONObject mainJson = new JSONObject();
//                    mainJson.put("to", "dw9_-MZTJmI:APA91bGJvaCEfAoWatuLZD6fcHvFVSotcz6v3GOYE0zjHr9gmwtppD4VruphFUwwss5Y5wx8DosgkCBCNO_uRIyEV9J_DvDywfQFWg6p5gjCPzKSlmCo7O86Nmdz5JopV6e5HqgVREef");
                    mainJson.put("to", gcim[0]);
                    mainJson.put("data", dataObj);

                    sendNotification(mainJson.toString());
                } catch (JSONException ex) {
                    Logger.getLogger(DummyWebService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
    }
}
