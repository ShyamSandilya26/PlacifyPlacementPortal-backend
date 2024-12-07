package com.klu.springboot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Employer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int EmployerId;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false,unique = true)
	private String email;
	@Column(nullable = false,unique = true)
	private String contact;
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
    private String status = "Pending";
	
	
	

}