package com.ikoembe.study.service;

import com.ikoembe.study.models.Roles;
import com.ikoembe.study.models.User;
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
    @Autowired
    private UserImplementation userImplementation;
    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    public DbSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if(userImplementation.findUserByRole("ROLE_ADMIN").size()==0){
            createFirstAdminUser();
        }else log.info("Since admin user is found, new admin user creation omitted.");

    }

    @OneTimeExecutors(taskId = "add_first_admin_user")
    public void createFirstAdminUser(){
        Set<Roles>roleSet = new HashSet<>();
        roleSet.add(Roles.ROLE_ADMIN);
        String pwd = encoder.encode(password);
        User admin = new User(
                "adminSeeder", username,
                pwd,roleSet);
        userRepository.save(admin);
        log.info("The first admin user added in DB username: {}, password :{}", username, password);
    }


}
