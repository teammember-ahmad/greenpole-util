/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.audit;

import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale.Agbaje
 */
public class AuditLog {
    
    /**
     * Log user's activity.
     * @param userEmail the user's email
     * @param userIpAddress the user's ip address
     * @param functionPerformed the user function performed
     * @param approvalRequired if approval is required for this function
     * @param designatedApprover the designated approver for this function (if any)
     * @param isAuthorisation if the function performed is an authorisation
     * @param notificationCode the notification code
     * @param responseCode the server's response code
     * @param responseDescription the server's response description
     * @return true, if activity is logged
     */
    public static boolean logUserActivity(String userEmail, String userIpAddress, String functionPerformed, boolean approvalRequired, String designatedApprover, 
            boolean isAuthorisation, String notificationCode, int responseCode, String responseDescription) {
        GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
        org.slf4j.Logger logger = LoggerFactory.getLogger(AuditLog.class);
        boolean result = false;
        try {
            result = gq.logUserActivity(userEmail, userIpAddress, functionPerformed, approvalRequired, designatedApprover, isAuthorisation, 
                    notificationCode, responseCode, responseDescription, null, "greenpole");
        } catch (Exception ex) {
            logger.info("error thrown while logging user activity for {}. See error log", userEmail);
            logger.error("error thrown while logging user activity for " + userEmail, ex);
        }
        return result;
    }
    
    /**
     * Log user's activity.
     * @param userEmail the user's email
     * @param userIpAddress the user's ip address
     * @param functionPerformed the user function performed
     * @param approvalRequired if approval is required for this function
     * @param designatedApprover the designated approver for this function (if any)
     * @param isAuthorisation if the function performed is an authorisation
     * @param notificationCode the notification code
     * @param responseCode the server's response code
     * @param responseDescription the server's response description
     * @param referenceId the reference id
     * @return true, if activity is logged
     */
    public static boolean logErsUserActivity(String userEmail, String userIpAddress, String functionPerformed, boolean approvalRequired, String designatedApprover, 
            boolean isAuthorisation, String notificationCode, int responseCode, String responseDescription, String referenceId) {
        GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();
        org.slf4j.Logger logger = LoggerFactory.getLogger(AuditLog.class);
        boolean result = false;
        try {
            result = gq.logUserActivity(userEmail, userIpAddress, functionPerformed, approvalRequired, designatedApprover, isAuthorisation, 
                    notificationCode, responseCode, responseDescription, referenceId, "ers");
        } catch (Exception ex) {
            logger.info("error thrown while logging user activity for {}. See error log", userEmail);
            logger.error("error thrown while logging user activity for " + userEmail, ex);
        }
        return result;
    }
}
