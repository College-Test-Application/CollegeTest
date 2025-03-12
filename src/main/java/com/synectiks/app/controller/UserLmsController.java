package com.synectiks.app.controller;

import com.synectiks.app.service.UserLmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user/lms") // Base path for user endpoints
public class UserLmsController {

    @Autowired
    private UserLmsService userLmsService;

    @GetMapping("/questions/{course}")
    public ResponseEntity<List<Map<String, Object>>> getLmsQuestions(@PathVariable String course) {
        return ResponseEntity.ok(userLmsService.getLmsQuestions(course));
    }

    @PostMapping("/questions/{course}/{sid}")
    public ResponseEntity<Map<String, Object>> validatelms(@RequestBody List<Map<String, Object>> data, @PathVariable String course, @PathVariable String sid) {
        Map<String, Object> results = userLmsService.validateLmsService(data, sid, course);

        // Check if the service returned an error
        if (results.containsKey("error")) {
            return ResponseEntity.status(500).body(results); // Return 500 Internal Server Error with the error message
        }

        return ResponseEntity.ok(results); // Return the results map as the response
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getAllCourses(@RequestParam String courseType) {
        return ResponseEntity.ok(userLmsService.getAllCourses(courseType));
    }

  @GetMapping("/stats/{courseType}")
  public ResponseEntity<Map<String, Object>> getCourseTypeStats(
          @PathVariable String courseType,
          @RequestParam String sid) {
      return ResponseEntity.ok(userLmsService.getCourseTypeStats(courseType, sid));
  }
}