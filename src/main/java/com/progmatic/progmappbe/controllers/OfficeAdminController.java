package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.BasicResult;
import com.progmatic.progmappbe.dtos.SchoolClassDTO;
import com.progmatic.progmappbe.dtos.StudentListDto;
import com.progmatic.progmappbe.dtos.UserDTO;
import com.progmatic.progmappbe.services.OfficeAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OfficeAdminController {

    private OfficeAdminService officeAdminService;

    @Autowired
    public OfficeAdminController(OfficeAdminService officeAdminService) {
        this.officeAdminService = officeAdminService;
    }

    @PostMapping("/class")
    public SchoolClassDTO createClass(@RequestBody SchoolClassDTO sc){
        return officeAdminService.createSchoolClass(sc);
    }

    @PostMapping("/student")
    public UserDTO createStudent(@RequestBody UserDTO udto){
        return officeAdminService.createStudent(udto);
    }

    @PutMapping("/class/{classId}/students")
    public BasicResult assigntStudentsToClass(@PathVariable("classId") String classId, @RequestBody StudentListDto students){
        return officeAdminService.assignStudentToClass(students, classId);
    }
}
