package com.ap.greenpole.usermodule.config;

import com.ap.greenpole.usermodule.model.GreenPoleUserDetails;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import com.ap.greenpole.usermodule.util.Helpers;
import com.ap.greenpole.usermodule.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.Jedis;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 03-Aug-20 11:56 PM
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserService userService;

    @Value("${spring.redis.ip}")
    private String redisIp;

    @Value("${spring.redis.port}")
    private int redisPort;

    Jedis jedis;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");
        String jwtToken = null;
        GreenPoleUserDetails userDetails;

        if (!WebSecurityConfig.orRequestMatcher.matches(httpServletRequest)) {
            if (requestTokenHeader != null && (requestTokenHeader.startsWith("Bearer ") || requestTokenHeader.startsWith("bearer "))) {
                jwtToken = requestTokenHeader.substring(7);
                try {
                    userDetails = jwtTokenUtil.getGreenPoleUserDetailsFromToken(jwtToken);

                    // fetch permission and cache in redis
                    if (jedis == null || !jedis.isConnected()) {
                        jedis = new Jedis(redisIp, redisPort);
                    }
                    boolean notExist = false;
                    try {
                        notExist = (jedis.get(jwtToken) == null || jedis.get(jwtToken).isEmpty());
                    } catch (Exception exception) {
                        jedis = new Jedis(redisIp, redisPort);
                    }
                    if (notExist) {
                        Optional<User> user = userService.getUserWithPermissionsAndRolesByEmail(userDetails.getEmail());
                        if (!user.isPresent()) {
                            Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED,
                                    "An error occur when trying to validate the access token, Could not get the roles and permissions");
                            return;
                        }
                        jedis.set(jwtToken, new ObjectMapper().writeValueAsString(user.get()));
                    }
                    User user = new ObjectMapper().readValue(jedis.get(jwtToken), User.class);
                    userDetails = new GreenPoleUserDetails(userDetails.getEmail(), user.getRoles(), user.getPermissions());
                    // end

                    httpServletRequest.setAttribute("ROLES", userDetails.getRoles());
                    httpServletRequest.setAttribute("PERMISSIONS", userDetails.getPermissions());
                    httpServletRequest.setAttribute("AUTHORIZATION_HEADER", requestTokenHeader);

                } catch (IllegalArgumentException | MalformedJwtException ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "Invalid Authentication token");
                    return;
                } catch (ExpiredJwtException ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "The Authentication token has expired");
                    return;
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "An error occur when trying to validate the access token");
                    return;
                }
            } else {
                Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED, "The Authentication token is missing");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    Helpers.WriteError(httpServletRequest, httpServletResponse, HttpStatus.UNAUTHORIZED,
                            "An error occur when trying to validate the access token, Could not get the roles and permissions");
                    return;
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
