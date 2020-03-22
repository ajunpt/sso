package com.new4net.sso.core.entity;

import com.new4net.sso.api.dto.UserInfo;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.redisson.config.Config;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Cacheable(true)
@org.hibernate.annotations.Cache(region = "User", usage = CacheConcurrencyStrategy.READ_WRITE)

public class User  implements UserDetails, Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
    @Column(unique = true)
    private String username;
    private String password;
    private boolean accountNonExpired;//账号是否未过期
    private boolean accountNonLocked; //账号是否未锁定
    private boolean credentialsNonExpired;//密码是否未过期
    private boolean enable; //是否激活
    private String email;
    private String mobile;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_authority",joinColumns={@JoinColumn(name="user_id")},inverseJoinColumns = {@JoinColumn(name = "authority_id")})
    private Set<Authority> authorities;
    private Date validTime;
    private Date invalidTime;
    public UserInfo getUserInfo(){

        return UserInfo.builder().username(username).password(password).accountNonExpired(accountNonExpired)
                .accountNonLocked(accountNonLocked).credentialsNonExpired(credentialsNonExpired).enable(enable)
                .authorities(authorities==null?null:authorities.stream().map(authority -> {return authority.getAuth();}).collect(Collectors.toSet())).build();
    }

    public Set<Authority> getAuthorities(){

        return authorities;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enable;
    }
}
