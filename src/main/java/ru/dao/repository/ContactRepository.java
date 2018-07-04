package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import ru.dao.entity.Contact;

public interface ContactRepository extends DataTablesRepository<Contact, Integer> {
}
