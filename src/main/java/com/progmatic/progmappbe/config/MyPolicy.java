package com.progmatic.progmappbe.config;


import com.progmatic.progmappbe.helpers.sourceevaluator.MyClassloader;

import java.security.*;
import java.util.Map;

public class MyPolicy extends Policy {


    @Override
    public boolean implies(ProtectionDomain domain, Permission permission) {
        if(domain.getClassLoader() instanceof MyClassloader){
            System.out.println("permission denied!!!!!!!!!!!!!!!!");
            return false;
        }
        return true;
    }
    private PermissionCollection defaultSystemPermissions() {
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission()); // this will set the application default permissions, in there we enable all
        return permissions;
    }

    private PermissionCollection noPermission() {
        Permissions permissions = new Permissions();
        return permissions;
    }
}

