/*
 *  Copyright © 2019 by Seven System Viet Nam, JSC - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  *
 *  * Write clean code and you can sleep well at night ¯\_(ツ)_/¯
 *  *
 *  * Written by hoang.nh@ssv.com.vn, 2/2/23, 9:00 AM
 *
 */

package com.example.human_resources.transform;

import com.example.human_resources.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserTransform {

    public void putValueIntoEmployeeAndSupervisorMap(Map<String, Map<String, Boolean>> supervisorMap,
                                                     Map<String, String> employeeMap, Map<String, String> dto) {
        for (Map.Entry<String, String> entry : dto.entrySet()) {
            if (supervisorMap.get(entry.getKey()) != null) {
                String tmpSupervisor = employeeMap.get(entry.getValue());
                while (tmpSupervisor != null) {
                    tmpSupervisor = employeeMap.get(tmpSupervisor);
                }
            }
            employeeMap.put(entry.getKey(), entry.getValue());
            if (supervisorMap.get(entry.getValue()) == null) {
                Map<String, Boolean> tmpMap = new HashMap<>();
                tmpMap.put(entry.getKey(), true);
                supervisorMap.put(entry.getValue(), tmpMap);
            } else {
                supervisorMap.get(entry.getValue()).put(entry.getKey(), true);
            }
        }
    }

    public User retrieveRootUser(Map<String, Map<String, Boolean>> supervisorMap,
                                 Map<String, String> dto) {
        for (String key : supervisorMap.keySet()) {
            if (dto.get(key) == null) {
                return new User(key, key);
            }
        }
        return null;
    }

    public List<User> retrieveCreateEntities(String supervisorName, String supervisor,
                                              Map<String, Map<String, Boolean>> supervisorMap) {
        List<User> users = new ArrayList<>();
        users.add(new User(supervisorName, supervisor));

        if (supervisorMap.get(supervisorName) == null) {
            return users;
        }
        for (String key : supervisorMap.get(supervisorName).keySet()) {
            if (supervisorMap.get(key) != null) {
                users.addAll(retrieveCreateEntities(key, supervisorName, supervisorMap));
            } else {
                users.add(new User(key, supervisorName));
            }
        }
        return users;
    }

    public Map<String, Map> retrieveHierarchyReponse(User boss, List<User> employees) {
        Map<String, List<User>> users = employees.stream().filter(x -> !x.getSupervisor().equalsIgnoreCase(x.getName())).collect(Collectors.groupingBy(User::getSupervisor));

        Map<String, Map> result = new HashMap<>();
        result.put(boss.getName(), buildHierarchyReponse(boss.getName(), users));
        return result;
    }

    private Map<String, Map> buildHierarchyReponse(String employeeName, Map<String, List<User>> users) {
        Map<String, Map> result = new HashMap<>();
        if (users.get(employeeName) != null) {
            List<User> employees = users.get(employeeName);
            Map<String, Map> values = new HashMap<>();
            for (User employee : employees) {
                values.put(employee.getName(), buildHierarchyReponse(employee.getName(), users));
            }
            result = values;
        }
        return result;
    }

}
