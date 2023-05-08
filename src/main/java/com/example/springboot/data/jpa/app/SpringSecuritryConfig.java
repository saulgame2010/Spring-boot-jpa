package com.example.springboot.data.jpa.app;

import com.example.springboot.data.jpa.app.auth.filter.JWTAuthenticationFilter;
import com.example.springboot.data.jpa.app.auth.filter.JWTAuthorizationFilter;
import com.example.springboot.data.jpa.app.auth.handler.LoginSuccessHandler;
import com.example.springboot.data.jpa.app.models.services.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SpringSecuritryConfig implements WebMvcConfigurer {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JpaUserDetailsService userDetailService;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void userDetailsService(AuthenticationManagerBuilder build) throws Exception {
        build.userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/", "/css/**", "/js/**", "/image/**", "/listar**","/locale").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationConfiguration.getAuthenticationManager()))
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
