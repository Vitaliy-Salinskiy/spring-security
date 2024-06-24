package com.security.auth.auth;

import com.security.auth.dto.LoginRequest;
import com.security.auth.repository.UserRepository;

import com.security.auth.dto.SignupRequest;
import com.security.auth.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @GetMapping("")
    public String login(){
        return "<h1>Auth</h1>";
    }

    @PostMapping("/login")
    public Map<String, String> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwt);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    @PostMapping("/sign-up")
    public String signUp(@RequestBody SignupRequest signUpRequest){
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return "Email Address already in use!";
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return  "User registered successfully!";
    }

}
