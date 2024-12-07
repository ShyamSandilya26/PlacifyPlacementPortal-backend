package com.klu.springboot.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.klu.springboot.model.Applications;
import com.klu.springboot.model.Employer;
import com.klu.springboot.model.Jobs;
import com.klu.springboot.model.Student;
import com.klu.springboot.repositry.StudentRepositry;
import com.klu.springboot.service.StudentService;

@RestController
@RequestMapping("/api/student")
@CrossOrigin
public class StudentController {
	
	
	@Autowired
	private StudentService studentService;
	
	private final Map<String, Student> activeSessions = new ConcurrentHashMap<>();
	
	@PostMapping("/register")
    public ResponseEntity<String> registerEmployer(@RequestBody Student student) {
        try {
            String message = studentService.studentRegistration(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering employer: " + e.getMessage());
        }
    }
    
	@PostMapping("/login")
	public ResponseEntity<?> loginStudent(@RequestParam String username, @RequestParam String password) {
	    try {
	        Student student = studentService.checkStudentLogin(username, password);
	        if (student != null) {
	            activeSessions.put(username, student); // Store the student in active sessions
	            System.out.println("Active Sessions After Login: " + activeSessions); // Debug log
	            return ResponseEntity.ok("Login successful");
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body("Invalid name or password");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error during login: " + e.getMessage());
	    }
	}
    
    @GetMapping("/jobs")
    public ResponseEntity<List<Jobs>> getAllJobs() {
        try {
            List<Jobs> jobs = studentService.viewAllJobs();
            if (jobs.isEmpty()) {
                return ResponseEntity.noContent().build(); // Return 204 if no jobs found
            }
            return ResponseEntity.ok(jobs); // Return 200 with the list of jobs
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Return 500 in case of errors
        }
    }
    
    
    @PostMapping("/submitapplication")
    public ResponseEntity<String> submitApplication(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("tenthcgpa") double tenthCgpa,
            @RequestParam("twelethcgpa") double twelethCgpa,
            @RequestParam("gradutioncgpa") double gradutionCgpa,
            @RequestParam("resume") MultipartFile resumeFile) {
        try {
            if (resumeFile.isEmpty() || !resumeFile.getOriginalFilename().endsWith(".pdf")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid resume file. Please upload a PDF.");
            }

            // Create Applications object and populate fields
            Applications application = new Applications();
            application.setName(name);
            application.setEmail(email);
            application.setTenthcgpa(tenthCgpa);
            application.setTwelethcgpa(twelethCgpa);
            application.setGradutioncgpa(gradutionCgpa);
            application.setResume(new javax.sql.rowset.serial.SerialBlob(resumeFile.getBytes()));

            // Save application using the service
            String message = studentService.submitApplication(application);

            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the resume file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting application: " + e.getMessage());
        }
    }
    
    @GetMapping("/studentdetails")
    public ResponseEntity<Student> getStudentDetails(@RequestParam String name) {
        System.out.println("Active Sessions: " + activeSessions); // Debug log
        Student student = activeSessions.get(name);

        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(student);
    }

}
