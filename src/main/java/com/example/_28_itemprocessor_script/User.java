package com.example._28_itemprocessor_script;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    private Long id;
    @NotBlank(message = "用戶名不能為空")
    private String name;
    private int age;
}
