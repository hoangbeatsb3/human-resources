package com.example.human_resources.controller;

import com.example.human_resources.dto.ThreeLevelsUserDto;
import com.example.human_resources.model.User;
import com.example.human_resources.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/users")
public class UsersController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping(path = "")
	public Map<String, Map> getUsers() throws NotFoundException {
		return userService.retrieveHierarchy();
	}

	@GetMapping(path = "/supervisor")
	public ThreeLevelsUserDto getThreeLevelUserByName(@RequestParam("name") String name) throws JsonProcessingException {
		return userService.retrieveThreeLevelsSupervisor(name);
	}

	@PostMapping(path = "")
	public List<User> createUsers(@RequestBody Object dto) {
		return userService.createUser((Map<String, String>) dto);
	}
	

}
