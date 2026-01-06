package com.example.waxingweb.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; //아이디

    @Column(nullable = false)
    private String password; //비번

    @Column(nullable = false)
    private String name;     //성함

    @Column(nullable = false, unique = true)
    private String email;    //이메일

    @Column(nullable = false, unique = true)
    private String phone;    //폰번호

    @Column(nullable = false)
    private String address;  //주소

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    public void setDefaultRole() {
        if (this.role == null) {
            this.role = Role.USER; //
        }
    }

}
