package com.wasd.smartWMS.web.dto;

import com.wasd.smartWMS.domain.admins.Role;
import java.io.Serializable;

public class SessionAdmin implements Serializable {

    private Long id;
    private String username;
    private Role role;

    public SessionAdmin() {}  // 기본 생성자

    // Admins 엔티티를 받아서 DTO로 변환하는 생성자
    public SessionAdmin(com.wasd.smartWMS.domain.admins.Admins admin) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.role = admin.getRole();
    }

    // getter, setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
