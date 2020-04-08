package com.new4net.sso.core.entity;

import com.new4net.sso.api.dto.ModuleInfo;
import com.new4net.sso.core.repo.AuthorityReposity;
import com.new4net.sso.core.repo.RoleReposity;
import com.new4net.sso.core.ApplicationContextUtils;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@EqualsAndHashCode(of = {"moduleId"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Cacheable(true)
@org.hibernate.annotations.Cache(region = "Module", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Module implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String moduleName;

    private String superModuleName;
    @Column(name="createTime" ,insertable = false,nullable=false,columnDefinition="timestamp  DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;

    private boolean enable=true;
    private String remark;
    public ModuleInfo buildModuleInfo(){
        return ModuleInfo.builder().moduleName(moduleName).enable(enable).remark(remark).superModuleName(superModuleName).build();
    }
    public Set<Role> findRoles(){
        RoleReposity roleReposity = ApplicationContextUtils.getBean(RoleReposity.class);

        return roleReposity.findByModule(this);
    }
    public Set<Authority> findAuthorities(){
        AuthorityReposity authorityReposity = ApplicationContextUtils.getBean(AuthorityReposity.class);
        return authorityReposity.findByModule(this);
    }
}
