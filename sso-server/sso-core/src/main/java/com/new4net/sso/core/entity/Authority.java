package com.new4net.sso.core.entity;

import com.new4net.sso.api.dto.Auth;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@EqualsAndHashCode(of = {"authorityCode"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cache(region = "Authority", usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
public class Authority implements GrantedAuthority, Serializable {
    @Id
    protected String authorityCode;

    protected String authorityName;


    protected String remark;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "moduleId")
    private Module module;

    @OneToMany(targetEntity = AuthorityRelation.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "superAuthCode", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Set<AuthorityRelation> authorityRelations;


    public Auth getAuth() {
        if (module.isEnable())
            return Auth.builder().authority(authorityCode).remark(remark).authorityName(authorityName).authorityRelationInfos(authorityRelations == null ? null : authorityRelations.stream().map(authorityRelation -> {
                return authorityRelation.getAuthorityRelationInfo();
            }).collect(Collectors.toSet())).build();
        else
            return null;
    }

    @Override
    public String getAuthority() {
        return authorityCode;
    }

}
