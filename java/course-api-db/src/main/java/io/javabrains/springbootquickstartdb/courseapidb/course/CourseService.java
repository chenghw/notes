package io.javabrains.springbootquickstartdb.courseapidb.course;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CourseService
 */
@Service
public class CourseService {

  @Autowired
  private CourseRepository courseRepository;

  public List<Course> getAllCourses(String topicId) {
    return courseRepository.findByTopicId(topicId);
  }

  public Course getCourse(String id) {
    return courseRepository.findById(id).get();
  }

  public Course addCourse(Course course) {
    return courseRepository.save(course);
  }

  public Course updateCourse(String id, Course course) {
    return courseRepository.save(course);
  }

  public void deleteCourse(String id) {
    courseRepository.deleteById(id);
  }
}
