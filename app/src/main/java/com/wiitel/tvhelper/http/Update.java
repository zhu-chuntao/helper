package com.wiitel.tvhelper.http;

public class Update {
	private String result;
	private String description;
	private String userToken;
	private String playUrl;
	private String apkVersion;
	private String apkName;
	private String apkDownloadUrl;
	private int apkVerCode;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}

	public String getApkVersion() {
		return apkVersion;
	}

	public void setApkVersion(String apkVersion) {
		this.apkVersion = apkVersion;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getApkDownloadUrl() {
		return apkDownloadUrl;
	}

	public void setApkDownloadUrl(String apkDownloadUrl) {
		this.apkDownloadUrl = apkDownloadUrl;
	}

	public int getApkVerCode() {
		return apkVerCode;
	}

	public void setApkVerCode(int apkVerCode) {
		this.apkVerCode = apkVerCode;
	}

	@Override
	public String toString() {
		return "result=" + result + ";description" + description;
	}

}
