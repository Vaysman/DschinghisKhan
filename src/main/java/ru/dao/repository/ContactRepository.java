package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.constant.ContactType;
import ru.dao.entity.Client;
import ru.dao.entity.Company;
import ru.dao.entity.Contact;

import java.util.List;
import java.util.Optional;

@PreAuthorize("isFullyAuthenticated()")
public interface ContactRepository extends DataTablesRepository<Contact, Integer> {
    List<Contact> findTop10ByNameContainingAndOriginator(@Param("name") String name,@Param("originator") Integer originator);
    Optional<Contact> findFirstByCompanyAndType(Company company, ContactType contactType);
    List<Contact> findTop10ByClientAndNameContaining(@Param("client") Client client, @Param("name") String name);
    List<Contact> findTop10ByClientIdAndNameContaining(@Param("clientId") Integer clientId, @Param("name") String name);
}
