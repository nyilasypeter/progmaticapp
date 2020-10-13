package com.progmatic.progmappbe.config;


import com.progmatic.progmappbe.helpers.sourceevaluator.MyClassloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilePermission;
import java.security.*;
import java.util.Map;

public class MyPolicy extends Policy {

    private static final Logger Log = LoggerFactory.getLogger(MyPolicy.class);

    @Override
    public boolean implies(ProtectionDomain domain, Permission permission) {
        if(domain.getClassLoader() instanceof MyClassloader){
            boolean isOk = fewPermission().implies(permission);
            if(!isOk){
                Log.warn("Permission will be denied for: {}", permission.toString() );
            }
            return isOk;
        }
        return true;
    }
    private PermissionCollection defaultSystemPermissions() {
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission()); // this will set the application default permissions, in there we enable all
        return permissions;
    }

    private PermissionCollection fewPermission() {
        Permissions permissions = new Permissions();
        FilePermission fp = new FilePermission("<<ALL FILES>>", "read");
        permissions.add(fp);
        return permissions;
    }
}

