package com.synectiks.app.controller;

import com.synectiks.app.service.AdminLmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/admin/lms")  // Base path for admin endpoints
public class AdminLmsController {

    @Autowired
    private AdminLmsService adminLmsService;

    // --- Course Type Endpoints ---

    @PostMapping("/coursetypes")
    public ResponseEntity<String> createCourseType(@RequestParam String courseTypeName) {
        return ResponseEntity.ok(adminLmsService.createCourseType(courseTypeName));
    }

    @GetMapping("/coursetypes")
    public ResponseEntity<List<Map<String, Object>>> getAllCourseTypes() {
        return ResponseEntity.ok(adminLmsService.getAllCourseTypes());
    }

    // --- Course Endpoints ---

    @PostMapping("/courses")
    public ResponseEntity<String> createCourse(
            @RequestParam String courseType,
            @RequestParam String courseName,
            @RequestParam String difficultyLevel,
            @RequestParam int duration,
            @RequestParam int numberOfQuestions) {
        return ResponseEntity.ok(adminLmsService.createCourse(courseType, courseName, difficultyLevel, duration, numberOfQuestions));
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getAllCourses(@RequestParam String courseType) {
        return ResponseEntity.ok(adminLmsService.getAllCourses(courseType));
    }

    // --- Question Endpoints ---

    @PostMapping("/questions")
    public ResponseEntity<String> createQuestion(@RequestParam String course, @RequestBody Map<String, Object> questionData) {
        return ResponseEntity.ok(adminLmsService.createQuestion(course, questionData));
    }

    @PutMapping("/questions/{qid}")
    public ResponseEntity<String> updateQuestion(@PathVariable String qid, @RequestBody Map<String, Object> questionData) {
        return ResponseEntity.ok(adminLmsService.updateQuestion(qid, questionData));
    }

    @DeleteMapping("/questions/{qid}")
    public ResponseEntity<String> deleteQuestion(@PathVariable String qid) {
        return ResponseEntity.ok(adminLmsService.deleteQuestion(qid));
    }
}