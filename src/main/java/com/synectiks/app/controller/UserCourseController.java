package com.synectiks.app.controller;

import com.synectiks.app.service.UserCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user/courses")
public class UserCourseController {

    @Autowired
    private UserCourseService userCourseService;

    @GetMapping("/{courseType}")
    public ResponseEntity<List<Map<String, Object>>> getCoursesWithDetails(
            @PathVariable String courseType,
            @RequestParam String sid) {
        List<Map<String, Object>> courses = userCourseService.getCoursesWithDetails(courseType, sid);
        return ResponseEntity.ok(courses);
    }

}