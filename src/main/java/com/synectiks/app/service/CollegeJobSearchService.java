package com.synectiks.app.service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class CollegeJobSearchService {
    public List<String> searchJobs(String query, int numResults) {
        List<String> jobLinks = new ArrayList<>();
        String searchUrl = "https://www.google.com/search?q=" + query + "+jobs";

        try {
            // Fetch the Google search results page
            Document document = Jsoup.connect(searchUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // Select the links from the search result page
            for (Element result : document.select("a")) {
                String link = result.attr("href");

                // Ensure the link is absolute
                if (!link.startsWith("http")) {
                    link = "https://www.google.com" + link;
                }

                // Filter links likely to lead to job applications
                if (link.contains("apply") || link.contains("job") || link.contains("career")) {
                    jobLinks.add(link);
                    if (jobLinks.size() >= numResults) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error while searching for jobs: " + e.getMessage());
        }

        return jobLinks;
    }
}

//public class CollegeJobSearchService {
//	 public List<String> searchJobs(String query, int numResults) {
//	        List<String> jobLinks = new ArrayList<>();
//	        String searchUrl = "https://www.google.com/search?q=" + query + "+jobs";
//
//	        try {
//	            // Fetch the Google search results page
//	            Document document = Jsoup.connect(searchUrl)
//	                    .userAgent("Mozilla/5.0")
//	                    .timeout(5000)
//	                    .get();
//
//	            // Select the links from the search result page
//	            for (Element result : document.select("a")) {
//	                String link = result.attr("href");
//
//	                // Filter links likely to lead to job applications
//	                if (link.contains("apply") || link.contains("job") || link.contains("career")) {
//	                    jobLinks.add(link);
//	                    if (jobLinks.size() >= numResults) {
//	                        break;
//	                    }
//	                }
//	                System.out.println(jobLinks);
//	            }
//	        } catch (IOException e) {
//	            System.err.println("Error while searching for jobs: " + e.getMessage());
//	        }
//
//	        return jobLinks;
//	    }
//}
