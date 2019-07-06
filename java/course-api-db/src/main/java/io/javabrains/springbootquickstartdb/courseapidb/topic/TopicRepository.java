package io.javabrains.springbootquickstartdb.courseapidb.topic;

import org.springframework.data.repository.CrudRepository;

/**
 * TopicRepository
 */
public interface TopicRepository extends CrudRepository<Topic, String> {

}