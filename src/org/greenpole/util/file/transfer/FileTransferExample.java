/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.file.transfer;

/**
 *
 * @author emmanuel.idoko
 */
public class FileTransferExample {

    public static void main(String[] args) throws Exception {
        String servername = "192.168.10.217";
        int port = 21;
        String username = "Administrator";
        String password = "Password@1";
        FileTransfer ftpobj = new FileTransfer(username, password, servername, port);
        
        //testing upload method
        //String localFileName = "C:\\Users\\emmanuel.idoko\\Documents\\CCNN DIV 15 NIBSS REPORT error Batch1.xlsx";
        //boolean done = ftpobj.uploadFTPFile(localFileName);
        //System.out.println("transfer status::" + done);
        
        //testing of download method
        String sourceFile = "Div 14.xlsx";
        String destFile = "C://Users//emmanuel.idoko//Documents";
        boolean done = ftpobj.downloadFTPFile(sourceFile, destFile);
        System.out.println("Download status::" + done);
    }
}
