package com.ksj.eoisa.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CustomErrorController implements ErrorController {

	@RequestMapping(value = "/error")
	public ModelAndView handleErrorPage(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        ModelAndView mv = new ModelAndView();
        mv.setViewName("error");
        mv.addObject("statusCode", statusCode.toString());
        mv.addObject("errorMessage", errorMessage.toString());

        return mv;
    }
    
    @Override
    public String getErrorPath() {
        return "/error";
    }

}