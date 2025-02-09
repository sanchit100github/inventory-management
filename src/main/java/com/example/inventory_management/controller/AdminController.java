package com.example.inventory_management.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory_management.model.AuditLog;
import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.AuditLogRepository;
import com.example.inventory_management.repository.RoleRepository;
import com.example.inventory_management.repository.UserRepository;
import com.example.inventory_management.service.AuditLogService;
import com.example.inventory_management.service.UserService;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuditLogRepository auditLogRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    UserService userService;

    @GetMapping("/getmanagerroles") 
    public ResponseEntity<?> getRoles() {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().equals("ADMIN")) {
                    hasRole = true;
                    break;
                }
            }
            if(hasRole) {
                List<String> roles = userService.getReqRoles(user.get());
                return new ResponseEntity<>(roles, HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PostMapping("/addmanager")
    public ResponseEntity<?> addManager(@RequestBody User user1) {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().equals("ADMIN")) {
                    hasRole = true;
                    break;
                }
            }
            if(hasRole) {
                if(userRepository.findByEmailAndActive(user1.getEmail(), true).isPresent()) {
                    return new ResponseEntity<>("Manager with this email already exists", HttpStatus.CONFLICT);
                }
                boolean test = userService.saveNewUser(user1);
                if(test) {
                    AuditLog log = new AuditLog(name, "ADD", "Added " + user1.getEmail(), List.of("ADMIN"));
                    auditLogService.saveAudit(log);
                    return new ResponseEntity<>("Manager has been added", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager was not added", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @PutMapping("/updatemanager")
    public ResponseEntity<?> updateManager(@RequestBody User user1) {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().equals("ADMIN")) {
                    hasRole = true;
                    break;
                }
            }
            if(hasRole) {
                if(!userRepository.findByEmailAndActive(user1.getEmail(), true).isPresent()) {
                    return new ResponseEntity<>("Manager with this email does not exist", HttpStatus.NOT_FOUND);
                }
                boolean test = userService.updateUser(user1);
                if(test) {
                    AuditLog log = new AuditLog(name, "UPDATE", "Updated " + user1.getEmail(), List.of("ADMIN"));
                    auditLogRepository.save(log);
                    return new ResponseEntity<>("Manager has been updated", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager was not updated", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getmanager")
    public ResponseEntity<?> findManager(@RequestParam String email) {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().equals("ADMIN")) {
                    hasRole = true;
                    break;
                }
            }
            if(hasRole) {
                Optional<User> user1 = userService.findUser(email);
                if(user1.isPresent()) {
                    return new ResponseEntity<>(user1, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager with this email does not exist", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getallmanagers")
    public ResponseEntity<?> findAllManagers() {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        List<User> userList;
        if(user.isPresent()) {
            for(Role it : user.get().getRoles()) {
                if(it.getName().equals("ADMIN")) {
                    hasRole = true;
                    break;
                }
            }
            if(hasRole) {
                userList = userService.getAllByActive(true);
                if(userList.isEmpty()) {
                    return new ResponseEntity<>("No manager found", HttpStatus.NOT_FOUND);
                }
                return new ResponseEntity<>(userList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/deletemanager")
    public ResponseEntity<?> deleteManager(@RequestParam String email) {
        boolean hasRole = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmailAndActive(name, true);
        
        if (user.isPresent()) {
            for (Role role : user.get().getRoles()) {
                if (role.getName().equals("ADMIN")) {
                    hasRole = true;
                    break;
                }
            }
            if (hasRole) {
                Optional<User> manager = userRepository.findByEmailAndActive(email, true);
                
                if (manager.isPresent()) {
                    User managerToUpdate = manager.get();
                    managerToUpdate.setActive(false);
                    userRepository.save(managerToUpdate);
                    
                    AuditLog log = new AuditLog(name, "DEACTIVATE", "Deactivated " + managerToUpdate.getEmail(), List.of("ADMIN"));
                    auditLogRepository.save(log);

                    return new ResponseEntity<>("Manager deactivated", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Manager not found", HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>("Access is denied", HttpStatus.FORBIDDEN);
    }
}
