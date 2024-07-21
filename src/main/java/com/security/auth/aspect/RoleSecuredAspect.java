package com.security.auth.aspect;

import com.security.auth.annotation.RoleSecured;
import com.security.auth.auth.JwtTokenProvider;
import com.security.auth.exception.CustomException;
import com.security.auth.model.RoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class RoleSecuredAspect {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final HttpServletRequest request;

    public RoleSecuredAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before("@annotation(roleSecured)")
    public void checkRole(RoleSecured roleSecured) {
            String token = request .getHeader("Authorization");

            if (token == null || !token.startsWith("Bearer ")) {
                throw new CustomException("Missing or invalid Authorization header", HttpStatus.FORBIDDEN);
            }

            token = token.substring(7);

            List<RoleEnum> roles = jwtTokenProvider.getRolesNamesFromToken(token);

            if(roles == null || roles.isEmpty()){
                throw new CustomException("User has no roles assigned", HttpStatus.FORBIDDEN);
            }

            boolean hasRoles = Arrays.stream(roleSecured.roles()).anyMatch(roles::contains);

            if (!hasRoles) {
                throw new CustomException("User does not have the required role", HttpStatus.FORBIDDEN);
            }
    }

}
