package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.Contract;

import java.util.List;

@PreAuthorize("isFullyAuthenticated()")
public interface ContractRepository extends PagingAndSortingRepository<Contract, Integer> {
    List<Contract> findAllByInitiativeCompanyId(Integer companyId);
    List<Contract> findAllByCompanyId(Integer companyId);
    List<Contract> findAllByCompanyIdAndInitiativeCompanyId(Integer companyId, Integer initiativeCompanyId);
}
