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

import com.example.human_resources.dto.ThreeLevelsUserDto;
import com.example.human_resources.model.User;
import com.example.human_resources.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public List<User> createUser(Map<String, String> dto) {
        if (dto.size() == 0) {
            throw new InvalidParameterException("Body can't be empty");
        }
        Map<String, Map<String, Boolean>> smap = new HashMap<>();
        Map<String, String> emap = new HashMap<>();

        for (Map.Entry<String, String> entry : dto.entrySet()) {
            if (smap.get(entry.getKey()) != null) {

                String tmpSupervisor = emap.get(entry.getValue());
                while (tmpSupervisor != null) {
                    if (tmpSupervisor.equals(entry.getKey())) {
                        throw new InvalidParameterException(entry.getKey() + " already is supervisor of " + entry.getValue());
                    }
                    tmpSupervisor = emap.get(tmpSupervisor);
                }
            }

            emap.put(entry.getKey(), entry.getValue());
            if (smap.get(entry.getValue()) == null) {
                Map<String, Boolean> tmpMap = new HashMap<>();
                tmpMap.put(entry.getKey(), true);
                smap.put(entry.getValue(), tmpMap);
            } else {
                smap.get(entry.getValue()).put(entry.getKey(), true);
            }
        }

        List<User> users = new ArrayList<>();
        String root = "";

        for (String key : smap.keySet()) {
            if (dto.get(key) == null) {
                if (!root.equals("")) {
                    throw new InvalidParameterException("Hierarchy can't contains multiple root: [" +
                            root + ", " + key + "]");
                }
                root = key;
                users.add(new User(root, key));
            }
        }

        Map<String, Boolean> hierarchyMap = smap.get(root);
        if (hierarchyMap != null) {
            for (String key : hierarchyMap.keySet()) {
                users.addAll(retrieveCreateEntities(key, root, smap));
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
        User boss = optionalUser.get();
        Map<String, List<User>> users = employees.stream().filter(x -> !x.getSupervisor().equalsIgnoreCase(x.getName())).collect(Collectors.groupingBy(User::getSupervisor));

        Map<String, Map> result = new HashMap<>();
        result.put(boss.getName(), retrieveReponse(boss.getName(), users));

        return result;
    }

    @Override
    public ThreeLevelsUserDto retrieveThreeLevelsSupervisor(String name) throws JsonProcessingException {
        return userRepository.findThreeLevelsByName(name);
    }


    private Map<String, Map> retrieveReponse(String employeeName, Map<String, List<User>> users) {
        Map<String, Map> result = new HashMap<>();

        if (users.get(employeeName) != null) {
            List<User> employees = users.get(employeeName);
            Map<String, Map> values = new HashMap<>();
            for (User employee : employees) {
                values.put(employee.getName(), retrieveReponse(employee.getName(), users));
            }
            result = values;
        }

        return result;
    }

    private List<User> retrieveCreateEntities(String sKey, String supervisor, Map<String, Map<String, Boolean>> smap) {
        List<User> users = new ArrayList<>();
        users.add(new User(sKey, supervisor));

        if (smap.get(sKey) == null) {
            return users;
        }

        for (String key : smap.get(sKey).keySet()) {
            if (smap.get(key) != null) {
                users.addAll(retrieveCreateEntities(key, sKey, smap));
            } else {
                users.add(new User(key, sKey));
            }
        }

        return users;
    }

    private boolean validateCreateUser(Map<String, String> body) {
        boolean result = false;


        return result;
    }
}
