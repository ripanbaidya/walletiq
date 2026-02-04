package com.walletiq.common.util;

import com.walletiq.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

/**
 * Utility class for accessing current authentication context.
 * <p>It helps to Decouple business logic from Spring Security, Provides
 * convenient helper methods and Type-safe access to user details</p>
 * <h3>Usage:</h3>
 * <pre>
 *     {@code
 *     @Controller
 *     @RequiredArgsConstructor
 *     public class UserController {
 *         private final AuthenticationFacade authFacade;
 *
 *          @GetMapping("/ride")
 *          public Ride createRide() {
 *              User user = authFacade.getCurrentUser();
 *              // ... business logic
 *          }
 *      }
 *     }
 * </pre>
 */
public final class AuthUtil {

    private AuthUtil() {
    }

    /**
     * Get raw authentication object
     */
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    /**
     * Get current {@code UserDetails}
     */
    public static Optional<UserDetails> getUserDetails() {
        return getAuthentication()
                .filter(auth -> auth.getPrincipal() instanceof UserDetails)
                .map(auth -> (UserDetails) auth.getPrincipal());
    }

    /**
     * Get current authenticated User
     */
    public static User getCurrentUser() {
        return getAuthentication()
                .filter(auth -> auth.getPrincipal() instanceof User)
                .map(auth -> (User) auth.getPrincipal())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );
    }

    /**
     * Get username(email) of current user
     */
    public static Optional<String> getUsername() {
        return getAuthentication()
                .filter(auth -> auth.getPrincipal() instanceof User)
                .map(auth -> (User) auth.getPrincipal())
                .map(User::getEmail);
    }

    /**
     * Check if Current request has authenticated user
     */
    public static boolean isAuthenticated() {
        return getAuthentication().map(Authentication::isAuthenticated)
                .orElse(false);
    }
}
