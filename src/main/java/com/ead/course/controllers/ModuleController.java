package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    ModuleService moduleService;

    @Autowired
    CourseService courseService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@PathVariable(value = "courseId") UUID courseId,
                                             @RequestBody @Valid ModuleDto moduleDto) {

        log.debug("POST saveModule moduleDto received {} ", moduleDto.toString());

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found.");
        }

        var moduleModel = new ModuleModel();

        BeanUtils.copyProperties(moduleDto, moduleModel);

        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));

        moduleModel.setCourse(courseModelOptional.get());

        var module = moduleService.save(moduleModel);

        log.debug("POST saveModule moduleId saved {} ", moduleModel.getModuleId());

        log.info("Module saved successfully moduleId {} ", moduleModel.getModuleId());

        return ResponseEntity.status(HttpStatus.CREATED).body(module);
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId) {

        log.debug("DELETE deleteModule moduleId received {} ", moduleId);

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);

        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found for this course.");
        }

        moduleService.delete(moduleModelOptional.get());

        log.debug("DELETE deleteModule moduleId deleted {} ", moduleId);

        log.info("Module deleted successfully moduleId {} ", moduleId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Module deleted successfully for this course.");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId,
                                               @RequestBody @Valid ModuleDto moduleDto) {

        log.debug("PUT updateModule moduleDto received {} ", moduleDto.toString());

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);

        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found for this course.");
        }

        var moduleModel = moduleModelOptional.get();

        moduleModel.setTitle(moduleDto.getTitle());

        moduleModel.setDescription(moduleDto.getDescription());

        var module = moduleService.save(moduleModel);

        log.debug("PUT updateModule moduleId saved {} ", moduleModel.getModuleId());

        log.info("Module updated successfully moduleId {} ", moduleModel.getModuleId());

        return ResponseEntity.status(HttpStatus.OK).body(module);
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModules(
            SpecificationTemplate.ModuleSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable,
            @PathVariable(value = "courseId") UUID courseId) {

        var moduleModelPage = moduleService
                .findAllByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(moduleModelPage);
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(@PathVariable(value = "courseId") UUID courseId,
                                               @PathVariable(value = "moduleId") UUID moduleId) {

        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);

        if (!moduleModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found for this course.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());

    }
}
