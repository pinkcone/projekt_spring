package com.pollub.cookie.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class NotificationDTO {
    private Long id;
    private String content;
    private boolean read;
    private LocalDateTime creationDate;
}
