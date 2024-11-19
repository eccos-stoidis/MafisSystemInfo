package com.ep.sysinfo.MafisSyStemInfo.controller.GlobalException;

public class InvalidDriveFormatException extends RuntimeException {
    public InvalidDriveFormatException(String message) {
        super(message);
    }
}