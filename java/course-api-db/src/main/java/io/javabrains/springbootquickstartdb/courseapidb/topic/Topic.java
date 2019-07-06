package io.javabrains.springbootquickstartdb.courseapidb.topic;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Topic
 */
@Entity
public class Topic {

  @Id
  private String id;
  private String name;
  private String description;

  public Topic() {

  }

  public Topic(String id, String name, String description) {
    super();

    this.id = id;
    this.name = name;
    this.description = description;
  }

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Topic id(String id) {
    this.id = id;
    return this;
  }

  public Topic name(String name) {
    this.name = name;
    return this;
  }

  public Topic description(String description) {
    this.description = description;
    return this;
  }
}