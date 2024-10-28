package com.ep.sysinfo.MafisSyStemInfo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.exceptions.TemplateEngineException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TemplateEngineException.class)
    public String handleTemplateEngineException(TemplateEngineException e) {
        logger.error("Template engine error occurred: {}", e.getMessage());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "An error occurred while processing your request. Please try again later. " + e.getMessage());
        return "error";
    }

    // Handle General Exception
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception e, Model model) {
        logger.error("General error occurred: {}", e.getMessage());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "An error occurred. Please try again later. " + e.getMessage());
        return "response";
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        logger.error("Illegal argument error occurred: {}", e.getMessage());
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "Illegal argument error occurred: " + e.getMessage());
        return "response";
    }

    //Handle RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime error occurred: {}", ex.getMessage());
        ModelAndView modelAndView = new ModelAndView("errorRuntime");
        modelAndView.addObject("error",  ex.getMessage());
        return modelAndView;
    }
}
