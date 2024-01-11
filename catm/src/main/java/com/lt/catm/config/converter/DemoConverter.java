package com.lt.catm.config.converter;

import com.lt.catm.entity.User;
import com.lt.catm.entity.UserType;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Instant;

@ReadingConverter //读取数据库数据的时候，把Row 转换称 UserType
public class DemoConverter implements Converter<Row, UserType> {

    @Override
    public UserType convert(Row source) {
        UserType userType = new UserType();
        userType.setId(source.get("id", Long.class));
        userType.setUserType(source.get("user_type", Integer.class));
        userType.setCreateTime(source.get("create_time", Instant.class));

        User user = new User();
        user.setUserName(source.get("user_name", String.class));
        user.setAge(source.get("age", Integer.class));
        user.setId(source.get("user_id", Long.class));
        user.setTypeId(source.get("id", Long.class));

        userType.setUser(user);
        return userType;
    }
}
