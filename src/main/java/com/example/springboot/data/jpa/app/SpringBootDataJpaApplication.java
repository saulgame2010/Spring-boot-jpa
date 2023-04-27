package com.example.springboot.data.jpa.app;

import com.example.springboot.data.jpa.app.models.services.IUploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SpringBootDataJpaApplication implements CommandLineRunner {

    @Autowired
    IUploadFileService iUploadFileService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDataJpaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        iUploadFileService.deleteAll();
        iUploadFileService.init();

        String password = "123456";
        for(int i = 0; i < 2; i++) {
            String bcryptPassword = passwordEncoder.encode(password);
            System.out.println(bcryptPassword);
        }
    }
}
