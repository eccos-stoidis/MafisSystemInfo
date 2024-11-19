package com.ep.sysinfo.MafisSyStemInfo.controller.GlobalException;

import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.exceptions.TemplateEngineException;

import java.io.FileNotFoundException;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TemplateEngineException.class)
    public String handleTemplateEngineException(TemplateEngineException e,Model model) {
        logger.error("Template engine error occurred: {}", e.getMessage());
        model.addAttribute("message", "An error occurred while processing your request. Please try again later. " + e.getMessage());
        return "response";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException e, Model model) {
        logger.warn("Resource not found: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "response";
    }

    // Handle General Exception
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e, Model model) {
        logger.warn("An error occurred: {}", e.getMessage(), e);

        // Use the exception's message or provide a default one if unavailable
        String specificMessage = e.getMessage() != null && !e.getMessage().isEmpty()
                ? e.getMessage()
                : "Ein unbekannter Fehler ist aufgetreten.";

        model.addAttribute("message", specificMessage);
        return "response";
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        logger.error("Illegal argument error occurred: {}", e.getMessage());
        model.addAttribute("message", "Illegal argument error occurred: " + e.getMessage());
        return "response";
    }

    //Handle RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        logger.error("Runtime error occurred: {}", ex.getMessage());
        model.addAttribute("message", "Runtime error occurred: " + ex.getMessage());
        return "response";
    }

    @ExceptionHandler(InvalidDriveFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidDriveFormat(InvalidDriveFormatException e, Model model) {
        logger.error("Invalid drive format: {}", e.getMessage());
        model.addAttribute("message", "Ungültiges Laufwerksformat: " + e.getMessage());
        return "response";
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleNumberFormatException(NumberFormatException e, Model model) {
        logger.error("Number format error occurred: {}", e.getMessage());
        model.addAttribute("message", "Ungültiges Zahlenformat: " + e.getMessage());
        return "response";
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleLogoImageIOException(IOException e, Model model) {
        logger.error("Logo image processing error: {}", e.getMessage());
        model.addAttribute("message", "Es ist ein Fehler beim Verarbeiten der Datei aufgetreten: " + e.getMessage());
        return "response";
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleFileNotFoundException(FileNotFoundException e, Model model) {
        logger.error("File not found error: {}", e.getMessage());
        model.addAttribute("message", "Die angeforderte Datei konnte nicht gefunden werden: " + e.getMessage());
        return "response";
    }

    @ExceptionHandler(DocumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDocumentException(DocumentException e, Model model) {
        logger.error("PDF generation error: {}", e.getMessage());
        model.addAttribute("message", "Es ist ein Fehler beim Erstellen des PDF-Dokuments aufgetreten: " + e.getMessage());
        return "response";
    }


}
