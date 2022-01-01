package com.ikoembe.study.teachers;

import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    private TeachersRepository teachersRepository;

    public TeacherService(TeachersRepository teachersRepository) {
        this.teachersRepository = teachersRepository;
    }

    public void insert(Teacher teacher){
        this.teachersRepository.insert(teacher);
    }

}
