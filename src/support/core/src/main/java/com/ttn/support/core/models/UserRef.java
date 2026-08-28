package com.ttn.support.core.models;

import java.util.Objects;

public class UserRef {

    private String id;
    private String name;
    private String email;
    private String role;

    public UserRef() {
    }

    public UserRef(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserRef)) {
            return false;
        }
        UserRef userRef = (UserRef) o;
        return Objects.equals(id, userRef.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
