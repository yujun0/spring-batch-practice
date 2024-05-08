package com.example._22_itemReader_flat_mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class UserFieldMapper implements FieldSetMapper<User> {

    @Override
    public User mapFieldSet(FieldSet fieldSet) throws BindException {
        User user = new User();
        user.setId(fieldSet.readLong("id"));
        user.setAge(fieldSet.readInt("age"));
        user.setName(fieldSet.readString("name"));
        String address = fieldSet.readString("area") + " " + fieldSet.readString("city") + " " + fieldSet.readString("district");
        user.setAddress(address);

        return user;
    }
}
