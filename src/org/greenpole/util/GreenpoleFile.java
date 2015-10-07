/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Akin
 * Used to create files for signatures and power of attorney documents.
 */
public class GreenpoleFile {
    private String fileName;
    private String folderPath;

    public GreenpoleFile() {
    }

    public GreenpoleFile(String fileName, String path) {
        this.fileName = fileName;
        this.folderPath = path;
    }

    public GreenpoleFile(String path) {
        this.folderPath = path;
    }
    
    /**
     * Creates a file name using random numbers and the current date-time.
     * @return the file name
     */
    private String createFileName() {
        java.util.Date date = new java.util.Date();
        Random rand = new Random();
        int randomNumber = rand.nextInt(99999999);
        return randomNumber + "_" + date.getTime();
    }
    
    /**
     * Creates a file and writes byte contents in the file.
     * @param contents the byte contents
     * @return true if file is created successfully
     * @throws FileNotFoundException if the file cannot be created
     * @throws IOException if the file cannot be read
     */
    public boolean createFile(byte[] contents) throws FileNotFoundException, IOException {
        if (folderPath != null && !"".equals(folderPath)) {
            if (fileName == null || "".equals(fileName))
                fileName = createFileName();
            File file = new File(folderPath + fileName);
            file.getParentFile().mkdirs();
            
            try (FileOutputStream fos = new FileOutputStream(file)) {
                if (!file.exists())
                    file.createNewFile();
                
                fos.write(contents);
                fos.flush();
            }
            
            return true;
        }
        return false;
    }
    
    public boolean deleteFile(String filePath) throws IOException {
        return Files.deleteIfExists(new File(filePath).toPath());
    }
    
    public void deleteFile(List<String> filePaths) throws IOException {
        for (String path : filePaths)
            Files.deleteIfExists(new File(path).toPath());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
