package com.ikoembe.study.teachers;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TeacherController {
    private TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping(value = "/teacher")
    @ApiOperation(value = "Creates new teacher")
    public
    @ResponseBody
    ResponseEntity<Void> createTeacher(@RequestBody Teacher teacher){
        this.teacherService.insert(teacher);
        return ResponseEntity.ok().build();

    }
}
