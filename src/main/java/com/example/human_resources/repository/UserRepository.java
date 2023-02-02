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

import com.example.human_resources.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "with recursive users (lvl, name, supervisor) as (" +
            "    select 0 as lvl, name, supervisor" +
            "    from user where name = :name" +
            "  union all" +
            "    select users.lvl + 1, user.name, user.supervisor" +
            "    from" +
            "      users join" +
            "      user on users.supervisor = user.name" +
            "    where lvl < :level" +
            "    order by users.lvl+1 desc, user.name) " +
            " select name, supervisor" +
            "   from users", nativeQuery = true)
    List<User> findRecursiveSupervisorByEmployeeName(@Param("name") String name, @Param("level") Integer level);
}
