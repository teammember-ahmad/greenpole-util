/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.file.transfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    public FileTransfer(String username, String password, String host, int portNumber) throws IOException, Exception {
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

    }

    // Method to upload the File on the FTP Server
    public boolean uploadFTPFile(String localFileFullName) {
        boolean done = false;
        try {
            File sourceFile = new File(localFileFullName);
            InputStream input = new FileInputStream(sourceFile);
            String filePathOnFtpServer = sourceFile.getName();
            done = ftp.storeFile(filePathOnFtpServer, input);
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
            System.out.println(e);

        } finally {
            disconnect();
        }
        return done;
    }

    // Download the FTP File from the FTP Server
    public boolean downloadFTPFile(String source, String destination) throws Exception {
        boolean downloaded = false;
        String fullPath = "";
        if (destination != null && !destination.trim().isEmpty() && destination.endsWith("//")) {
            fullPath = destination + source;
        } else if (destination != null && !destination.trim().isEmpty() && !destination.endsWith("//")) {
            fullPath = destination + "//" + source;
        }
        System.out.println("Complete file name - " + fullPath);
        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            downloaded = ftp.retrieveFile(source, fos);
            if (downloaded) {
                System.out.println("File downloaded successfully.");
            } else {
                System.out.println("File download failed.");
            }
        } catch (IOException e) {
            System.out.println("Error - " + e.getMessage());
        } finally {
            disconnect();
        }
        return downloaded;
    }

    // Disconnect the connection to FTP
    private void disconnect() {
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
