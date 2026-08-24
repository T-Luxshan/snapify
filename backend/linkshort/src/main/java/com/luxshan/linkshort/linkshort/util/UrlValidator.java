package com.luxshan.linkshort.linkshort.util;

import com.luxshan.linkshort.linkshort.exception.InvalidUrlException;
import org.springframework.stereotype.Component;
@Component
public class UrlValidator {
    public boolean isValid(String url) {
        if(url == null || url.isEmpty()) {
           return false;
        }
        // if(url.length() > 2048) {
        //     return false;
        // }
        if(url.startsWith("http://") || url.startsWith("https://")) {
            return true;
        }
        else {
            return false;
        }
    }

}
