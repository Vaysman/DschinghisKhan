package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entity.Contract;

public interface ContractRepository extends PagingAndSortingRepository<Contract, Integer> {

}
