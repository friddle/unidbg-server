package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class UnidbgServerApplication {

    public static void main(String[] args) {
        if(!new File("./tmp").exists()){
            System.out.println("mkdir temp");
            new File("./tmp").mkdir();
        }
        SpringApplication.run(UnidbgServerApplication.class, args);
    }

}
