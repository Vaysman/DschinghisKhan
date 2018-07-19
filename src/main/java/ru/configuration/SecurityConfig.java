package ru.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.configuration.authentication.CustomAuthenticationManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private CustomAuthenticationManager customAuthenticationManager;



    @Autowired
    public SecurityConfig(CustomAuthenticationManager customAuthenticationManager) {
        this.customAuthenticationManager = customAuthenticationManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/").authenticated()
                .antMatchers("/main").hasAnyRole("ADMIN","DISPATCHER")
                .antMatchers("/routes").authenticated()
                .antMatchers("/orders").authenticated()
                .antMatchers("/profile").authenticated()
                .antMatchers("/info").authenticated()
                .antMatchers("/api").permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login-error")
                .defaultSuccessUrl("/profile")
                .successForwardUrl("/profile")
                .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/login");
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return customAuthenticationManager;
    }
}
