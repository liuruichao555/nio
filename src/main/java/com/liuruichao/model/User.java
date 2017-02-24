package com.liuruichao.model;

import java.io.Serializable;

/**
 * User
 *
 * @author liuruichao
 * @date 15/7/18 下午4:14
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
