package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entities.Pretension;

public interface PretensionRepository extends PagingAndSortingRepository<Pretension, Integer> {

}
