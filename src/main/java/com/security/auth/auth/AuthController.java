package com.security.auth.auth;

import com.security.auth.model.User;
import com.security.auth.dto.LoginRequest;
import com.security.auth.exception.CustomException;
import com.security.auth.dto.SignupRequest;

import com.security.auth.service.impl.AuthServiceImpl;
import com.security.auth.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long jwtRefreshExpiration;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@Valid @NonNull @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            jwtTokenProvider.setCookies(response, accessToken, refreshToken);

            return ResponseEntity.ok(tokens);
        }  catch (CustomException e){
            throw e;
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@Valid @NonNull @RequestBody SignupRequest signUpRequest){
        try{
            authService.registerUserByCredentials(signUpRequest);
            return  ResponseEntity.ok("User registered successfully!");
        } catch (CustomException e){
            throw e;
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshTokens(@NotNull @CookieValue("refreshToken") String refreshToken, HttpServletResponse response){
        try {
            authService.refreshTokens(response, refreshToken);
            return ResponseEntity.ok("Tokens refreshed successfully!");
        } catch (CustomException e){
            throw e;
        } catch (ExpiredJwtException e) {
            throw new CustomException("Token has expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
