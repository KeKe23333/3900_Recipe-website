package com.yyds.recipe.mapper;

import com.yyds.recipe.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.HashMap;

@Mapper
public interface UserMapper {

    void saveUser(User user);

    void saveUserAccount(User user);

    void editUser(User user);

    @Update("update user_account set password = #{password} where user_id = #{userId}")
    void changePassword(@Param(value = "userId") String userId, @Param(value = "password") String newPassword);

    User getUserByEmail(String email);

    @Select("select user_id from recipe.user where email = #{email}")
    String getUserIdByEmail(@Param(value = "email") String email);

    User getUserByUserId(String userId);

    void followUser(String followingId, String followId);

    void unfollowUser(String followingId, String followId);
}
