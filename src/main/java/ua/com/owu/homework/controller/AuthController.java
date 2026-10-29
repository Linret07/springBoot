package ua.com.owu.homework.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import ua.com.owu.homework.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService users;
    private final JwtService jwt;
    public AuthController(AuthenticationManager authenticationManager, UserDetailsService users, JwtService jwt) {
        this.authenticationManager = authenticationManager; this.users = users; this.jwt = jwt;
    }
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Credentials credentials) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        return tokens(users.loadUserByUsername(credentials.getUsername()));
    }
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshRequest request) {
        try {
            String token = request.getRefreshToken();
            if (token == null || !jwt.isRefreshToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            UserDetails user = users.loadUserByUsername(jwt.username(token));
            if (!jwt.valid(token, user)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            Map<String, String> result = new HashMap<>(); result.put("accessToken", jwt.accessToken(user));
            return ResponseEntity.ok(result);
        } catch (RuntimeException exception) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
    }
    private Map<String, String> tokens(UserDetails user) {
        Map<String, String> result = new HashMap<>(); result.put("accessToken", jwt.accessToken(user));
        result.put("refreshToken", jwt.refreshToken(user)); return result;
    }
    public static class Credentials {
        private String username; private String password;
        public String getUsername() { return username; } public void setUsername(String value) { username = value; }
        public String getPassword() { return password; } public void setPassword(String value) { password = value; }
    }
    public static class RefreshRequest {
        private String refreshToken;
        public String getRefreshToken() { return refreshToken; } public void setRefreshToken(String value) { refreshToken = value; }
    }
}
