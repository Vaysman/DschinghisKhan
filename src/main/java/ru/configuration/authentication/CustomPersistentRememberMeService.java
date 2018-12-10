package ru.configuration.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import ru.util.generator.RandomStringGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.Date;

@Service
public class CustomPersistentRememberMeService implements RememberMeServices {

    private final DataSource dataSource;

    private final PersistentTokenRepository persistentTokenRepository;

    private final UserDetailsService userDetailsService;


    @Autowired
    public CustomPersistentRememberMeService(DataSource dataSource, PersistentTokenRepository persistentTokenRepository, UserDetailsService userDetailsService) {
        this.dataSource = dataSource;
        this.persistentTokenRepository = persistentTokenRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("remember-me")) {
                PersistentRememberMeToken rememberMeToken = persistentTokenRepository.getTokenForSeries(cookie.getValue());
                if (rememberMeToken != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(rememberMeToken.getUsername());

                    Authentication authToken = new RememberMeAuthenticationToken(rememberMeToken.getTokenValue(), rememberMeToken.getUsername(), userDetails.getAuthorities());
                    persistentTokenRepository.updateToken(cookie.getValue(), rememberMeToken.getTokenValue(), new Date());

                    return authToken;
                } else {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                }
            }
        }
        return null;
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("remember-me")) {
                cookie.setValue("");
                cookie.setMaxAge(0);
            }
        }


    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
        if (request.getParameterMap().get("remember-me")!=null && request.getParameterMap().get("remember-me").equals("on")) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("remember-me")) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                }
            }
            String login = (String) successfulAuthentication.getPrincipal();
            String series = RandomStringGenerator.randomAlphaNumeric(64);
            String value = RandomStringGenerator.randomAlphaNumeric(64);
            PersistentRememberMeToken token = new PersistentRememberMeToken(login, series, value, new Date());
            persistentTokenRepository.createNewToken(token);
            Cookie cookie = new Cookie("remember-me", series);
            //valid for 30 days
            cookie.setMaxAge(((60 * 60) * 24) * 30);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

    }
}
