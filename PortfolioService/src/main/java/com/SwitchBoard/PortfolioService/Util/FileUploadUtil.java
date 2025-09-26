package com.SwitchBoard.PortfolioService.Util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUploadUtil {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * Uploads a file to the specified subdirectory
     *
     * @param file the file to upload
     * @param subDir the subdirectory within the upload directory
     * @return the relative path to the file
     * @throws IOException if an error occurs during file upload
     */
    public String saveFile(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        // Create directory if it doesn't exist
        String directory = uploadDir + File.separator + subDir;
        Path directoryPath = Paths.get(directory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = directoryPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);

        // Return relative path
        return subDir + "/" + newFilename;
    }

    /**
     * Deletes a file from the upload directory
     *
     * @param relativePath the relative path to the file
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return false;
        }

        Path filePath = Paths.get(uploadDir, relativePath);
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
}
