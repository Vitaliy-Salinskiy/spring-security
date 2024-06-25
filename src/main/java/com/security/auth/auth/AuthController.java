package com.security.auth.auth;

import com.security.auth.dto.LoginRequest;
import com.security.auth.model.RoleEnum;
import com.security.auth.repository.UserRepository;
import com.security.auth.repository.RoleRepository;
import com.security.auth.dto.SignupRequest;
import com.security.auth.model.User;
import com.security.auth.model.Role;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @GetMapping()
    public String login(){
        return "<h1>Auth</h1>";
    }

    @PostMapping("/login")
    public Map<String, String> authenticateUser(@NonNull @RequestBody LoginRequest loginRequest, HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        setCookies(response, accessToken, refreshToken);

        return tokens;
    }

    @PostMapping("/sign-up")
    public String signUp(@NonNull @RequestBody SignupRequest signUpRequest){
        if(userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return "Email Address already in use!";
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Role defaultRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));

        userRepository.save(user);

        return  "User registered successfully!";
    }

    private void setCookies (@NonNull HttpServletResponse response, String accessToken, String refreshToken){
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) (jwtExpiration / 1000));

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (jwtRefreshExpiration / 1000));

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

}
