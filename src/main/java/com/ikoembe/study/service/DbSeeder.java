package com.ikoembe.study.service;

import com.ikoembe.study.models.ERole;
import com.ikoembe.study.models.Role;
import com.ikoembe.study.models.User;
import com.ikoembe.study.repository.RoleRepository;
import com.ikoembe.study.repository.UserRepository;
import com.ikoembe.study.util.OneTimeExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DbSeeder implements CommandLineRunner {
    Logger log = LoggerFactory.getLogger(DbSeeder.class);
    @Autowired
    PasswordEncoder encoder;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    @Autowired
    private UserImplementation userImplementation;
    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    public DbSeeder(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if(roleRepository.findAll().size()==0){
            initializeRoles();
        }else log.info("Roles initialization is not needed");

        if(userImplementation.findUserByRole("ROLE_ADMIN").size()==0){
            createFirstAdminUser();
        }else log.info("Since admin user is found, new admin user creation omitted.");

    }

    @OneTimeExecutors(taskId = "add_first_admin_user")
    public void createFirstAdminUser(){
        Set<Role>roleSet = new HashSet<>();
        roleSet.add(new Role(ERole.ROLE_ADMIN));
        String pwd = encoder.encode(password);
        User admin = new User(
                "adminSeeder", username,
                pwd,roleSet);
        userRepository.save(admin);
        log.info("The first admin user added in DB username: {}, password :{}", username, password);
    }

    @OneTimeExecutors(taskId = "initialize roles in DB")
    public void initializeRoles(){
        List<Role> roleList = Arrays.asList(
                new Role(ERole.ROLE_ADMIN),
                new Role(ERole.ROLE_STUDENT),
                new Role(ERole.ROLE_TEACHER),
                new Role(ERole.ROLE_GUARDIAN)
        );
        // drop all roles if existed
        this.roleRepository.deleteAll();
        roleRepository.saveAll(roleList);
        log.info("User roles are added in roles collection");
    }
}
