package com.security.auth.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GitHubEmail {
    private String email;
    private boolean primary;
    private boolean verified;
}
