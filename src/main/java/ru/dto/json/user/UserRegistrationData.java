package ru.dto.json.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationData {
    private String companyName;
    private String companyShortName;
    private String email;
    private String inn;
    private String login;
    private String password;
    private String pointName;
    private String pointAddress;

    public Map<String,String> check(){
        Map<String, String> errors = new HashMap<>();

        if (companyName.isEmpty()) errors.put("companyNameError", "Не указано название компании");
        if (login.isEmpty()) errors.put("loginError", "Не указан логин");
        if (password.isEmpty()) errors.put("passwordError", "Не указан пароль");
        if (password.length() < 6) errors.put("passwordError", "Пароль меньше 6 символов");
        if (pointName.isEmpty() && !pointAddress.isEmpty()) errors.put("pointNameError","Если указан адрес - нужно так же указать название");
        if (pointAddress.isEmpty() && !pointName.isEmpty()) errors.put("pointAddressError","Если указано название - нужно так же указать адрес");
        return  errors;
    }


}
