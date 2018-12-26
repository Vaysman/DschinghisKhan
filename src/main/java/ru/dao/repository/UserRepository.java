package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.User;

import java.util.List;
import java.util.Optional;
@PreAuthorize("isFullyAuthenticated()")
public interface UserRepository extends DataTablesRepository<User, Integer> {


    Optional<User> findByLogin(String login);


    List<User> findDistinctByUsernameContaining(@Param("username") String username);

    List<User> findTop10ByUsernameContainingAndOriginator(@Param("username") String username, @Param("originator") Integer originator);
}
