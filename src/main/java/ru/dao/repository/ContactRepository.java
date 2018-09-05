package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import ru.constant.ContactType;
import ru.dao.entity.Company;
import ru.dao.entity.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends DataTablesRepository<Contact, Integer> {
    List<Contact> findTop10ByNameContainingAndOriginator(@Param("name") String name,@Param("originator") Integer originator);
    Optional<Contact> findFirstByCompanyAndType(Company company, ContactType contactType);
}
