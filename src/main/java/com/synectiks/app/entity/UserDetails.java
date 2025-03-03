package com.synectiks.app.entity;

import java.util.List;
import java.util.Map;

public class UserDetails {
	 private String name;
	    private String mail;
	    private String mno;
	    private String university;
	    private String cgpa;
	    private String degree;
	    private String passoutYear;
	    private List<String> skills;
	    private  List<Map<String, Object>> experience;
	    private List<String> projects;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMail() {
			return mail;
		}
		public void setMail(String mail) {
			this.mail = mail;
		}
		public String getMno() {
			return mno;
		}
		public void setMno(String mno) {
			this.mno = mno;
		}
		public String getUniversity() {
			return university;
		}
		public void setUniversity(String university) {
			this.university = university;
		}
		public String getCgpa() {
			return cgpa;
		}
		public void setCgpa(String cgpa) {
			this.cgpa = cgpa;
		}
		public String getDegree() {
			return degree;
		}
		public void setDegree(String degree) {
			this.degree = degree;
		}
		public String getPassoutYear() {
			return passoutYear;
		}
		public void setPassoutYear(String passoutYear) {
			this.passoutYear = passoutYear;
		}
		public List<String> getSkills() {
			return skills;
		}
		public void setSkills(List<String> skills) {
			this.skills = skills;
		}
		public List<Map<String, Object>> getExperience() {
			return experience;
		}
		public void setExperience(List<Map<String, Object>> experience) {
			this.experience = experience;
		}
		public List<String> getProjects() {
			return projects;
		}
		public void setProjects(List<String> projects) {
			this.projects = projects;
		}
		@Override
		public String toString() {
			return "UserDetails [name=" + name + ", mail=" + mail + ", mno=" + mno + ", university=" + university
					+ ", cgpa=" + cgpa + ", degree=" + degree + ", passoutYear=" + passoutYear + ", skills=" + skills
					+ ", experience=" + experience + ", projects=" + projects + "]";
		}
	    
}
