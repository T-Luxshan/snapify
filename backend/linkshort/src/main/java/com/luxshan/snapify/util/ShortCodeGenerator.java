package com.luxshan.snapify.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class ShortCodeGenerator {
 final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

 public String generate() {
    StringBuilder shortCode = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < 7; i++) {
        shortCode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
    }
    return shortCode.toString();
 }
}


