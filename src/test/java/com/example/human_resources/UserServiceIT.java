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
import com.example.human_resources.dto.ThreeLevelsUserDto;
import com.example.human_resources.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.*;

@SpringBootTest(classes = HumanResoucesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceIT {

    @LocalServerPort
    private int port;

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
    public void testCreateUser()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity =
                new HttpEntity<>(buildPayload(), headers);
        ResponseEntity<List<User>> response =
                restTemplate
                        .withBasicAuth("admin", "admin")
                        .exchange("http://localhost:" + port + "/users", HttpMethod.POST, requestEntity,
                                new ParameterizedTypeReference<List<User>>() {}
                        );

        Assertions.assertEquals(10, Objects.requireNonNull(response.getBody()).size());
        Assertions.assertEquals("Jonas", response.getBody().get(0).getName());
    }

    @Test
    public void testCreateUserFailedForEmpty()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity =
                new HttpEntity<>(new HashMap<>(), headers);
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/users", HttpMethod.POST, requestEntity,
                        String.class
                );

        Assertions.assertEquals("Body can't be empty", response.getBody());
    }

    @Test
    public void testCreateUserFailedForLoop()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity =
                new HttpEntity<>(buildPayloadLoop(), headers);
        ResponseEntity<String> response = restTemplate
                    .withBasicAuth("admin", "admin")
                    .exchange("http://localhost:" + port + "/users", HttpMethod.POST, requestEntity,
                            String.class
                    );

        Assertions.assertEquals("Nick already is supervisor of Steve", response.getBody());
    }

    @Test
    public void testCreateUserFailedForMultipleRoot()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity =
                new HttpEntity<>(buildPayloadMultipleRoot(), headers);
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/users", HttpMethod.POST, requestEntity,
                        String.class
                );

        Assertions.assertEquals("Hierarchy can't contains multiple root: [Ronaldo, Nick]", response.getBody());
    }

    @Test
    public void testRetrieveHierarchy() {
        dbInitializeConfig.initialize();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity =
                new HttpEntity<>(buildPayload(), headers);
        restTemplate
            .withBasicAuth("admin", "admin")
            .exchange("http://localhost:" + port + "/users", HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<List<User>>() {}
            );

        ResponseEntity<Map<String, Map>> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/users", HttpMethod.GET, null,
                        new ParameterizedTypeReference<Map<String, Map>>() {}
                );

        Map<String, Map> result = response.getBody();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(3, result.get("Jonas").size());
    }

    @Test
    public void testRetrieveThreeLevelsSupervisor() {
        dbInitializeConfig.initialize();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity =
                new HttpEntity<>(buildPayload(), headers);
        restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/users", HttpMethod.POST, requestEntity,
                        new ParameterizedTypeReference<List<User>>() {}
                );

        ResponseEntity<ThreeLevelsUserDto> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/users/supervisor?name=Nick", HttpMethod.GET, null,
                        new ParameterizedTypeReference<ThreeLevelsUserDto>() {}
                );

        ThreeLevelsUserDto result = response.getBody();
        Assertions.assertEquals("Nick", result.getName());
        Assertions.assertEquals("Sophie", result.getSupervisor());
        Assertions.assertEquals("Jonas", result.getSuperSupervisor());
    }

}
