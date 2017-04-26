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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author emmanuel.idoko
 */
public class FileTransfer {

    //Creating FTP Client instance
    private FTPClient ftp = null;
    private final Logger logger = LoggerFactory.getLogger(FileTransfer.class);
    private final String username;
    private final String password;
    private final String host;
    private final int portNumber;
    
    public FileTransfer(String username, String password, String host, int portNumber) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.portNumber = portNumber;

    }

    // Method to upload the File on the FTP Server
    public void uploadFTPFile(String localFileFullName) {
        try {

            try {
                ftp = new FTPClient();
                int reply;
                ftp.connect(host, portNumber);

                reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftp.disconnect();
                    logger.info("Exception in connecting to FTP Server");
                    throw new Exception("Exception in connecting to FTP Server");
                }
                logger.info("Connected to ftp server successfully - {} ", username);
                ftp.login(username, password);
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalPassiveMode();
            } catch (Exception ex) {
                logger.info("error thrown trying to connect to ftp server");
                logger.error("error thrown trying to connect to ftp server", ex);
            }

            File sourceFile = new File(localFileFullName);
            InputStream input = new FileInputStream(sourceFile);
            String filePathOnFtpServer = sourceFile.getName();
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
