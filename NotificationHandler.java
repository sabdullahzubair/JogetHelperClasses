/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics.helper;

//import java.util.Map;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.apps.app.dao.PluginDefaultPropertiesDao;
import org.joget.apps.app.lib.EmailTool;
import org.joget.apps.app.model.PluginDefaultProperties;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.property.service.PropertyUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author syeda
 */
public class NotificationHandler {
    
    private String footer = "<p>Thank You</p>"
                + "<p>MALAYSIAN MEDICAL ASSOCIATION</p>"
                + "<p>Tel: +603-4041 1375</p>"
                + "<p>Fax: +603-4041 8187 / +603-4041 9929</p>"
                + "<p>Email: membership@mma.org.my</p>"
                + "<p>Web: http://www.mma.org.my/</p>"
                + "<a href=\"https://www.facebook.com/malaysianmedicalassociation\">" +
                    "<img src=\"http://membership.mma.org.my/facebook.png\" alt=\"Facebook\">"
                + "</a>"
                + "<a href=\"https://twitter.com/MysMedicalAssn\">" +
                    "<img src=\"http://membership.mma.org.my/twitter.png\" alt=\"twitter\">"
                + "</a>"
                + "<a href=\"https://plus.google.com/+MmaOrgMy\">" +
                    "<img src=\"http://membership.mma.org.my/googleplus.png\" alt=\"GooglePlus\">"
                + "</a>"
                + "<a href=\"https://www.linkedin.com/company/malaysian-medical-association\">" +
                    "<img src=\"http://membership.mma.org.my/linkedin.png\" alt=\"linkedin\">"
                + "</a>"
                + "<a href=\"https://www.youtube.com/c/MmaOrgMy\">" +
                    "<img src=\"http://membership.mma.org.my/youtube.png\" alt=\"youtube\">"
                + "</a>"
                + "<a href=\"http://www.mma.org.my\">" +
                    "<img src=\"http://membership.mma.org.my/mma.png\" alt=\"MMA\">"
                + "</a>";
    
    private QueryHandler qh = new QueryHandler();
    
    public void sendVerificationCode(String id, String email, String verificationCode){
        String subject = "Email Verification";
        String content = "<p>Dear User,</p>" +
                "<p>In order to activate your account Please click <a href=\"http://membership.mma.org.my/jw/web/userview/mma_cpd/cpd/_/verify_email?"
                + "id="+ id +"&vc="+ verificationCode +"\">here</a></p>";
        JSONObject contentJSON = new JSONObject();
        try {
            contentJSON.put("content", content);
        } catch (JSONException ex) {
            Logger.getLogger(NotificationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        if(isEmail(email)){
            sendEmail(email, "","",subject,content);
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "1"}, null);
        }else{
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "0"}, null);
        }
    }
    
    
    public void sendVerificationCodeUser(String email, String verificationCode){
        String subject = "Email Verification";
        String content = "<p>Dear User,</p>" +
                "<p>In order to activate your account Please click <a href=\"http://membership.mma.org.my/jw/web/json/plugin/org.joget.cpdWebServices.webservices/service?method=activateAccount&email="
                + email + "&code="+ verificationCode +"\">here</a></p>";
            
        if(isEmail(email)){
            sendEmail(email, "","",subject,content);
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "1"}, null);
        }else{
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "0"}, null);
        }
    }
    
    
    public void sendEventApproved(String email){
        String subject = "Event Approved";
        String content = "Dear CPD Provider," +
                "<p>Your event has been approved by the Admin.</p></br></br>";
            
        if(isEmail(email)){
            sendEmail(email, "","",subject,content);
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "1"}, null);
        }else{
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "0"}, null);
        }
    }
    
    public void sendProviderAccountApproved(String email){
        String subject = "Account Approved";
        String content = "Dear CPD Provider," +
                "<p>Your Account has been approved by the Admin.</p></br></br>";
            
        if(isEmail(email)){
            sendEmail(email, "","",subject,content);
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "1"}, null);
        }else{
            qh.insertInto("cpd_sent_emails", new String[]{"to","from", "cc", "bcc", "subject", "content", "successful"},
                new String[]{email,"", "", "", subject, content + footer, "0"}, null);
        }
    }
    
    
    private void sendEmail(String emailId, String cc, String bcc, String subject, String content){
        EmailTool et = new EmailTool();
        PluginDefaultPropertiesDao dao = (PluginDefaultPropertiesDao) AppUtil.getApplicationContext().getBean("pluginDefaultPropertiesDao");
        PluginDefaultProperties pluginDefaultProperties = dao.loadById("org.joget.apps.app.lib.EmailTool", AppUtil.getCurrentAppDefinition());
        Map properties = PropertyUtil.getPropertiesValueFromJson(pluginDefaultProperties.getPluginProperties());
        properties.put("toSpecific", emailId);
        properties.put("cc", cc);
        properties.put("bcc", bcc);
        properties.put("subject", subject);
        properties.put("message", content + footer);
        et.execute(properties);
    }
    
    
    private static boolean isEmail(String email)
    {
        if (email == null) {
          return false;
        }
        // Assigning the email format regular expression
        String emailPattern = "^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})";
        return email.matches(emailPattern);
    }
}
