package com.example.Chartian.utils;

import com.example.Chartian.user.UserExternalAPI;
import com.example.Chartian.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserExternalAPI userExternalAPI;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO user = userExternalAPI.findUserByEmail(username);
        if (user != null) {
            return buildUserDetails(user);
        } else {
            throw new UsernameNotFoundException("User not found with ID: " + username);
        }
    }

    public CustomUserDetails loadUserById(Long id) throws UsernameNotFoundException {
        // Handle numeric IDs by converting them to Long or Integer if necessary
        try {
            UserDTO user = userExternalAPI.findUserById(id);
            if (user != null) {
                return buildUserDetails(user);
            } else {
                throw new UsernameNotFoundException("User not found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID format: " + id);
        }
    }

    // Define the buildUserDetails method to convert User to CustomUserDetails
    private CustomUserDetails buildUserDetails(UserDTO user) {
        // Assuming user.getRole() returns a single Role object
        Role role = user.getRole();
//        Collection<? extends GrantedAuthority> authorities = Collections.singleton(role); // Wrap the single role in a collection
        // Create a list to hold granted authorities
        // Create a list to hold granted authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        try {
            // Add role with ROLE_ prefix
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
            System.out.println("After adding role: " + authorities);

            // Add permissions as granted authorities
            authorities.addAll(user.getRole().getPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .collect(Collectors.toList())); // Use Collectors.toList() for List

            System.out.println("After adding permissions: " + authorities);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return CustomUserDetails
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), authorities);
    }
}
