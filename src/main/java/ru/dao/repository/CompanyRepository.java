package ru.dao.repository;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.constant.CompanyType;
import ru.dao.entity.Company;

import java.util.List;

public interface CompanyRepository extends DataTablesRepository<Company, Integer> {
    @Override
    DataTablesOutput<Company> findAll(DataTablesInput input, Specification<Company> additionalSpecification);

    List<Company> findTop10ByNameContainingAndType(@Param("name") String name, @Param("type") CompanyType companyType);
    List<Company> findTop10ByNameContaining(@Param("name") String name);
    List<Company> findTop10ByNameContainingAndOriginator(@Param("name") String name, @Param("originator") Integer originator);

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    void delete(Company entity);

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    void deleteAll();

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    void deleteAll(Iterable<? extends Company> entities);

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    void deleteById(Integer integer);
}
