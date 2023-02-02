/*
 *  Copyright © 2019 by Seven System Viet Nam, JSC - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  *
 *  * Write clean code and you can sleep well at night ¯\_(ツ)_/¯
 *  *
 *  * Written by hoang.nh@ssv.com.vn, 1/28/23, 5:24 PM
 *
 */

package com.example.human_resources.service;

import com.example.human_resources.model.User;
import com.example.human_resources.repository.UserRepository;
import com.example.human_resources.transform.UserTransform;
import com.example.human_resources.validation.UserValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserValidation userValidation;

    @Autowired
    private UserTransform userTransform;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public List<User> createUser(Map<String, String> dto) {
        if (dto.size() == 0) {
            throw new InvalidParameterException("Body can't be empty");
        }
        Map<String, Map<String, Boolean>> supervisorMap = new HashMap<>();
        Map<String, String> employeeMap = new HashMap<>();

        String errorMsg = userValidation.validateHierarchyRequest(dto);
        if (errorMsg != null) {
            throw new InvalidParameterException(errorMsg);
        }

        userTransform.putValueIntoEmployeeAndSupervisorMap(supervisorMap, employeeMap, dto);
        User rootSupervisor = userTransform.retrieveRootUser(supervisorMap, dto);
        List<User> users = new ArrayList<>();
        String root = rootSupervisor.getName();
        users.add(rootSupervisor);

        Map<String, Boolean> hierarchyMap = supervisorMap.get(root);
        if (hierarchyMap != null) {
            for (String key : hierarchyMap.keySet()) {
                users.addAll(userTransform.retrieveCreateEntities(key, root, supervisorMap));
            }
        }
        userRepository.deleteAll();
        return userRepository.saveAll(users);
    }

    @Override
    public Map<String, Map> retrieveHierarchy() throws NotFoundException {
        List<User> employees = userRepository.findAll();
        Optional<User> optionalUser = employees.stream()
                .filter(x -> x.getName().equalsIgnoreCase(x.getSupervisor()))
                .findFirst();
        if (!optionalUser.isPresent()) {
            throw new NotFoundException("User not found");
        }
        return userTransform.retrieveHierarchyReponse(optionalUser.get(), employees);
    }

    @Override
    public Map<String, Map> retrieveHierarchyByNameAndLevel(String name, Integer level) throws NotFoundException {
        if (level == null) {
            level = 3;
        }
        List<User> employees = userRepository.findRecursiveSupervisorByEmployeeName(name, level);
        if (CollectionUtils.isEmpty(employees)) {
            throw new NotFoundException("Employee and Supervisor could not be found");
        }
        return userTransform.retrieveHierarchyReponse(employees.get(employees.size() - 1), employees);
    }

}
