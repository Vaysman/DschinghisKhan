package ru.dao.repository;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import ru.dao.entity.Company;

import java.util.List;

public interface CompanyRepository extends DataTablesRepository<Company, Integer> {
    @Override
    DataTablesOutput<Company> findAll(DataTablesInput input, Specification<Company> additionalSpecification);

    List<Company> findTop10ByNameContaining(@Param("name") String name);
    List<Company> findTop10ByNameContainingAndOriginator(@Param("name") String name, @Param("originator") Integer originator);
}
