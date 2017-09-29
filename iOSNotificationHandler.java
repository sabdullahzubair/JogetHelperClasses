/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package org.opendynamics.helper;


import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.exceptions.InvalidSSLConfig;
import com.notnoop.exceptions.RuntimeIOException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SetupManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author syeda
 */
public class iOSNotificationHandler {
    
    private static ApnsService servicePartner = null;
    private static String isIOSPNSEnabledPartner = "Yes";
    
    private String gcmId;
    private String title;
    private String body;
    private String sound = "default";
    private String event_id;
    private String type;
    
    private String assetId;
    private String ticketId;
    private String deviceId;
    private String actionLockKey = "View";
    String apnsCode = "NEW_JOB_REQUEST";
    String acme_device_id;
    private String engineerId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }
    
    public String getEventId() {
        return event_id;
    }

    public void setEventId(String eventId) {
        this.event_id = eventId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getActionLockKey() {
        return actionLockKey;
    }

    public void setActionLockKey(String actionLockKey) {
        this.actionLockKey = actionLockKey;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getApnsCode() {
        return apnsCode;
    }

    public void setApnsCode(String apnsCode) {
        this.apnsCode = apnsCode;
    }

    public String getAcme_device_id() {
        return acme_device_id;
    }

    public void setAcme_device_id(String acme_device_id) {
        this.acme_device_id = acme_device_id;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getEngineerId() {
        return engineerId;
    }

    public void setEngineerId(String engineerId) {
        this.engineerId = engineerId;
    }
//    public class GCMPayloadPack {
//
//        
//
//    }
    
    private void sendToIos(final ApnsService service) throws IOException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    String payload = APNS.newPayload().alertTitle(getTitle()).alertBody(getBody()).sound(getSound()).customField("acme_event_id", getEventId()).customField("acme_type", getType()).build();
                    
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("title", getTitle());
                    dataJson.put("body", getBody());
                    dataJson.put("sound", getSound());
                    
                    JSONObject alert = new JSONObject();
                    alert.put("alert", dataJson);
                    
                    JSONObject mainJson = new JSONObject();
                    mainJson.put("aps", alert);
                    mainJson.put("acme_type", getType());
                    mainJson.put("acme_event_id", getEventId());
                    
                    
                    LogUtil.info(getClass().getName(), " sendToIos payload = " + mainJson.toString());
                    LogUtil.info(getClass().getName(), " Gcm Id = " + getGcmId());
                    ApnsNotification notification = service.push(getGcmId(), mainJson.toString());
                    LogUtil.info(getClass().getName(), " sendToIos Apns Resposne = " + notification.toString());
                    System.out.println(" sendToIos Apns Resposne = " + notification.toString());
                    //insertForm(gcmPayLoad.getGcmId(), payload, notification.toString(), "IOS");
                } catch (JSONException ex) {
                    Logger.getLogger(iOSNotificationHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private void prepareAPNSForIOSPartner() {
        Connection con = null;
        PreparedStatement ps = null;
        String iosCertPassword = "";
        try {
            if (servicePartner == null) {
                System.out.println("-->Reading PushNotification Settings");
                DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

                con = ds.getConnection();
                String getAPNSSetup = "SELECT * FROM app_fd_ios_push_notif_conf";
                ps = con.prepareStatement(getAPNSSetup);
                try (ResultSet rs = ps.executeQuery()) {
                    String certFile = "";
//                    isIOSPNSEnabledPartner = "No";
                    String sandboxProduction = "SandBox";
                    String typeUse = "sandbox";
                    
                    while (rs.next()) {
                        System.out.println(" Reading Settings ");
//                        isIOSPNSEnabledPartner = rs.getString("c_iosPNSEnabled");
                        sandboxProduction = rs.getString("c_sandProd");
                        String id = rs.getString("id");
                        System.out.println(" id : " + id);
//                        System.out.println(" isIOSPNSEnabledPartner : " + isIOSPNSEnabledPartner);
                        System.out.println(" sandboxProduction : " + sandboxProduction);
                        typeUse = sandboxProduction;
                        String absolutePath = SetupManager.getBaseDirectory() + "app_formuploads/" + "ios_push_notif_conf" + File.separator + id + File.separator;
                        System.out.println(" Path : " + absolutePath);
                        if ("Production".equalsIgnoreCase(sandboxProduction) && "Production".equalsIgnoreCase(typeUse)) {
                            System.out.println("--> " + sandboxProduction);
                            certFile = absolutePath + rs.getString("c_cert_file_prod");
                            iosCertPassword = rs.getString("c_pass_cert_prod");
                            
                            System.out.println("--> " + iosCertPassword);
                            System.out.println("--> " + certFile);

                            if ((certFile != null && !"".equals(certFile))) {
                                servicePartner = APNS.newService().withCert(certFile, iosCertPassword).withProductionDestination().build();
                            }

                            System.out.println("Go to production");
                        } else if ("SandBox".equalsIgnoreCase(sandboxProduction) && "SandBox".equalsIgnoreCase(typeUse)) {
                            certFile = absolutePath + rs.getString("c_cert_file_sand");
                            iosCertPassword = rs.getString("c_pass_cert_sand");

                            System.out.println("--> " + certFile);
                            if ((certFile != null && !"".equals(certFile))) {
                                servicePartner = APNS.newService().withCert(certFile, iosCertPassword).withSandboxDestination().build();
                            }

                            System.out.println("Go to sandbox");
                        }
                    }
                }
            }
            if ("Yes".equalsIgnoreCase(isIOSPNSEnabledPartner)) {
                if (getGcmId().length() > 0 && servicePartner != null) {
                    sendToIos(servicePartner);
                }
            }
            
        } catch (SQLException | RuntimeIOException | InvalidSSLConfig ex ) {
            LogUtil.info(getClass().getName(), "Exception = " + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(iOSNotificationHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
    public void sendGeneralNotification(String gcimID){
        this.setGcmId(gcimID);
        this.setTitle("Notification");
        this.setBody("A new version of MMA application is available, please update the application from AppStore");
        this.setEventId("");
        this.setType("GENERAL");
        this.prepareAPNSForIOSPartner();
        try {
            this.sendToIos(servicePartner);
        } catch (IOException ex) {
            Logger.getLogger(iOSNotificationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
