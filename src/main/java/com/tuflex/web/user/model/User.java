package com.tuflex.web.user.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import com.tuflex.web.common.CommonVO;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Setter
@DynamicInsert
@Getter
public class User extends CommonVO {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 10)
    private String snsType;

    @Size(max = 120)
    private String snsId;

    @NotBlank
    @Size(max = 120)
    private String password;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_pid"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String email, String password, String name, String snsType, String snsId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.snsId = snsId;
        this.snsType = snsType;
    }
}