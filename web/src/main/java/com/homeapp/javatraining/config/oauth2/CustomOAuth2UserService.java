package com.homeapp.javatraining.config.oauth2;


import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String githubId = String.valueOf(attributes.get("id"));
        String login = (String) attributes.get("login");
        String email = (String) attributes.get("email");
        if (email == null) {
            email = login + "@github.local";
        }

        User user = userRepository.findByGithubId(githubId).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(email).orElse(null);
        }

        if (user == null) {
            user = new User(login, "", email, Role.USER);
            user.setGithubId(githubId);
            user = userRepository.save(user);
        } else if (user.getGithubId() == null) {
            user.setGithubId(githubId);
            user = userRepository.save(user);
        }

        Map<String, Object> enhancedAttributes = new HashMap<>(attributes);
        enhancedAttributes.put("appUserId", user.getId().toString());

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                enhancedAttributes,
                "id"
        );
    }


}
