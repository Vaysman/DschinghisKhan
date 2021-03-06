package ru.configuration.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.dao.entity.User;
import ru.dao.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(username).orElseThrow(()-> new UsernameNotFoundException("Username not found"));
        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setPassword(user.getPassAndSalt());
        userDetails.setUsername(username);
        userDetails.setAuthority(user.getUserRole());
        return userDetails;
    }
}
