package com.homeapp.javatraining.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AvatarService {
    private static final Logger log = LoggerFactory.getLogger(AvatarService.class);

    public List<String> getAvailableAvatars() {
        log.debug("Loading available avatars list");
        return List.of(
                "/resources/avatars/default/avatar1.png",
                "/resources/avatars/default/avatar2.png",
                "/resources/avatars/default/avatar3.png",
                "/resources/avatars/default/avatar4.png",
                "/resources/avatars/default/avatar5.png",
                "/resources/avatars/default/avatar6.png"
        );
    }
}
