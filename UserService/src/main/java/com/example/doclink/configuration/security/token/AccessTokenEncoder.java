package com.example.doclink.configuration.security.token;

public interface AccessTokenEncoder {
    String encode(AccessToken accessToken);
}
