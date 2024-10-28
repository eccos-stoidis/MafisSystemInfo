package com.ep.sysinfo.MafisSyStemInfo.controller;

import com.ep.sysinfo.MafisSyStemInfo.model.Benutzer;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    /**
     * Retrieves the login page.
     *
     * @return the name of the login page
     */
    @GetMapping("/login")
    String login() {
        return "login";
    }

    /**
     * Handles the default behavior after a successful login.
     *
     * @return the URL to redirect the user to based on their role
     */
    @RequestMapping("/homepage")
    public String defaultAfterLogin() {
        return "homepage";
    }

}
