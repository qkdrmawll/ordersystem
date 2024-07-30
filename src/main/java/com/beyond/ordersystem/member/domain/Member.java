package com.beyond.ordersystem.member.domain;

import com.beyond.ordersystem.common.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    @Embedded
    private Address address;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    @Column(nullable = false, columnDefinition = "char(1) default 'N'")
    private String delYn = "N";

}
