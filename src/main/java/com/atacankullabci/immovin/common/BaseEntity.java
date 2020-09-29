package com.atacankullabci.immovin.common;

public abstract class BaseEntity {
    private String name;

    public BaseEntity() {
    }

    public BaseEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "name='" + name + '\'' +
                '}';
    }
}
