package com.example.webswipeservice.modal.user.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserInfo {
    MultipartFile avatar;
    String username;
    String password;
}
