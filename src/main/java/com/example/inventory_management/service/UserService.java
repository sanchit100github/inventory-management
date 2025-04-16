package com.example.inventory_management.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.inventory_management.model.Role;
import com.example.inventory_management.model.User;
import com.example.inventory_management.repository.RoleRepository;
import com.example.inventory_management.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User saveNewUser(User user) {
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
        
    }

    public User updateUser(User user) {
        Optional<User> user1 = userRepository.findByEmail(user.getEmail());
        if(user1.isPresent()) {
            User finaluser = user1.get();
            finaluser.setAssigned(user.getAssigned());
            finaluser.setContact(user.getContact());
            return userRepository.save(finaluser);
        }
        return null;
        
    }

    public List<User> findByAssigned(Role assigned) {
        return userRepository.findAllByAssigned(assigned);
    }

    public Optional<User> findUser(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllByActive(boolean active) {
        return userRepository.findAllByActive(active);
    }

    public void deleteUser(String email) {
        userRepository.deleteByEmailAndActive(email, true);
    }

    public List<Role> getReqRolesManager(User user) {
        List<Role> roles = roleRepository.findByNameNot("ADMIN");
        Iterator<Role> iterator = roles.iterator();
        while (iterator.hasNext()) {
            Role it = iterator.next();
            if(!it.getAddedby().getName().startsWith("ADMIN")) {
                iterator.remove(); 
            }
        }
        return roles;
    }

    public List<String> getCategories(User user) {
        List<String> categories = new ArrayList<>();
        categories.add(user.getAssigned().getName().replaceFirst("^EMPLOYEE_", ""));
        return categories;
    }

    public List<User> getAllManagers() {
        List<User> managers = userRepository.findAllByActive(true);
        Iterator<User> iterator = managers.iterator();
        while (iterator.hasNext()) {
            User it = iterator.next();
            if (!it.getAssigned().getName().startsWith("MANAGER")) {
                iterator.remove(); 
            }
        }
        return managers;
    }

    public List<User> getAllEmployees(User user) {
        List<User> employees = userRepository.findAllByActive(true);
        Iterator<User> iterator = employees.iterator();
        while (iterator.hasNext()) {
            User it = iterator.next();
            if (!it.getAssigned().getName().startsWith("EMPLOYEE")) {     
                iterator.remove(); 
            }
        }
        Iterator<User> iterator2 = employees.iterator();
        while (iterator2.hasNext()) {
            User it = iterator2.next();
            if(!it.getAssigned().getAddedby().equals(user.getAssigned())) {
                iterator2.remove(); 
            }
        }
        return employees;
    }

    public List<Role> getReqRolesEmployee(User user) {
        List<Role> roles = roleRepository.findByNameNot("ADMIN");
        Iterator<Role> iterator = roles.iterator();
        while (iterator.hasNext()) {
            Role it = iterator.next();
            if(!it.getAddedby().equals(user.getAssigned())) {
                iterator.remove(); 
            }
        }
        return roles;
    }

}
