/*
 *  Copyright © 2019 by Seven System Viet Nam, JSC - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  *
 *  * Write clean code and you can sleep well at night ¯\_(ツ)_/¯
 *  *
 *  * Written by hoang.nh@ssv.com.vn, 1/28/23, 5:23 PM
 *
 */

package com.example.human_resources.service;

import com.example.human_resources.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import javassist.NotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     *
     * @param dto
     * @return
     */
    List<User> createUser(Map<String, String> dto);

    /**
     *
     * @return
     * @throws NotFoundException
     */
    Map<String, Map> retrieveHierarchy() throws NotFoundException;

    /**
     *
     * @param name
     * @return
     * @throws JsonProcessingException
     */
    Map<String, Map> retrieveHierarchyByNameAndLevel(String name, Integer level) throws NotFoundException;
}
