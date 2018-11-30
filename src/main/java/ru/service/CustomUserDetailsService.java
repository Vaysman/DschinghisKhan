package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.configuration.authentication.CustomUserDetails;
import ru.dao.entity.User;
import ru.dao.repository.UserRepository;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username).orElseThrow(()->new UsernameNotFoundException("Username not found"));
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setPassword(user.getPassAndSalt());
        userDetails.setAuthority(user.getUserRole());
        userDetails.setUsername(user.getLogin());
        return userDetails;
    }
}
