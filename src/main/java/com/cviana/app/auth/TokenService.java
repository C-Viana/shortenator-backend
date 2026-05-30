package com.cviana.app.auth;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cviana.app.auth.dto.TokenDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
	
	@Value("${api.security.token.secret}")
    private String secretKey;
	
	@Value("${api.security.token.expiry}")
    private long ACCESS_DURATION_IN_MINUTES;
	
	@Value("${api.security.token.refresh}")
    private long REFRESH_DURATION_IN_MINUTES;
    
    private Instant startTime;
    private Instant endTime;
    
    private SecretKey getSignKey() {
    	String encodedSecret = Base64.getEncoder().encodeToString(secretKey.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(encodedSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    private TokenDto createAccessToken(Map<String, Object> claims, String email) {
    	startTime = Instant.now(Clock.systemDefaultZone());
    	endTime = Instant.from(startTime).plus(ACCESS_DURATION_IN_MINUTES, ChronoUnit.MINUTES);
    	String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    	
    	String accessToken = Jwts.builder()
    			.issuer(issuerUrl)
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(startTime))
                .expiration(Date.from(endTime))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    	String refreshToken = createRefreshToken(claims, email, startTime);
    	
		return new TokenDto("Bearer", accessToken, refreshToken, LocalDateTime.ofInstant(endTime, ZoneId.of("GMT-3")));
    }
    
    private String createRefreshToken(Map<String, Object> claims, String email, Instant startTime) {
    	endTime = Instant.from(startTime).plus(REFRESH_DURATION_IN_MINUTES, ChronoUnit.MINUTES);
    	
		return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(Date.from(startTime))
                .expiration(Date.from(endTime))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }
    
    public TokenDto generateToken(String email) {
        Map<String, Object> claims = new HashMap<String, Object>();
        return createAccessToken(claims, email);
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
    	return Jwts.parser()
    			.verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
