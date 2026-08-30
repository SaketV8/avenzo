package com.maurya.avenzo.security;

import com.maurya.avenzo.entity.UserEntity;
import com.maurya.avenzo.exception.ApiException;
import com.maurya.avenzo.exception.ErrorCode;
import com.maurya.avenzo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                            log.info("🐸 USER NOT FOUND: {}", username);
                            /*return new ApiException(ErrorCode.USER_NOT_FOUND);*/
                            // will catch it later in the login service
                            return new UsernameNotFoundException("User not found");
                        }
                );
//                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "User not found with Email: " + username));

        /*return User.builder()
                // .username(user.getName())
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();*/

        return new CustomUserDetails(user);
    }
}
