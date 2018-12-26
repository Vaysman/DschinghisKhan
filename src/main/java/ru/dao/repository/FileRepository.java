package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.StoredFile;

@PreAuthorize("isFullyAuthenticated()")
public interface FileRepository extends PagingAndSortingRepository<StoredFile, Integer> {
}
