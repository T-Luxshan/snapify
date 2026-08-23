package com.luxshan.linkshort.linkshort.util;

import com.luxshan.linkshort.linkshort.exception.InvalidUrlException;
import org.springframework.stereotype.Component;
@Component
public class UrlValidator {
    public boolean isValid(String url) {
        if(url == null || url.isEmpty()) {
            throw new InvalidUrlException("URL is required");
        }
        // if(url.length() > 2048) {
        //     return false;
        // }
        if(url.startsWith("http://") || url.startsWith("https://")) {
            return true;
        }
        else {
            throw new InvalidUrlException("Invalid URL: must start with http:// or https://");
        }
    }

}
