package com.luxshan.linkshort.linkshort.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Link {

    String shortCode;
    String originalUrl;
    LocalDateTime createdAt;
    boolean active;
}
