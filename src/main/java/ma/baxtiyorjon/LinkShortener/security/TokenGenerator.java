package ma.baxtiyorjon.LinkShortener.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenGenerator {

    @Value("${token.secret}")
    private String SECRET;

    @Value("${token.validity}")
    private Long VALIDITY;

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + VALIDITY);
        return Jwts.builder().
                setSubject(username).
                setIssuedAt(now).setExpiration(expireDate).
                signWith(SignatureAlgorithm.HS256, SECRET).
                compact();
    }

    public String getUsernameFromToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public Boolean validateToken(String token) {
        final Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        if (claims != null && claims.getExpiration().getTime() > new Date().getTime()) {
            return true;
        } else {
            throw new AuthenticationCredentialsNotFoundException("Token expired or incorrect");
        }
    }
}
