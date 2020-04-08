package com.new4net.sso.core.entity;

import com.new4net.sso.api.dto.AuthorityRelationInfo;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Builder
@Entity
@Table(name = "AUTHORITYRELATION")
@Data
@EqualsAndHashCode(of = {"superAuthCode","subAuthCode"})
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Cache(region = "AuthorityRelation", usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable(true)
public class AuthorityRelation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private  String superAuthCode;
    private  String superAuthName;

    private  String subAuthCode;
    private  String subAuthName;

    public AuthorityRelationInfo buildAuthorityRelationInfo(){
        return AuthorityRelationInfo.builder().subAuthCode(subAuthCode).subAuthName(subAuthName).superAuthCode(superAuthCode).superAuthName(superAuthName).build();
    }
}
