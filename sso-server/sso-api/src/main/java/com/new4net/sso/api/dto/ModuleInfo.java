package com.new4net.sso.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder

public class ModuleInfo {
    private String moduleName;
    private boolean enable=true;
    private String remark;

    public ModuleInfo() {
    }

    private String superModuleName;
    Set<Auth> auths;

    public ModuleInfo(String moduleName, boolean enable, String remark, String superModuleName, Set<Auth> auths) {
        this.moduleName = moduleName;
        this.enable = enable;
        this.remark = remark;
        this.superModuleName = superModuleName;
        this.auths = auths;
    }
}
