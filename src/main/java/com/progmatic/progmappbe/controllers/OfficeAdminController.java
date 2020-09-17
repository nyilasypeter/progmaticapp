package com.progmatic.progmappbe.controllers;

import com.progmatic.progmappbe.dtos.*;
import com.progmatic.progmappbe.dtos.schoolclass.SchoolClassDTO;
import com.progmatic.progmappbe.dtos.user.UserSearchResponseDTO;
import com.progmatic.progmappbe.dtos.user.StudentListDto;
import com.progmatic.progmappbe.dtos.user.UserDTO;
import com.progmatic.progmappbe.dtos.user.UserSearchRequestDTO;
import com.progmatic.progmappbe.services.OfficeAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/class")
    public List<SchoolClassDTO> searchClass(SchoolClassDTO classFilter){
        return officeAdminService.searchClass(classFilter);
    }

    @PostMapping("/student")
    public EntityCreationResult createStudent(@RequestBody @Valid UserDTO udto){
        return officeAdminService.createStudent(udto);
    }

    @GetMapping("/user")
    public List<UserSearchResponseDTO> searchUser(UserSearchRequestDTO requestDTO){
        return officeAdminService.searchUser(requestDTO);
    }

    @PostMapping("/user")
    public EntityCreationResult createUser(@RequestBody @Valid  UserDTO udto){
        return officeAdminService.createUser(udto);
    }

    @PutMapping("/class/{classId}/students")
    public BasicResult assignStudentsToClass(@PathVariable("classId") String classId, @RequestBody StudentListDto students){
        return officeAdminService.assignStudentToClass(students, classId);
    }

    @DeleteMapping("/class/{classId}/students/{studentId}")
    public BasicResult removeStudentsFromClass(@PathVariable("classId") String classId, @PathVariable("studentId")  String students){
        return officeAdminService.removeStudentsFromClass(students, classId);
    }

    @PutMapping("/user/newreglink/{userId}")
    public BasicResult updateRegistrationLink(@PathVariable("userId") String userId){
        return officeAdminService.updateRegistrationLink(userId);
    }
}
