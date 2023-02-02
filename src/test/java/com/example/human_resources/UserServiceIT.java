/*
 *  Copyright © 2019 by Seven System Viet Nam, JSC - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  *
 *  * Write clean code and you can sleep well at night ¯\_(ツ)_/¯
 *  *
 *  * Written by hoang.nh@ssv.com.vn, 1/29/23, 10:28 PM
 *
 */

package com.example.human_resources;

import com.example.human_resources.configuration.db.DBInitializeConfig;
import com.example.human_resources.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceIT extends AbstractBaseTestIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DBInitializeConfig dbInitializeConfig;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, String> buildPayload() {
        Map<String, String> payload = new HashMap<>();
        payload.put("Steve", "Chris");
        payload.put("Chris", "Pete");
        payload.put("Pete", "Nick");
        payload.put("Barbara", "Nick");
        payload.put("John", "Barbara");
        payload.put("Nick", "Sophie");
        payload.put("Sophie", "Jonas");
        payload.put("Jack", "Jonas");
        payload.put("Jim", "Jonas");
        return payload;
    }

    public Map<String, String> buildPayloadLoop() {
        Map<String, String> payload = new HashMap<>();
        payload.put("Steve", "Chris");
        payload.put("Chris", "Pete");
        payload.put("Pete", "Nick");
        payload.put("Nick", "Steve");
        return payload;
    }

    public Map<String, String> buildPayloadMultipleRoot() {
        Map<String, String> payload = new HashMap<>();
        payload.put("Steve", "Chris");
        payload.put("Chris", "Pete");
        payload.put("Pete", "Nick");
        payload.put("Jane", "Ronaldo");
        return payload;
    }

    @Test
    public void testCreateUser() throws JsonProcessingException {
        ResponseEntity<List<User>> response =
                restTemplate
                        .withBasicAuth("admin", "admin")
                        .exchange(getUrl() + "/users", HttpMethod.POST,
                                buildRequestEntity(buildPayload()),
                                new ParameterizedTypeReference<List<User>>() {}
                        );
        Assertions.assertEquals("[{\"name\":\"Jonas\",\"supervisor\":\"Jonas\"},{\"name\":\"Sophie\",\"supervisor\":\"Jonas\"},{\"name\":\"Nick\",\"supervisor\":\"Sophie\"},{\"name\":\"Pete\",\"supervisor\":\"Nick\"},{\"name\":\"Chris\",\"supervisor\":\"Pete\"},{\"name\":\"Steve\",\"supervisor\":\"Chris\"},{\"name\":\"Barbara\",\"supervisor\":\"Nick\"},{\"name\":\"John\",\"supervisor\":\"Barbara\"},{\"name\":\"Jack\",\"supervisor\":\"Jonas\"},{\"name\":\"Jim\",\"supervisor\":\"Jonas\"}]", objectMapper.writeValueAsString(response.getBody()));
    }

    @Test
    public void testCreateUserFailedForEmpty()
    {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(getUrl() + "/users", HttpMethod.POST,
                        buildRequestEntity(new HashMap<>()),
                        String.class
                );
        Assertions.assertEquals("Body can't be empty", response.getBody());
    }

    @Test
    public void testCreateUserFailedForLoop()
    {
        ResponseEntity<String> response = restTemplate
                    .withBasicAuth("admin", "admin")
                    .exchange(getUrl() + "/users", HttpMethod.POST,
                            buildRequestEntity(buildPayloadLoop()),
                            String.class
                    );
        Assertions.assertEquals("Line 4 cause loop hierarchy. Please fix it and try again!", response.getBody());
    }

    @Test
    public void testCreateUserFailedForMultipleRoot()
    {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(getUrl() + "/users", HttpMethod.POST,
                        buildRequestEntity(buildPayloadMultipleRoot()),
                        String.class
                );
        Assertions.assertEquals("Hierarchy can't contains multiple root: [Ronaldo, Nick]", response.getBody());
    }

    @Test
    public void testRetrieveHierarchy() throws JsonProcessingException {
        dbInitializeConfig.initialize();
        initEmployeeHierarchy();
        ResponseEntity<Map<String, Map>> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(getUrl() + "/users", HttpMethod.GET, null,
                        new ParameterizedTypeReference<Map<String, Map>>() {}
                );
        Assertions.assertEquals("{\"Jonas\":{\"Sophie\":{\"Nick\":{\"Pete\":{\"Chris\":{\"Steve\":{}}},\"Barbara\":{\"John\":{}}}},\"Jack\":{},\"Jim\":{}}}", objectMapper.writeValueAsString(response.getBody()));
    }

    @Test
    public void testRetrieveHierarchyByNameAndLevel() throws JsonProcessingException {
        dbInitializeConfig.initialize();
        initEmployeeHierarchy();
        ResponseEntity<Map<String, Map>> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(getUrl() + "/users/supervisor?name=Pete&level=3", HttpMethod.GET, null,
                        new ParameterizedTypeReference<Map<String, Map>>() {}
                );
        Assertions.assertEquals("{\"Jonas\":{\"Sophie\":{\"Nick\":{\"Pete\":{}}}}}", objectMapper.writeValueAsString(response.getBody()));
    }

    @Test
    public void testRetrieveHierarchyByNameAndLevelNotFound() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(getUrl() + "/users/supervisor?name=Jennifer&level=3", HttpMethod.GET, null, String.class
                );
        Assertions.assertEquals("Employee and Supervisor could not be found", response.getBody());
    }

    private void initEmployeeHierarchy() {
        restTemplate
                .withBasicAuth("admin", "admin")
                .exchange(getUrl() + "/users", HttpMethod.POST,
                        buildRequestEntity(buildPayload()),
                        new ParameterizedTypeReference<List<User>>() {}
                );
    }

}
