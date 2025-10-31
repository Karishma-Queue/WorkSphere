package com.karishma.worksphere.exception;

public class MemberOnlyException extends RuntimeException {
    public MemberOnlyException(String message) {
        super(message);
    }
}
