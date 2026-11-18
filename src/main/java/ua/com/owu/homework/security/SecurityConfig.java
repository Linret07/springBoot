package ua.com.owu.homework.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration @EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtAuthenticationFilter jwtFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean public UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode("admin")).roles("ADMIN").build(),
                User.withUsername("manager").password(encoder.encode("manager")).roles("MANAGER").build(),
                User.withUsername("user").password(encoder.encode("user")).roles("USER").build());
    }
    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(users(passwordEncoder()))
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/uploads/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(),
                HttpMethod.PUT.name(), HttpMethod.DELETE.name()));
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
            .antMatchers("/auth/**", "/error").permitAll()
            .antMatchers(HttpMethod.GET, "/cars/**").hasAnyRole("ADMIN", "MANAGER", "USER")
            .antMatchers(HttpMethod.POST, "/cars/**").hasAnyRole("ADMIN", "MANAGER")
            .antMatchers(HttpMethod.PUT, "/cars/**").hasRole("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/cars/**").hasRole("ADMIN")
            .anyRequest().authenticated().and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
