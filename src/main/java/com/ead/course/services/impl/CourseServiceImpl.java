package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseUserRepository courseUserRepository;

    @Autowired
    private AuthUserClient authUserClient;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {

        boolean deleteCourseUserInAuthUser = false;

        var moduleModelList = moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());

        if (!moduleModelList.isEmpty()) {

            deleteModules(moduleModelList);
        }

        var courseUserModelList = courseUserRepository
                .findAllCourseUserIntoCourse(courseModel.getCourseId());

        if (!courseUserModelList.isEmpty()) {
            courseUserRepository.deleteAll(courseUserModelList);
            deleteCourseUserInAuthUser = true;
        }

        courseRepository.delete(courseModel);

        if (deleteCourseUserInAuthUser) {
            authUserClient.deleteCourseInAuthUser(courseModel.getCourseId());
        }
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
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {

        return courseRepository.findAll(spec, pageable);
    }
}
