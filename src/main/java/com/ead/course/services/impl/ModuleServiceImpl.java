package com.ead.course.services.impl;

import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    ModuleRepository moduleRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Transactional
    @Override
    public void delete(ModuleModel moduleModel) {

        deleteLessons(moduleModel);

        moduleRepository.delete(moduleModel);
    }
    private void deleteLessons(ModuleModel moduleModel) {

        var lessonList = lessonRepository.findAllLessonsIntoModule(moduleModel.getModuleId());

        if (!lessonList.isEmpty()) {

            lessonRepository.deleteAll(lessonList);
        }
    }
}
