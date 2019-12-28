package com.new4net.sso.core.entity;

import com.new4net.sso.api.dto.AuthorityRelationInfo;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Entity
@Table(name = "AUTHORITYRELATION")
@Data
@EqualsAndHashCode(of = {"superAuthCode","subAuthCode"})
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityRelation {

    @Id

    private String id;

    private  String superAuthCode;
    private  String superAuthName;

    private  String subAuthCode;
    private  String subAuthName;

    public AuthorityRelationInfo getAuthorityRelationInfo(){
        return AuthorityRelationInfo.builder().subAuthCode(subAuthCode).subAuthName(subAuthName).superAuthCode(superAuthCode).superAuthName(superAuthName).build();
    }
}
