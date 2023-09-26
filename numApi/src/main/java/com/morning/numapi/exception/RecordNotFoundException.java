package com.morning.numapi.exception;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(Long id){
        super("Could not found the record with id " + id);
    }
}
