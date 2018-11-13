/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.progmappbe.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author peti
 */
@Entity
@NamedEntityGraphs({
    @NamedEntityGraph(name = "userWithRolesAndPriviliges",
            attributeNodes = {
                @NamedAttributeNode(
                        value = "roles", subgraph = "user.roles")
            },
            subgraphs = {
                @NamedSubgraph(
                        name = "user.roles",
                        attributeNodes = @NamedAttributeNode(value = "priviliges"))
            }
            
    )
})
public class User extends BaseEntity implements UserDetails {

    private String name;

    @Column(nullable = false, unique = true)
    private String loginName;

    private String emailAddress;

    private String password;

    private Boolean accountNonLocked = true;

    @ManyToMany(mappedBy = "users")
    private Set<Role> roles = new HashSet<>();
    
    @OneToMany(mappedBy = "student")
    private Set<ActualTest> actualTests = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    public void addRole(Role role){
        this.roles.add(role);
    }

    public Set<ActualTest> getActualTests() {
        return actualTests;
    }

    public void setActualTests(Set<ActualTest> actualTests) {
        this.actualTests = actualTests;
    }
    
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> ret = new HashSet<>();
        for (Role role : roles) {
            ret.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            for (Privilige privilige : role.getPriviliges()) {
                ret.add(new SimpleGrantedAuthority(privilige.getName()));
            }
        }

        return ret;
    }

    @Override
    public String getUsername() {
        return loginName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return getAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}