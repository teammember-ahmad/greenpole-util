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
        //String localFileName = "C:\\Users\\Akinwale.Agbaje\\Documents\\MONEY MARKET\\Div 14.xlsx";
        String localFileName = "C:\\Users\\emmanuel.idoko\\Documents\\development tools\\BOND_MODULE_UAT_Issue_Tracker.xls";
        //String hostDir = "/etc/Greenpole/Reports";

        FileTransfer ftpobj = new FileTransfer(username, password, servername, port);
        //ftpobj.uploadFTPFile(localFileName);
        ftpobj.downloadFTPFile("192.168.10.217\\etc\\Greenpole\\Reports\\Div 14.xlsx", "C:\\Users\\emmanuel.idoko\\Documents");
    }
}
