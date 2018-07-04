package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import ru.dao.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends DataTablesRepository<User, Integer> {


    Optional<User> findByLogin(String login);


    List<User> findDistinctByUsernameContaining(@Param("username") String username);
}
