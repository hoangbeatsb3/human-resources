/*
 *  Copyright © 2019 by Seven System Viet Nam, JSC - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  *
 *  * Write clean code and you can sleep well at night ¯\_(ツ)_/¯
 *  *
 *  * Written by hoang.nh@ssv.com.vn, 1/29/23, 7:04 PM
 *
 */

package com.example.human_resources;

import com.example.human_resources.model.User;
import com.example.human_resources.repository.UserRepository;
import com.example.human_resources.service.UserServiceImpl;
import com.example.human_resources.transform.UserTransform;
import com.example.human_resources.validation.UserValidation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.InvalidParameterException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceUT {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidation userValidation;

    @Mock
    private UserTransform userTransform;

    @InjectMocks
    private UserServiceImpl userService;

    private final List<User> users = new ArrayList<>();
    private ObjectMapper objectMapper;
    private Map<String, Map> resultMap = new HashMap<>();

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
        users.add(new User("Jonas", "Jonas"));
        users.add(new User("Steve", "Chris"));
        users.add(new User("Chris", "Pete"));
        users.add(new User("Pete", "Nick"));
        users.add(new User("Nick", "Sophie"));
        users.add(new User("Sophie", "Jonas"));

        Map<String, String> secondaryLevelMap = new HashMap<>();
        secondaryLevelMap.put("Nick", "Pete");
        resultMap.put("Sophie", secondaryLevelMap);
    }

    @Test
    public void testRetrieveHierarchy() throws NotFoundException, JsonProcessingException {
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(userTransform.retrieveHierarchyReponse(Mockito.any(), Mockito.any())).thenReturn(resultMap);
        Map<String, Map> result = userService.retrieveHierarchy();

        Assert.assertNotNull(result);
        Assert.assertEquals("{\"Sophie\":{\"Nick\":\"Pete\"}}", objectMapper.writeValueAsString(result));
    }

    @Test
    public void testRetrieveHierarchyByNameAndLevel() throws NotFoundException, JsonProcessingException {
        List<User> employees = new ArrayList<>();
        employees.add(new User("Pete", "Nick"));
        employees.add(new User("Nick", "Sophie"));

        Mockito.when(userRepository.findRecursiveSupervisorByEmployeeName(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(employees);
        Mockito.when(userTransform.retrieveHierarchyReponse(Mockito.any(), Mockito.any())).thenReturn(resultMap);
        Map<String, Map> result = userService.retrieveHierarchyByNameAndLevel("Pete", 2);

        Assert.assertEquals("{\"Sophie\":{\"Nick\":\"Pete\"}}", objectMapper.writeValueAsString(result));
    }

    @Test(expected = NotFoundException.class)
    public void testRetrieveHierarchyByNameAndLevelNotFound() throws NotFoundException, JsonProcessingException {
        Mockito.when(userRepository.findRecursiveSupervisorByEmployeeName(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(new ArrayList<>());
        userService.retrieveHierarchyByNameAndLevel("Pete", 2);
    }

    @Test
    public void testCreateUser() {
        Mockito.doNothing().when(userRepository).deleteAll();
        Mockito.when(userRepository.saveAll(Mockito.any())).thenReturn(users);
        Mockito.when(userTransform.retrieveRootUser(Mockito.any(), Mockito.any())).thenReturn(new User("Jonas", "Jonas"));
        Map<String, String> body = new HashMap<>();
        body.put("Steve", "Chris");
        body.put("Chris", "Pete");
        body.put("Pete", "Nick");
        body.put("Barbara", "Nick");
        body.put("John", "Barbara");
        body.put("Nick", "Sophie");
        body.put("Sophie", "Jonas");
        body.put("Jack", "Jonas");
        body.put("Jim", "Jonas");
        List<User> result = userService.createUser(body);
        Assert.assertEquals(6, result.size());
        Assert.assertEquals("Jonas", result.get(0).getName());
    }

    @Test(expected = InvalidParameterException.class)
    public void testCreateUserFailedForEmpty() {
        Map<String, String> body = new HashMap<>();
        userService.createUser(body);
    }

    @Test(expected = InvalidParameterException.class)
    public void testCreateUserFailedForLoop() {
        Map<String, String> body = new HashMap<>();
        body.put("Steve", "Chris");
        body.put("Chris", "Pete");
        body.put("Pete", "Nick");
        body.put("Nick", "Steve");

        Mockito.when(userValidation.validateHierarchyRequest(body)).thenReturn("Line 4 cause loop hierarchy. Please fix it and try again!");

        userService.createUser(body);
    }

    @Test(expected = InvalidParameterException.class)
    public void testCreateUserFailedForMultipleRoot() {
        Map<String, String> body = new HashMap<>();
        body.put("Steve", "Chris");
        body.put("Chris", "Pete");
        body.put("Pete", "Nick");
        body.put("Jane", "Ronaldo");

        Mockito.when(userValidation.validateHierarchyRequest(body)).thenReturn("Hierarchy can't contains multiple root: [Ronaldo, Ronaldo]");

        userService.createUser(body);
    }
}
