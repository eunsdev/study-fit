package dev.euns.studyfit.global.security.service;

import dev.euns.studyfit.global.security.principal.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String userId) {
        return new CustomUserDetails(Long.valueOf(userId));
    }
}