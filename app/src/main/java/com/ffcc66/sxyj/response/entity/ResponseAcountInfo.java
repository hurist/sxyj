package com.ffcc66.sxyj.response.entity;

public class ResponseAcountInfo {
	
	private boolean usernameIsExist;
	private boolean emailIsExist;
	
	public boolean isUsernameIsExist() {
		return usernameIsExist;
	}
	public void setUsernameIsExist(boolean usernameIsExist) {
		this.usernameIsExist = usernameIsExist;
	}
	public boolean isEmailIsExist() {
		return emailIsExist;
	}
	public void setEmailIsExist(boolean emailIsExist) {
		this.emailIsExist = emailIsExist;
	}

	
}
