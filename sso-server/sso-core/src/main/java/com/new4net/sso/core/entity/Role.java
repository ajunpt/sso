package com.new4net.sso.core.entity;

import com.new4net.sso.api.dto.Auth;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;
import java.util.TreeSet;



@Entity
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("role")
@EqualsAndHashCode(of = {"roleCode"})
public class Role extends Authority {
    @Column(unique = true)
    private String roleCode;
    private String roleName;

    //@JoinTable(name="role_authority",joinColumns={@JoinColumn(name="role_id")},inverseJoinColumns = {@JoinColumn(name = "authority_id")})


    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.authorityCode=roleCode;
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.authorityName=roleName;
        this.roleName = roleName;
    }


}
