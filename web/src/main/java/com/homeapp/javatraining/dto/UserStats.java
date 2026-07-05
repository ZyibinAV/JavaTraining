package com.homeapp.javatraining.dto;

import com.homeapp.javatraining.model.User;

public class UserStats extends BaseStats {

    private final String username;

    public UserStats(User user) {
        this.username = user.getUsername();
    }

    public String getUsername() {
        return username;
    }

}
