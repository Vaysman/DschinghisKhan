package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.CargoType;

import java.util.List;

@PreAuthorize("isFullyAuthenticated()")
public interface CargoTypeRepository extends PagingAndSortingRepository<CargoType, Integer> {
    List<CargoType> findTop10ByNameStartingWith(@Param("name") String name);
}
