package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import ru.dao.entity.Client;

import java.util.List;

public interface ClientRepository extends DataTablesRepository<Client, Integer> {
    List<Client> findAllByNameContainingAndOriginator(@Param("name") String name, @Param("originator") Integer originator);
    List<Client> findTop10ByOriginatorAndNameContaining(@Param("originator") Integer originator, @Param("name") String name);
}
