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

package com.example.human_resources.repository;

import com.example.human_resources.dto.ThreeLevelsUserDto;
import com.example.human_resources.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT new com.example.human_resources.dto.ThreeLevelsUserDto(u1.name as name, u2.name as supervisor, u2.supervisor as superSupervisor) " +
            "   FROM User u1 " +
            "   INNER JOIN User u2 " +
            "       ON u1.supervisor = u2.name " +
            "   WHERE  u1.name = :name ")
    ThreeLevelsUserDto findThreeLevelsByName(@Param("name") String name);
}
