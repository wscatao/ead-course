package com.ead.course.services.impl;

import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {

        var moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());

        if (!moduleModelList.isEmpty()) {

            deleteModules(moduleModelList);
        }

        courseRepository.delete(courseModel);
    }

    private void deleteModules(List<ModuleModel> moduleModelList) {

            moduleModelList.forEach(this::deleteLessons);

            moduleRepository.deleteAll(moduleModelList);
    }

    private void deleteLessons(ModuleModel module) {

        var lessonList = lessonRepository.findAllLessonsIntoModule(module.getModuleId());

        if (!lessonList.isEmpty()) {

            lessonRepository.deleteAll(lessonList);
        }
    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return courseRepository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return courseRepository.findById(courseId);
    }

    @Override
    public List<CourseModel> findAll() {

        return courseRepository.findAll();
    }
}
