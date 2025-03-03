package com.synectiks.app.entity;

public class CollegeReadingForFireBase {
	   private String questionId;  // Firestore auto-generated ID
	    private String questionText;
	    private String option1;
	    private String option2;
	    private String option3;
	    private String option4;
	    private String correctAnswer;
	    private String paragraph;
	    private String category;
		public String getQuestionId() {
			return questionId;
		}
		public void setQuestionId(String questionId) {
			this.questionId = questionId;
		}
		public String getQuestionText() {
			return questionText;
		}
		public void setQuestionText(String questionText) {
			this.questionText = questionText;
		}
		public String getOption1() {
			return option1;
		}
		public void setOption1(String option1) {
			this.option1 = option1;
		}
		public String getOption2() {
			return option2;
		}
		public void setOption2(String option2) {
			this.option2 = option2;
		}
		public String getOption3() {
			return option3;
		}
		public void setOption3(String option3) {
			this.option3 = option3;
		}
		public String getOption4() {
			return option4;
		}
		public void setOption4(String option4) {
			this.option4 = option4;
		}
		public String getCorrectAnswer() {
			return correctAnswer;
		}
		public void setCorrectAnswer(String correctAnswer) {
			this.correctAnswer = correctAnswer;
		}
		public String getParagraph() {
			return paragraph;
		}
		public void setParagraph(String paragraph) {
			this.paragraph = paragraph;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		@Override
		public String toString() {
			return "CollegeReadingForFireBase [questionId=" + questionId + ", questionText=" + questionText
					+ ", option1=" + option1 + ", option2=" + option2 + ", option3=" + option3 + ", option4=" + option4
					+ ", correctAnswer=" + correctAnswer + ", paragraph=" + paragraph + ", category=" + category + "]";
		}
	    
	    
	    
}


