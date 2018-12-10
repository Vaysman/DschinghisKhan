package ru.dto.json.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.util.Translit;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationData {
    private String companyName;
    private String email;
    private String inn;
    private String pointName;
    private String phone;
    private String ocved;
    private String kpp;
    private String ogrn;
    private String ocpo;
    private String directorFullname;


    public Map<String,String> check(){
        Translit translit = new Translit();
        Map<String, String> errors = new HashMap<>();
        if (inn.isEmpty()) errors.put("innError", "Не указан ИНН");
        Pattern mailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher mailMatcher = mailPattern.matcher(email);
        if(phone.isEmpty() && email.isEmpty()){
            errors.put("emailError","Должен быть указан адрес почты, либо телефон");
        } else if (!email.isEmpty() && !mailMatcher.find()){
            errors.put("emailError","Указан неверный адрес почты");
        }
        return  errors;
    }


}
