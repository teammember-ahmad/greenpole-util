/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.greenpole.hibernate.entity.EnvironmentalVariables;
import org.greenpole.hibernate.entity.PropertyReport;
import org.greenpole.hibernate.query.GeneralComponentQuery;
import org.greenpole.hibernate.query.factory.ComponentQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author akinwale.agbaje
 */
public class ReportProperties extends Properties {
    
    private static ReportProperties INSTANCE;
    private InputStream instream;
    private final String REPORT_HOST = "report.host";
    private final String REPORT_PORT = "report.port";
    private final String REPORT_DB = "report.db";
    private final String REPORT_USERNAME = "report.username";
    private final String REPORT_PASSWORD = "report.password";
    private final String REPORT_DEPOSITORY = "report.depository";
    private final String REPORT_QUERY = "report.query";
    private final String REPORT_DATE_FORMAT = "report.date.format";
    private static final Logger logger = LoggerFactory.getLogger(GreenpoleProperties.class);
    private final GeneralComponentQuery gq = ComponentQueryFactory.getGeneralComponentQuery();

    private ReportProperties() {
        load();
    }

    public static ReportProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReportProperties();
        }
        return INSTANCE;
    }

    /**
     * Loads a configuration file from the disk
     */
    public final void load() {
        try {
            String config_file = "report.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Loading configuration file - {}", config_file);

            boolean exists = false;
            File propFile = new File(prop_path + config_file);
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                reload();
            } else {
                exists = true;
            }

            if (exists) {
                FileInputStream loadstream = new FileInputStream(propFile);
                load(loadstream);
                loadstream.close();

                //ensure that all property keys have not been tampered with
                List<PropertyReport> all = gq.getAllReportProperty();
                for (PropertyReport g : all) {
                    boolean found = false;
                    for (Map.Entry pairs : entrySet()) {
                        String key = (String) pairs.getKey();
                        String value = (String) pairs.getValue();
                        if (g.getPropertyName().equals(key) && g.getPropertyValue().equals(value)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        reload();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            logger.info("failed to load configuration file - see error log");
            logger.error("error loading email config file:", ex);
        }
    }

    /**
     * Reloads a configuration file, getting all necessary variables from the
     * database.
     */
    public final void reload() {
        try {
            String config_file = "report.properties";
            EnvironmentalVariables ev = gq.getVariable("property.path");
            String prop_path = ev.getPath();
            logger.info("Reloading configuration file - {}", config_file);

            File propFile = new File(prop_path + config_file);
            propFile.delete();
            //if property file does not exist in designated location, create file using default file within system classpath
            if (!propFile.exists()) {
                propFile.getParentFile().mkdirs();

                instream = ReportProperties.class.getClassLoader().getResourceAsStream(config_file);
                FileOutputStream outstream = new FileOutputStream(propFile);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = instream.read(buffer)) > 0) {
                    outstream.write(buffer, 0, length);
                }

                instream.close();
                outstream.close();
            }

            FileInputStream loadstream = new FileInputStream(propFile);
            load(loadstream);
            loadstream.close();

            FileOutputStream changestream = new FileOutputStream(propFile);

            setProperty(REPORT_HOST, gq.getReportProperty(REPORT_HOST).getPropertyValue());
            setProperty(REPORT_PORT, gq.getReportProperty(REPORT_PORT).getPropertyValue());
            setProperty(REPORT_DB, gq.getReportProperty(REPORT_DB).getPropertyValue());
            setProperty(REPORT_USERNAME, gq.getReportProperty(REPORT_USERNAME).getPropertyValue());
            setProperty(REPORT_PASSWORD, gq.getReportProperty(REPORT_PASSWORD).getPropertyValue());
            setProperty(REPORT_DEPOSITORY, gq.getReportProperty(REPORT_DEPOSITORY).getPropertyValue());
            setProperty(REPORT_QUERY, gq.getReportProperty(REPORT_QUERY).getPropertyValue());
            setProperty(REPORT_DATE_FORMAT, gq.getReportProperty(REPORT_DATE_FORMAT).getPropertyValue());

            store(changestream, null);
            changestream.close();
        } catch (Exception ex) {
            logger.info("failed to reload configuration file - see error log");
            logger.error("error reloading email config file:", ex);
        }
    }
    
    public String getReportHost() {
        return getProperty(REPORT_HOST);
    }

    public String getReportPort() {
        return getProperty(REPORT_PORT);
    }

    public String getReportDb() {
        return getProperty(REPORT_DB);
    }

    public String getReportUsername() {
        return getProperty(REPORT_USERNAME);
    }

    public String getReportPassword() {
        return getProperty(REPORT_PASSWORD);
    }
    
    public String getReportDepository() {
        return getProperty(REPORT_DEPOSITORY);
    }
    
    public String getReportQuery() {
        return getProperty(REPORT_QUERY);
    }
    
    public String getReportDateFormat() {
        return getProperty(REPORT_DATE_FORMAT);
    }
    
    /**
     * Close input stream.
     */
    private void close() {
        try {
            if (instream != null) {
                instream.close();
            }
        } catch (IOException ex) {
            logger.info("failed to close configuration file input stream - see error log");
            logger.error("error closing greenpole config file input stream:", ex);
        }
    }
}
