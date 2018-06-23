package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entities.User;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {

    Optional<User> findByLogin(String login);
}
