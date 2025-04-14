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

    public boolean saveNewUser(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        try {
            Optional<User> user1 = userRepository.findById(user.getId());
            user.setAssigned(user.getAssigned());
            if (!user1.get().getPassword().equals(passwordEncoder.encode(user.getPassword()))) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public List<Role> getReqRoles(User user) {
        List<Role> roles = roleRepository.findByNameNot("ADMIN");
        for(Role it : roles) {
            if(!it.getAddedby().getName().equals(user.getAssigned().getName())) {
                roles.remove(it);
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
            if (!it.getAssigned().getName().startsWith("EMPLOYEE") || !it.getAssigned().getAddedby().equals(user.getAssigned())) {
                iterator.remove(); 
            }
        }
        return employees;
    }

}
