package io.javabrains.springbootquickstart.helloworld;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloWorldController
 */
@RestController
public class HelloWorldController {

  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ResponseBody
  public String helloWorld() {
    return "Hello World!";
  }
}