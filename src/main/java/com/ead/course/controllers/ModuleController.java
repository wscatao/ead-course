package com.ead.course.controllers;

import com.ead.course.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    ModuleService moduleService;

//    @PostMapping("/courses/{courseId}/modules")
//    public
}
