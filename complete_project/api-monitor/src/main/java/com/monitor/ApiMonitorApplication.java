package com.monitor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class ApiMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiMonitorApplication.class, args);
        System.out.println("\n====================================");
        System.out.println("  API Monitor: http://localhost:9090");
        System.out.println("====================================\n");
    }
}
