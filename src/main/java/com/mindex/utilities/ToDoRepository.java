package com.mindex.utilities;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "toDo", path = "toDo")
public interface ToDoRepository extends PagingAndSortingRepository<ToDo, Long> {

	List<ToDo> findById(@Param("id") String id);

}
