/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 *
 * @author Akinwale.Agbaje
 */
public class FileTransfer {
    public static void transferFiles(File source, File destination) throws IOException {
        FileChannel inputChannel = new FileInputStream(source).getChannel();
        FileChannel outputChannel = new FileOutputStream(destination).getChannel();
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

        inputChannel.close();
        outputChannel.close();
    }
}
