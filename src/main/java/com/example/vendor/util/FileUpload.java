package com.example.vendor.util;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUpload {

    public String saveFile(String folder, MultipartFile multipartFile, String id) throws IOException {
        try {
            String ext = getFileExtension(multipartFile.getOriginalFilename());
            byte[] bytes = multipartFile.getBytes();
            Path path = Paths.get(folder + id + "." + ext);
            Files.write(path, bytes);
            return path.toString();
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: ");
        }
    }

    public static String getFileExtension(String fileName) {

        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

}

