package com.ikoembe.study.classes;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.*;

@RestController
@RequestMapping(value = "/classes")
public class ClassController {
    private ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @PutMapping
    @ApiOperation( tags = "Classes", value = "Create new Class object")
    public Classes insert(@RequestBody Classes classes){
        this.classService.insert(classes);
        return classes;
    }

    @GetMapping
    public List<Classes> getAllClass(){
        return this.classService.findAll();
    }

    @PostMapping("/classname/{className}")
    public List<Classes> getAllClassStudents(@PathParam("className") String className){
        return this.classService.findAllByClassName(className);
    }

}
