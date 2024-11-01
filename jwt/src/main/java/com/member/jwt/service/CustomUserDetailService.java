package com.member.jwt.service;

import com.member.jwt.dto.CustomUserDetails;
import com.member.jwt.entity.UserEntity;
import com.member.jwt.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // DB에서 조회
        UserEntity userData = userRepository.findByUsername(username);

        if (userData != null) {

            // UserDetails에 담아서 return 하면 AuthenticationManager가 검증
            return new CustomUserDetails(userData);
        }

        return null;
    }
}
