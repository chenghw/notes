package io.javabrains.springbootquickstart.topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * TopicService
 */
@Service
public class TopicService {

  private List<Topic> topics = new ArrayList<Topic>(Arrays.asList(
    new Topic("spring", "Spring Framework", "Spring Framework Description"),
    new Topic("java", "Core Java", "Core Java Description"),
    new Topic("javascript", "JavaSript", "JavaScript Description")
  ));

  public List<Topic> getAllTopics() {
    return topics;
  }

  public Topic getTopic(String id) {
    return topics.stream().filter(t -> t.getId().equals(id)).findFirst().get();
  }

  public void addTopic(Topic topic) {
    topics.add(topic);
  }

  public void updateTopic(String id, Topic topic) {
    for (int i = 0; i < topics.size(); i++ ) {
      if (topics.get(i).getId().equals(id)) {
        topics.set(i, topic);
      }
    }
  }

  public void deleteTopic(String id) {
    topics.removeIf(t -> t.getId().equals(id));
  }
}
