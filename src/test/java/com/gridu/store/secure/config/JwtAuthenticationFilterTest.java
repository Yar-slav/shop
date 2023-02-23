package com.gridu.store.secure.config;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gridu.store.model.UserEntity;
import com.gridu.store.model.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testDoFilterInternal_WithValidToken_ShouldAuthenticateUser() throws Exception {
        String token = "valid_token";
        String userEmail = "test@example.com";
        UserDetails userDetails = new UserEntity(1L, userEmail, "passwordEncode", UserRole.USER, null);
        SecurityContextHolder.getContext().setAuthentication(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(userEmail);
        when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, Mockito.times(1)).extractUsername(token);
        verify(userDetailsService, Mockito.times(1)).loadUserByUsername(userEmail);
        verify(jwtService, Mockito.times(1)).isTokenValid(token, userDetails);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNotNull(authentication);
        Assertions.assertEquals(userDetails, authentication.getPrincipal());
    }

    @Test
    public void testDoFilterInternal_WithInvalidToken_ShouldNotAuthenticateUser() throws Exception {
        String token = "invalid_token";
        SecurityContextHolder.getContext().setAuthentication(null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, Mockito.times(1)).extractUsername(token);
        verify(userDetailsService, Mockito.never()).loadUserByUsername(Mockito.anyString());
        verify(jwtService, Mockito.never()).isTokenValid(Mockito.anyString(), Mockito.any());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNull(authentication);
    }

    @Test
    public void testDoFilterInternal_WithoutAuthorizationHeader_ShouldNotAuthenticateUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, Mockito.never()).extractUsername(Mockito.anyString());
        verify(userDetailsService, Mockito.never()).loadUserByUsername(Mockito.anyString());
        verify(jwtService, Mockito.never()).isTokenValid(Mockito.anyString(), Mockito.any());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertNull(authentication);
    }
}