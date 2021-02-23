package com.waltersteven.graphql.springbootgraphqldemo.repository;

import com.waltersteven.graphql.springbootgraphqldemo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
}
