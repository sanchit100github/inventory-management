package com.example.inventory_management.service;

import java.util.*;

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
            Set<Role> roles = new HashSet<>();
            for(Role it : user.getRoles()) {
                if (roleRepository.findByName(it.getName()).isEmpty()) {
                    Role role = new Role();
                    role.setName(it.getName());
                    roleRepository.save(role);
                    roles.add(role);
                }
                else {
                    roles.add(roleRepository.findByName(it.getName()).get());
                }
                
            }
            user.setRoles(roles);
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
            Set<Role> roles = new HashSet<>();
            for(Role it : user.getRoles()) {
                if (roleRepository.findByName(it.getName()).isEmpty()) {
                    Role role = new Role();
                    role.setName(it.getName());
                    roleRepository.save(role);
                    roles.add(role);
                }
                else {
                    roles.add(roleRepository.findByName(it.getName()).get());
                }
                
            }
            user.setRoles(roles);
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

    public Optional<User> findUser(String email) {
        return userRepository.findByEmailAndActive(email, true);
    }

    public List<User> getAllByActive(boolean active) {
        return userRepository.findAllByActive(active);
    }


    public void deleteUser(String email) {
        userRepository.deleteByEmailAndActive(email, true);
    }

    public List<String> getReqRoles(User user) {
        List<String> roles = new ArrayList<>(); 
        for(Role it : user.getRoles()) {
            if(it.getName().equals("ADMIN")) {
                roles.add("MANAGER_Electronics");
                roles.add("MANAGER_Furniture");
                roles.add("MANAGER_Sports");
                roles.add("MANAGER_Hardware");
                roles.add("MANAGER_Toys");
            }
            else if(it.getName().equals("MANAGER_Electronics")) {
                roles.add("EMPLOYEE_Smartphones");
                roles.add("EMPLOYEE_Laptops");
                roles.add("EMPLOYEE_TV");
                roles.add("EMPLOYEE_Refrigerators");
                roles.add("EMPLOYEE_AC");
            }
            else if(it.getName().equals("MANAGER_Furniture")) {
                roles.add("EMPLOYEE_Sofa");
                roles.add("EMPLOYEE_DiningTables");
                roles.add("EMPLOYEE_StudyTables");
                roles.add("EMPLOYEE_Chairs");
                roles.add("EMPLOYEE_Bed");
            }
            else if(it.getName().equals("MANAGER_Sports")) {
                roles.add("EMPLOYEE_Cricket");
                roles.add("EMPLOYEE_Football");
                roles.add("EMPLOYEE_Hockey");
                roles.add("EMPLOYEE_Vollyball");
                roles.add("EMPLOYEE_TableTennis");
            }
            else if(it.getName().equals("MANAGER_Hardware")) {
                roles.add("EMPLOYEE_Plumbing");
                roles.add("EMPLOYEE_Paint");
                roles.add("EMPLOYEE_Electrical");
                roles.add("EMPLOYEE_Gardening");
                roles.add("EMPLOYEE_Power");
            }
            else if(it.getName().equals("MANAGER_Toys")) {
                roles.add("EMPLOYEE_ActionFigures");
                roles.add("EMPLOYEE_Board");
                roles.add("EMPLOYEE_Educational");
                roles.add("EMPLOYEE_VideoGames");
                roles.add("EMPLOYEE_Stuffed");
            }
        }
        return roles;
    }

    public List<String> getCategories(User user) {
        List<String> categories = new ArrayList<>();
        for(Role it : user.getRoles()) {
            categories.add(it.getName().replaceFirst("^EMPLOYEE_", ""));
        }
        return categories;
    }
    
}
