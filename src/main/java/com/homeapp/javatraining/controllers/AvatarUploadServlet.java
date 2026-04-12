package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/profile/avatar/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,  // 1MB
        maxFileSize = 5 * 1024 * 1024,  // 5MB
        maxRequestSize = 6 * 1024 * 1024 // 6MB
)

public class AvatarUploadServlet extends BaseServlet {

    private static final String UPLOAD_DIR_NAME = "avatar-uploads";
    private UserRepository userRepository;

    @Override
    protected void initializeSpecificServices() {
        this.userRepository = (UserRepository) getServletContext().getAttribute("userRepository");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("POST /profile/avatar/upload");

        User user = getCurrentUser(req);
        log.debug("User {} initiates avatar upload", user.getUsername());

        Part filePart = req.getPart("avatar");
        if (filePart == null || filePart.getSize() == 0) {
            log.warn("Пользователь {} попытался загрузить пустой файл", user.getUsername());
            resp.sendRedirect(req.getContextPath() + "/profile/avatar");
            return;
        }

        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String extension = submittedFileName.substring(submittedFileName.lastIndexOf('.'));
        String fileName = UUID.randomUUID() + extension;
        
        // Use external directory outside webapp to persist across Maven clean
        String userHome = System.getProperty("user.home");
        File uploadDir = new File(userHome, UPLOAD_DIR_NAME);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File file = new File(uploadDir, fileName);
        filePart.write(file.getAbsolutePath());

        // Store relative path for serving via servlet
        user.setAvatarPath("/uploads/" + fileName);
        userRepository.save(user);
        log.info("User {} successfully uploaded avatar: {}", user.getUsername(), fileName);
        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}

