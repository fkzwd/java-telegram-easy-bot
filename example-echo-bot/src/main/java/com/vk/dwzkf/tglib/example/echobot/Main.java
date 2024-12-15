package com.vk.dwzkf.tglib.example.echobot;

import com.vk.dwzkf.tglib.botcore.BotCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Roman Shageev
 * @since 12.12.2024
 */
@SpringBootApplication(scanBasePackageClasses = {Main.class, BotCore.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}