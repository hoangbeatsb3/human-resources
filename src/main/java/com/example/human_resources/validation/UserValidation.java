/*
 *  Copyright © 2019 by Seven System Viet Nam, JSC - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  *
 *  * Write clean code and you can sleep well at night ¯\_(ツ)_/¯
 *  *
 *  * Written by hoang.nh@ssv.com.vn, 2/2/23, 8:54 AM
 *
 */

package com.example.human_resources.validation;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserValidation {

    public String validateHierarchyRequest(Map<String, String> dto) {
        Map<String, Map<String, Boolean>> supervisorMap = new HashMap<>();
        Map<String, String> employeeMap = new HashMap<>();
        Integer lineCount = 0;
        for (Map.Entry<String, String> entry : dto.entrySet()) {
            lineCount++;
            if (supervisorMap.get(entry.getKey()) != null) {
                String tmpSupervisor = employeeMap.get(entry.getValue());
                while (tmpSupervisor != null) {
                    if (tmpSupervisor.equals(entry.getKey())) {
                        return "Line " + lineCount + " cause loop hierarchy. Please fix it and try again!";
                    }
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

        String root = "";
        for (String key : supervisorMap.keySet()) {
            if (dto.get(key) == null) {
                if (!root.equals("")) {
                    return "Hierarchy can't contains multiple root: [" +
                            root + ", " + key + "]";
                }
                root = key;
            }
        }
        return null;
    }

}
