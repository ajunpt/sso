package com.new4net.sso.api.dto;

import lombok.*;

@Builder
@Data

@EqualsAndHashCode(of = {"superAuthCode","subAuthCode"})
public class AuthorityRelationInfo {
    private  String superAuthCode;
    private  String superAuthName;
    private  String subAuthCode;
    private  String subAuthName;

    public AuthorityRelationInfo(String superAuthCode, String superAuthName, String subAuthCode, String subAuthName) {
        this.superAuthCode = superAuthCode;
        this.superAuthName = superAuthName;
        this.subAuthCode = subAuthCode;
        this.subAuthName = subAuthName;
    }

    public AuthorityRelationInfo() {
    }
}
