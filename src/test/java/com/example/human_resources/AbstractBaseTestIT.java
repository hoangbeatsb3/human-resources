/*
 *  Copyright © 2019 by Seven System Viet Nam, JSC - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  *
 *  * Write clean code and you can sleep well at night ¯\_(ツ)_/¯
 *  *
 *  * Written by hoang.nh@ssv.com.vn, 2/2/23, 12:31 PM
 *
 */

package com.example.human_resources;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

@SpringBootTest(classes = HumanResoucesApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractBaseTestIT {

    @LocalServerPort
    private int port;

    public String getUrl() {
        return "http://localhost:" + port;
    }

    public HttpEntity<Object> buildRequestEntity(Map<String, String> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(payload, headers);
    }

}
