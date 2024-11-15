package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager, final JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/auth/signin");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            final SigninRequest req = new ObjectMapper().readValue(request.getInputStream(), SigninRequest.class);

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());
            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new AuthenticationServiceException("인증에 실패하였습니다.");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) {
        final AuthUser authUser = (AuthUser) authentication.getPrincipal();
        final String token = jwtUtil.createToken(authUser);

        response.addHeader("Authorization", token);
        response.setStatus(200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        System.out.println("fail");
    }
}


