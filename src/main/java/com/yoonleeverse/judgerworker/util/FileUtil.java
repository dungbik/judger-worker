package com.yoonleeverse.judgerworker.util;

import java.io.*;
import java.nio.file.Path;

public class FileUtil {

    public static boolean makeFolder(Path basePath) {
        File folder = basePath.toFile();
        if (!folder.exists()) {
            return folder.mkdir();
        }
        return true;
    }

    public static boolean saveText(Path filePath, String text) {
        File file = filePath.toFile();
        if (!file.exists()) {
            try (FileWriter fileWriter = new FileWriter(file);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(text);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static String loadText(Path filePath) {
        File file = filePath.toFile();
        if (file.exists()) {
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader br = new BufferedReader(fileReader)) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    if (sb.length() > 0) {
                        sb.append(System.lineSeparator());
                    }
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
