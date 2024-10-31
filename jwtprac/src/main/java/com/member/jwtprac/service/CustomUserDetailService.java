package com.member.jwtprac.service;

import com.member.jwtprac.dto.CustomUserDetails;
import com.member.jwtprac.entity.UserEntity;
import com.member.jwtprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            return new CustomUserDetails(userEntity);
        }

        return null;
    }
}
