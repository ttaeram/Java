package com.blog.blog.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccessTokenRequest {

    private String refreshToken;
}
