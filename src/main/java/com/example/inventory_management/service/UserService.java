package com.example.inventory_management.service;

import java.util.ArrayList;
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

    public boolean saveNewUser(User user) {
        try {
            if (roleRepository.findByName(user.getAssigned().getName()).isEmpty()) {
                Role role = new Role();
                role.setName(user.getAssigned().getName());
                roleRepository.save(role);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        } 
        catch (Exception e) {
            e.printStackTrace();  
            return false;
        }
    }

    public boolean updateUser(User user) {
        try {
            Optional<User> user1 = userRepository.findById(user.getId());
            if (roleRepository.findByName(user.getAssigned().getName()).isEmpty()) {
                Role role = new Role();
                role.setName(user.getAssigned().getName());
                roleRepository.save(role);
            }
            user.setAssigned(user.getAssigned());
            if(!user1.get().getPassword().equals(passwordEncoder.encode(user.getPassword()))) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(user);
            return true;
        } 
        catch (Exception e) {
            e.printStackTrace();  
            return false;
        }
    }

    public List<User> findByAssigned(Role assigned) {
        return userRepository.findAllByAssigned(assigned);
    }

    public Optional<User> findUser(String email) {
        return userRepository.findByEmailAndActive(email, true);
    }

    public List<User> getAllByActive(boolean active) {
        return userRepository.findAllByActive(active);
    }


    public void deleteUser(String email) {
        userRepository.deleteByEmailAndActive(email, true);
    }

    public List<Role> getReqRoles(User user) {
        return user.getAssigned().getOwned();
    }

    public List<String> getCategories(User user) {
        List<String> categories = new ArrayList<>();
        categories.add(user.getAssigned().getName().replaceFirst("^EMPLOYEE_", ""));
        return categories;
    }
    
}
