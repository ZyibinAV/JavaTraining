package com.homeapp.javatraining.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AvatarService {
    private static final Logger log = LoggerFactory.getLogger(AvatarService.class);
    private static final String UPLOAD_DIR_NAME = "avatar-uploads";

    public List<String> getAvailableAvatars() {
        log.debug("Loading available avatars list");
        List<String> avatars = new ArrayList<>();
        
        // Default avatars
        avatars.add("/resources/avatars/default/avatar1.png");
        avatars.add("/resources/avatars/default/avatar2.png");
        avatars.add("/resources/avatars/default/avatar3.png");
        avatars.add("/resources/avatars/default/avatar4.png");
        avatars.add("/resources/avatars/default/avatar5.png");
        avatars.add("/resources/avatars/default/avatar6.png");
        
        return avatars;
    }

    public List<String> getAvailableAvatars(String userAvatarPath) {
        List<String> avatars = getAvailableAvatars();
        
        // Add user's uploaded avatar if it exists
        if (userAvatarPath != null && userAvatarPath.startsWith("/uploads/")) {
            avatars.add(userAvatarPath);
        }
        
        return avatars;
    }

    public boolean isUploadedAvatar(String avatarPath) {
        return avatarPath != null && avatarPath.startsWith("/uploads/");
    }
}
