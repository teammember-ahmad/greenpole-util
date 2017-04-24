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
        String servername = "192.168.10.150";
        int port = 21;
        String username = "emmanuel.idoko";
        String password = "Desterity8080&$";
        String localFileName = "C:\\Users\\emmanuel.idoko\\Documents\\myoutput.txt";
        String hostDir = "/Tester";

        FileTransfer ftpobj = new FileTransfer(username, password, servername, port);
        ftpobj.uploadFTPFile(localFileName, hostDir);
    }
}
