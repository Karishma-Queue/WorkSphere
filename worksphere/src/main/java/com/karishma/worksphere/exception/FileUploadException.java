package com.karishma.worksphere.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message,Throwable cause){
        super(message,cause);
    }
}
