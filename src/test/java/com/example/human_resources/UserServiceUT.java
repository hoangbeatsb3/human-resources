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

import com.example.human_resources.dto.ThreeLevelsUserDto;
import com.example.human_resources.model.User;
import com.example.human_resources.repository.UserRepository;
import com.example.human_resources.service.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    @InjectMocks
    private UserServiceImpl userService;

    private final List<User> users = new ArrayList<>();

    @Before
    public void setup() {
        users.add(new User("Jonas", "Jonas"));
        users.add(new User("Steve", "Chris"));
        users.add(new User("Chris", "Pete"));
        users.add(new User("Pete", "Nick"));
        users.add(new User("Nick", "Sophie"));
        users.add(new User("Sophie", "Jonas"));
    }

    @Test
    public void testRetrieveHierarchy() throws NotFoundException {
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Map<String, Map> result = userService.retrieveHierarchy();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1, result.get("Jonas").size());
        Assert.assertEquals(new HashSet<String>(Collections.singleton("Sophie")), result.get("Jonas").keySet());

    }

    @Test
    public void testRetrieveThreeLevelsSupervisor() throws JsonProcessingException {
        ThreeLevelsUserDto threeLevelsUserDto = new ThreeLevelsUserDto("Nick", "Sophie", "Jonas");

        Mockito.when(userRepository.findThreeLevelsByName(Mockito.anyString()))
                .thenReturn(threeLevelsUserDto);
        ThreeLevelsUserDto result = userService.retrieveThreeLevelsSupervisor("Nick");

        Assert.assertEquals("Nick", result.getName());
        Assert.assertEquals("Sophie", result.getSupervisor());
        Assert.assertEquals("Jonas", result.getSuperSupervisor());
    }

    @Test
    public void testRetrieveTwoLevelsSupervisor() throws JsonProcessingException {
        ThreeLevelsUserDto threeLevelsUserDto = new ThreeLevelsUserDto("Nick", "Sophie", "");

        Mockito.when(userRepository.findThreeLevelsByName(Mockito.anyString()))
                .thenReturn(threeLevelsUserDto);
        ThreeLevelsUserDto result = userService.retrieveThreeLevelsSupervisor("Nick");

        Assert.assertEquals("Nick", result.getName());
        Assert.assertEquals("Sophie", result.getSupervisor());
        Assert.assertEquals("", result.getSuperSupervisor());
    }

    @Test
    public void testCreateUser() {
        Mockito.doNothing().when(userRepository).deleteAll();
        Mockito.when(userRepository.saveAll(Mockito.any())).thenReturn(users);

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

        userService.createUser(body);
    }

    @Test(expected = InvalidParameterException.class)
    public void testCreateUserFailedForMultipleRoot() {
        Map<String, String> body = new HashMap<>();
        body.put("Steve", "Chris");
        body.put("Chris", "Pete");
        body.put("Pete", "Nick");
        body.put("Jane", "Ronaldo");

        userService.createUser(body);
    }
}
