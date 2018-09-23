package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entity.File;

public interface FileRepository extends PagingAndSortingRepository<File, Integer> {
}
