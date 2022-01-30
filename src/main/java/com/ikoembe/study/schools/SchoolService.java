package com.ikoembe.study.schools;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolService {
    private SchoolRepository schoolRepository;

    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    public void insert(School school){
        this.schoolRepository.insert(school);

    }

    public List<School> findAll() {
        List<School> schoolList = this.schoolRepository.findAll();
        return schoolList;
    }

    public List<School> findAllByClassName(String className){
        return this.schoolRepository.findByClassName(className);
    }

    public List<School> findAllBySchoolName(String className){
        return this.schoolRepository.findByClassName(className);
    }

}
