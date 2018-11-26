package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entity.StoredFile;

public interface FileRepository extends PagingAndSortingRepository<StoredFile, Integer> {
}
