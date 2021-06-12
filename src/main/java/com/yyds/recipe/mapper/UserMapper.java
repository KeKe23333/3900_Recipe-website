package com.yyds.recipe.mapper;

import com.yyds.recipe.model.LoginUser;
import com.yyds.recipe.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    void saveUser(User user);

    void saveUserAccount(User user);

    void editUser(User user);

    @Select("select * from user where userId = #{userId}")
    User getUserbyId(@Param(value = "userId") String userId);

    LoginUser getLoginUserInfo(String email);

    @Select("select password from user_account where user_id = #{userId}")
    String getPasswordByUserid(@Param(value = "userId") String userId);

    @Update("update user_account set password = #{password} where user_id = #{userId}")
    void changePassword(@Param(value = "userId") String userId, @Param(value = "password") String newPassword);
}
