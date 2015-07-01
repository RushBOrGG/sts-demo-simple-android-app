package com.example.oss_sdk_demo.model;


public class FileObject {

	public static enum FileType {
		FOLDER,
		FILE
	}

	private FileType fileType;
	private String fileName;

	public FileObject(String fileName, FileType type) {
		this.fileName = fileName;
		this.fileType = type;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
