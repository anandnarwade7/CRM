package com.crm.security;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import com.crm.user.User;
import com.crm.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	public JWTAuthorizationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
		super();
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		  Map<String, Object> errorDetails = new HashMap<>();
//		
//		try {
//            String accessToken = jwtUtil.resolveToken(request);
//            if (accessToken != null) {
//                Claims claims = jwtUtil.resolveClaims(request);
//
//                if (claims != null && jwtUtil.validateClaims(claims)) {
//                    String email = claims.getSubject();
//                    String role = claims.get("role", String.class);
//                    
//                    Admin admin ;
//                    if (role.equalsIgnoreCase("Admin")) {
//                    	admin= adminRepository.findByEmail(email);
//					}
//                    // Fetch the user from the database to get the roles
//
//                    if (user != null) {
////                        Role roles = user.getRoles();
////
////                        // Prefix roles with "ROLE_" if necessary
////                        SimpleGrantedAuthority authorities = new SimpleGrantedAuthority("ROLE_" + roles.getName());
////
////                        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
////                        SecurityContextHolder.getContext().setAuthentication(authentication);
//                    } else {
//                        // Handle the case where the user is not found in the database
//                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                        errorDetails.put("message", "User not found");
//                        objectMapper.writeValue(response.getWriter(), errorDetails);
//                        return;
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            errorDetails.put("message", "Authentication Error");
//            errorDetails.put("details", e.getMessage());
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//            objectMapper.writeValue(response.getWriter(), errorDetails);
//
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");
		String role = null;
		String jwt = null;

		try {
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				jwt = authorizationHeader.substring(7);
				role = jwtUtil.extractRole(jwt); // Extract role from JWT
				String email = jwtUtil.extractEmail(jwt); // Extract email (subject) from JWT

				if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = null;

					// Check the role and call the respective repository
					if ("ADMIN".equalsIgnoreCase(role)) {
						User admin = userRepository.findByEmail(email);
						if (admin != null) {
							userDetails = new CustomUserDetails(admin); // Convert to UserDetails
						}
					} else if ("SALES".equalsIgnoreCase(role)) {
						User user = userRepository.findByEmail(email);
						if (user != null) {
							userDetails = new CustomUserDetails(user); // Convert to UserDetails
						}
					} else if ("CRM".equalsIgnoreCase(role)) {
						User user = userRepository.findByEmail(email);
						if (user != null) {
							userDetails = new CustomUserDetails(user); // Convert to UserDetails
						}
					}

					if (userDetails != null && jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
						// Set authentication in the security context
						UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					}
				}
			}
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Unauthorized: " + e.getMessage());
			return;
		}

		chain.doFilter(request, response);
	}

}
