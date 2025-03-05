// package com.example.inventory_management.config;

// import com.example.inventory_management.model.User;
// import com.example.inventory_management.repository.UserRepository;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import com.example.inventory_management.model.Role;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.util.Optional;
// import java.util.Set;

// @Component
// public class RoleAuthorizationFilter extends OncePerRequestFilter {

//     private final UserRepository userRepository;

//     public RoleAuthorizationFilter(UserRepository userRepository) {
//         this.userRepository = userRepository;
//     }

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//             throws ServletException, IOException {

//         // Get authenticated user
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//         if (authentication == null || authentication.getName() == null) {
//             chain.doFilter(request, response);
//             return;
//         }

//         // Fetch user from DB
//         Optional<User> userOptional = userRepository.findByEmailAndActive(authentication.getName(), true);
//         if (userOptional.isEmpty()) {
//             response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: User not found");
//             return;
//         }

//         User user = userOptional.get();
//         Role role = user.getAssigned(); // Get roles assigned to the user

//         // Define role-based access rules
//         if (request.getRequestURI().startsWith("/employee/")) {
//             if (!hasRole(roles, "EMPLOYEE")) {
//                 response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Requires EMPLOYEE role");
//                 return;
//             }
//         } else if (request.getRequestURI().startsWith("/manager/")) {
//             if (!hasRole(roles, "MANAGER")) {
//                 response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Requires MANAGER role");
//                 return;
//             }
//         } else if (request.getRequestURI().contains("/admin/")) {
//             if (!hasRole(roles, "ADMIN")) {
//                 response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Requires ADMIN role");
//                 return;
//             }
//         }

//         chain.doFilter(request, response); // Proceed if role is valid
//     }

//     private boolean hasRole(Set<Role> roles, String requiredRole) {
//         return roles.stream().anyMatch(role -> role.getName().startsWith(requiredRole));
//     }
// }
