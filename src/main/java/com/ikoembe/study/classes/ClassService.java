package com.ikoembe.study.classes;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassService {
    private ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public void insert(Classes classes){
        this.classRepository.insert(classes);

    }

    public List<Classes> findAll() {
        List<Classes> classesList = this.classRepository.findAll();
        return classesList;
    }

    public List<Classes> findAllByClassName(String className){
        return this.classRepository.findByClassName(className);
    }

}
