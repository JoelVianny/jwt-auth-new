package org.example.authsystem.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.authsystem.service.JwtService;
import org.example.authsystem.service.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtAuthFilter  extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            final  String authHeader =  request.getHeader("Authorization");
            String token =  null;
            String username = null;
             if( authHeader != null && authHeader.startsWith("Bearer")){
                 token = authHeader.substring(7);
                 username = jwtService.extractUsername(token);
             }

             if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                 UserDetails userDetails =userDetailsService.loadUserByUsername(username);
                 if(jwtService.validateToken(token,userDetails)){
                     var authToken =  new UsernamePasswordAuthenticationToken(userDetails , null ,
                             userDetails.getAuthorities());
                     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                     SecurityContextHolder.getContext().setAuthentication(authToken);

                 }
             }

             filterChain.doFilter(request,response);
    }
}
