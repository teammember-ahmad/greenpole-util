/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.email;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Akinwale Agbaje
 * Reads an email template into a string once path is provided
 */
public class TemplateReader {
    private static final Logger logger = LoggerFactory.getLogger(TemplateReader.class);
    
    public static String getTemplateContent(String template_path) {
        //read template into string format, java 8 style
        StringBuilder sb = new StringBuilder();
        logger.info("loading email template - [{}]", template_path);
        try {
            List<String> lines = Files.readAllLines(Paths.get(template_path), StandardCharsets.UTF_8);
            lines.stream().forEach((line) -> {
                sb.append(line).append("\n");
            });
        } catch (IOException ex) {
            logger.info("failed to load email template - see error log");
            logger.error("error loading email template:", ex);
        }
        return sb.toString();
    }
}
