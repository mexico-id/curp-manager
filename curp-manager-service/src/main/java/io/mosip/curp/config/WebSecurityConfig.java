package io.mosip.curp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/v1/curpManager/api/curp-bio-data/**").permitAll()
                .antMatchers(HttpMethod.GET, "/v1/curpManager/api/curp-bio-data/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }
}