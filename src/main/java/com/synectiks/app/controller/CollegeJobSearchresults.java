package com.synectiks.app.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.synectiks.app.service.CollegeJobSearchService;

import java.util.List;

@RestController
@CrossOrigin
public class CollegeJobSearchresults {

	@Autowired
    private CollegeJobSearchService jobSearchService;

    @GetMapping("/job-searchresults")
    public List<String> getJobLinks(@RequestParam String query, @RequestParam(defaultValue = "10") int numResults) {
        return jobSearchService.searchJobs(query, numResults);
    }


}
