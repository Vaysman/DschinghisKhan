package ru.dto.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dao.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private String userName;
    private String userEmail;

    public UserData(User user) {
        this.userName = user.getUsername();
        this.userEmail = user.getEmail();
    }
}
