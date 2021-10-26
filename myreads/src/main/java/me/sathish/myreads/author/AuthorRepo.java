package me.sathish.myreads.author;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepo extends CassandraRepository<Author, String> {
}
