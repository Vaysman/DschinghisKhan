package ru.dto.json.user;

import lombok.Data;

@Data
public class CompanyPasswordResetRequest {
    private String phoneNumber;
    private String contact;
    private String email;

}
