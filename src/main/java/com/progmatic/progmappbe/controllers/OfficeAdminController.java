package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.services.OfficeAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class OfficeAdminController {

    private OfficeAdminService officeAdminService;

    @Autowired
    public OfficeAdminController(OfficeAdminService officeAdminService) {
        this.officeAdminService = officeAdminService;
    }

    @PostMapping("/class")
    public EntityCreationResult createClass(@RequestBody SchoolClassDTO sc){
        return officeAdminService.createSchoolClass(sc);
    }

    @PostMapping("/student")
    public EntityCreationResult createStudent(@RequestBody @Valid  UserDTO udto){
        return officeAdminService.createStudent(udto);
    }

    @PutMapping("/class/{classId}/students")
    public BasicResult assignStudentsToClass(@PathVariable("classId") String classId, @RequestBody StudentListDto students){
        return officeAdminService.assignStudentToClass(students, classId);
    }
}
