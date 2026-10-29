package ua.com.owu.homework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final Key signingKey;
    private final long accessLifetime;
    private final long refreshLifetime;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-lifetime-ms:900000}") long accessLifetime,
                      @Value("${app.jwt.refresh-lifetime-ms:604800000}") long refreshLifetime) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessLifetime = accessLifetime; this.refreshLifetime = refreshLifetime;
    }
    public String accessToken(UserDetails user) { return create(user, accessLifetime, "access"); }
    public String refreshToken(UserDetails user) { return create(user, refreshLifetime, "refresh"); }
    private String create(UserDetails user, long lifetime, String type) {
        Map<String, Object> claims = new HashMap<>(); claims.put("type", type);
        claims.put("roles", user.getAuthorities().stream().map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toList()));
        Date now = new Date();
        return Jwts.builder().setClaims(claims).setSubject(user.getUsername()).setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + lifetime)).signWith(signingKey, SignatureAlgorithm.HS256).compact();
    }
    public String username(String token) { return claim(token, Claims::getSubject); }
    public boolean isRefreshToken(String token) { return "refresh".equals(claim(token, c -> c.get("type", String.class))); }
    public boolean valid(String token, UserDetails user) {
        try { return username(token).equals(user.getUsername()) && !expired(token); } catch (RuntimeException e) { return false; }
    }
    private boolean expired(String token) { return claim(token, Claims::getExpiration).before(new Date()); }
    private <T> T claim(String token, Function<Claims, T> resolver) {
        return resolver.apply(Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody());
    }
}
