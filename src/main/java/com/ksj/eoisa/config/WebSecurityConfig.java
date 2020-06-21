package com.ksj.eoisa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import com.ksj.eoisa.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/css/**", "/js/**", "/images/**", "/profile/**", "/webfonts/**", "/assets/**").permitAll()
            .antMatchers("/", "/*", "/deal/**", "/filter/**", "/rank/**", "/theme/**", "/search/**", "/fcm/**").permitAll()
            .antMatchers("/sign/verification/**", "/sign/find/**").permitAll()
            .antMatchers("/sign/form", "/sign/fail", "/sign/signin", "/sign/signup", "/sign/oauth/**").anonymous()
            .antMatchers("/sign/modify/**", "/sign/upload/**").authenticated()
            .antMatchers("/reply/list/**").permitAll()
            .antMatchers("/reply/*").authenticated()
            .antMatchers("/admin/**", "/config/**").hasAuthority("ADMIN")
            .anyRequest().denyAll();
 
        http.formLogin()
            .loginPage("/sign/form")
            .failureUrl("/sign/fail")
            .loginProcessingUrl("/sign/signin")
            .defaultSuccessUrl("/")
            .usernameParameter("username")
            .passwordParameter("password");
 
        http.logout()
            .invalidateHttpSession(true)
            .deleteCookies("JSESSION_ID")
            .logoutUrl("/sign/signout")
            .logoutSuccessUrl("/");

        http.sessionManagement()
            .maximumSessions(1)
            .expiredUrl("/");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
    
}