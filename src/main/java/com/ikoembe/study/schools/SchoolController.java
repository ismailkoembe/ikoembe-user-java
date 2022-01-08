package com.ikoembe.study.schools;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.*;

@RestController
@RequestMapping(value = "/classes")
public class SchoolController {
    private SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @PutMapping
    @ApiOperation( tags = "Classes", value = "Create new Class object")
    public School insert(@RequestBody School school){
        this.schoolService.insert(school);
        return school;
    }

    @GetMapping
    public List<School> getAllClass(){
        return this.schoolService.findAll();
    }

    @PostMapping("/classname/{className}")
    public List<School> getAllClassStudents(@PathParam("className") String className){
        return this.schoolService.findAllByClassName(className);
    }

}
