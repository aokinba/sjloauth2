/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjloauth2.gateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Administrator
 */
@Controller
public class LoginController {

    @GetMapping("/authlogin")
    public String index(Model model) {
        model.addAttribute("userAttributes", "123");
        return "authlogin";
    }
}
