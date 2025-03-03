package com.synectiks.app.entity;

import com.google.api.services.storage.Storage.DefaultObjectAccessControls.List;

public class FireBaseListeningAudio {
	  private String audioFileUrl; // URL of the uploaded audio file
	    private List questions;
		public String getAudioFileUrl() {
			return audioFileUrl;
		}
		public void setAudioFileUrl(String audioFileUrl) {
			this.audioFileUrl = audioFileUrl;
		}
		public List getQuestions() {
			return questions;
		}
		public void setQuestions(List questions) {
			this.questions = questions;
		}
	    
	    
}
