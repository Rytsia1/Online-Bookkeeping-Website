package com.DTMK.Online.Bookkeeping.Website.Project.mapper;

import com.DTMK.Online.Bookkeeping.Website.Project.entity.SavingGoal;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface SavingGoalMapper {

    // Retrieves all saving goals for a user.
    @Select("SELECT * FROM t_saving_goal WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<SavingGoal> findByUserId(@Param("userId") Integer userId);

    // Creates a new saving goal.
    @Insert("INSERT INTO t_saving_goal(user_id, name, target, saved, note, completed) " +
            "VALUES(#{userId}, #{name}, #{target}, #{saved}, #{note}, #{completed})")
    void insert(SavingGoal goal);

    // Updates the saved amount and completed flag of a saving goal.
    @Update("UPDATE t_saving_goal SET saved = #{saved}, completed = (#{saved} >= target) WHERE id = #{id}")
    void updateSaved(@Param("id") Integer id, @Param("saved") BigDecimal saved);

    // Deletes a saving goal.
    @Delete("DELETE FROM t_saving_goal WHERE id = #{id}")
    void deleteById(@Param("id") Integer id);
}
