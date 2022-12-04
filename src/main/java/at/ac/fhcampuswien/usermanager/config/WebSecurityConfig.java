package at.ac.fhcampuswien.usermanager.config;

import at.ac.fhcampuswien.usermanager.security.AuthTokenFilter;
import at.ac.fhcampuswien.usermanager.service.AuthTokenService;
import at.ac.fhcampuswien.usermanager.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurityConfig(
            UserService userService,
            AuthTokenService authTokenService,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.authTokenService = authTokenService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .anonymous()
                .disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v*/auth/**", "/api/v*/user", "/api/v*/user/**")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/v*/auth/logout"))
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers("/api/v*/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        http.addFilterBefore(
                authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/v*/api-docs/**", "/configuration/ui", "/swagger*/**", "/webjars/**")
                .and()
                .ignoring()
                .antMatchers(HttpMethod.POST, "/api/v*/auth/**", "/api/v*/user", "/api/v*/user/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(userService, authTokenService);
    }
}
