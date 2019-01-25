package ru.dto.json.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    /**
     * @return Returns map of format: "ErrorName":"Error description"
     * Checks provided data for incorrect values, such as incorrect or empty email, empty INN, empty phone, et cetera.
     */
    public Map<String,String> check(){
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
