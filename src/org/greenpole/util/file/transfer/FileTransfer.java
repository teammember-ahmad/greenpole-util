/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.file.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.greenpole.util.properties.GreenpoleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author emmanuel.idoko
 */
public class FileTransfer {

    // Creating FTP Client instance
    FTPClient ftp = null;
    Logger logger = LoggerFactory.getLogger(FileTransfer.class);
    private final GreenpoleProperties greenProp = GreenpoleProperties.getInstance();

    // Constructor to connect to the FTP Server
    public FileTransfer() {
        try {
            int ftp_port = 0;
            try {
                ftp_port = Integer.valueOf(greenProp.getFtpPort());
            } catch (NumberFormatException nex) {
                logger.info("error thrown trying to retrieve ftp port");
                logger.error("error thrown trying to retrieve ftp port", nex);
            }
            ftp = new FTPClient();
            int reply;
            ftp.connect(greenProp.getFtpHost(), ftp_port);

            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                logger.info("Exception in connecting to FTP Server");
                throw new Exception("Exception in connecting to FTP Server");
            }
            logger.info("Connected to ftp server successfully - {} ", greenProp.getFtpUsername());
            ftp.login(greenProp.getFtpUsername(), greenProp.getFtpPassword());
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
        } catch (Exception ex) {
            logger.info("error thrown trying to connect to ftp server");
            logger.error("error thrown trying to connect to ftp server", ex);
        }

    }

    // Method to upload the File on the FTP Server
    public void uploadFTPFile(String localFileFullName, String dirToCreate) {
        try {
            File sourceFile = new File(localFileFullName);
            //InputStream input = new FileInputStream(new File(localFileFullName));
            InputStream input = new FileInputStream(sourceFile);
            String filePathOnFtpServer = "";
            boolean dirCreated;
            if (dirToCreate != null && !dirToCreate.isEmpty()) {
                dirCreated = ftp.makeDirectory(dirToCreate);
                if (dirCreated) {
                    System.out.println("Successfully created directory: " + dirToCreate);
                    logger.info("Successfully created directory: - {} ", dirToCreate);
                    filePathOnFtpServer = dirToCreate + "/" + sourceFile.getName();
                } else {
                    logger.info("Failed to create directory. See server's reply. - {} ", dirToCreate);
                    System.out.println("Failed to create directory. See server's reply.");
                }
            } else {
                filePathOnFtpServer = sourceFile.getName();
                logger.info("No directory name found for creation");
            }

            boolean done = ftp.storeFile(filePathOnFtpServer, input);
            if (done) {
                System.out.println("Transfer successful");
                logger.info("Transfer successful with transfer status as - {} ", done);
            } else {
                System.out.println("Unable to transfer the file");
                logger.info("Transfer successful with transfer status as - {} ", done);
            }

        } catch (Exception e) {
            logger.info("error in transfering file to the server. See error log");
            logger.error("error in transfering file to the server", e);

        }
    }

    // Disconnect the connection to FTP
    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                logger.info("error in disconnecting from the server. See error log");
                logger.error("error in disconnecting the server", f);

            }
        }
    }
}
