package com.docklink.apigateway.configuration.security.token.impl;

import com.docklink.apigateway.configuration.security.token.AccessToken;
import com.docklink.apigateway.configuration.security.token.AccessTokenDecoder;
import com.docklink.apigateway.configuration.security.token.exception.InvalidAccessTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.List;

@Service
public class AccessTokenEncoderDecoderImpl implements AccessTokenDecoder {
    private final Key key;

    public AccessTokenEncoderDecoderImpl(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public AccessToken decode(String accessTokenEncoded) {
        try {
            Jwt<?, Claims> jwt = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(accessTokenEncoded);
            Claims claims = jwt.getBody();

            List<String> roles = claims.get("roles", List.class);
            Long userId = claims.get("userId", Long.class);

            return new AccessTokenImpl(claims.getSubject(), userId, roles);
        } catch (JwtException e) {
            throw new InvalidAccessTokenException(e.getMessage());
        }
    }
}
