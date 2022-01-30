package com.ikoembe.study.schools;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import javax.xml.bind.ValidationException;
import java.util.*;

@RestController
@RequestMapping(value = "/school")
public class SchoolController {
    private SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @PostMapping
    @ApiOperation( tags = "School", value = "Create new School object")
    public School insert(@RequestBody School school) throws ValidationException {
        this.schoolService.insert(school);
        return school;
    }

    @GetMapping
    public List<School> getAllSchool(){
        return this.schoolService.findAll();
    }

    @PostMapping("/class/{className}")
    public List<School> getAllClassStudents(@PathParam("className") String className){
        return this.schoolService.findAllByClassName(className);
    }

    @PostMapping("/all/{school}")
    public List<School> getAllSchools(@PathParam("schoolName") String schoolName){
        return this.schoolService.findAllBySchoolName(schoolName);
    }

}
