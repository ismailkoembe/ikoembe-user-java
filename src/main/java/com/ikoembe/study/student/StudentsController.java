package com.ikoembe.study.student;

import com.google.common.base.Strings;
import com.ikoembe.study.student.models.Student;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/students")
public class StudentsController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private StudentService studentService;
    private Object Student;

    public StudentsController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @ApiOperation(value = "Creates new student")
    public
    @ResponseBody
    ResponseEntity<com.ikoembe.study.student.models.Student> insert(@RequestBody Student student) throws ValidationException {
        final LocalDateTime localDateTime = LocalDateTime.now();
        logger.info("A new student{},{} created with schoolNumber : {}, createdDate {}: ",
                student.getFirstname(), student.getLastname(), student.getSchoolNumber(), localDateTime);
        this.studentService.insert(student);
        return ResponseEntity.ok(student);
    }

    @GetMapping(value = "/all")
    public List<Student> getAllStudents() {
        return this.studentService.findAll();
    }

    @GetMapping(value = "/schoolNumber/{schoolNumber}")
    public Optional<Student> getStudentByEmail(@PathVariable("schoolNumber") String schoolNumber) {
        return this.studentService.findStudentBySchoolNumber(schoolNumber);
    }

    @GetMapping(value = "/id/{id}")
    public Optional<Student> getStudentById(@PathVariable("id") String id) {
        return this.studentService.findStudentById(id);
    }

    @DeleteMapping(value = "/id/{id}")
    @ApiOperation(value = "Deletes user by id")
    public ResponseEntity<Void> deleteStudentById(@PathVariable("id") String id) throws ValidationException {
        if (Strings.isNullOrEmpty(id)) {
            throw new ValidationException("id can not be null or empty");
        }
        if (!studentService.findStudentById(id).isPresent()) {
            logger.info("User is not found. Make sure that id is correct");
            return ResponseEntity.notFound().build();
        }
        this.studentService.deleteStudentById(id);
        logger.info("user {} is deleted", id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/update/id/{id}")
    @ApiOperation(value = "Patches a student's information")
    public ResponseEntity<Student> patchStudentInfoById(@PathVariable String id, @RequestBody Map<String, Object> patches){
       //TODO Fix this bug
        Student student = studentService.findStudentById(id).orElseThrow(IllegalArgumentException::new);
        patches.forEach((k,v)-> {
            Field field = ReflectionUtils.findField(Student.class, k);
            field.setAccessible(true);
            ReflectionUtils.setField(field,student, v);
        });
        this.studentService.updateStudentInfo(student);
        return ResponseEntity.ok(student);
    }

    @PatchMapping(path = "/update/schoolNumber/{schoolNumber}")
    @ApiOperation(value = "Patches a student's information with student email")
    public ResponseEntity<Student> patchStudentInfoBySchoolAccount(@PathVariable String schoolNumber, @RequestBody Map<String, Object> patches){
        Student student = studentService.findStudentBySchoolNumber(schoolNumber).orElseThrow(IllegalArgumentException::new);
        patches.forEach((k,v)-> {
            Field field = ReflectionUtils.findField(Student.class, k);
            field.setAccessible(true);
            ReflectionUtils.setField(field,student, v);
        });
        this.studentService.updateStudentInfo(student);
        return ResponseEntity.ok(student);
    }

}
