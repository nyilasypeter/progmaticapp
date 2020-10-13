package com.progmatic.progmappbe.helpers.sourceevaluator;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;

import java.net.URL;


public class MyClassloader extends LaunchedURLClassLoader {

    public MyClassloader(){
        super(new URL[]{}, Assertions.class.getClassLoader());
    }


    public MyClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public MyClassloader(boolean exploded, URL[] urls, ClassLoader parent) {
        super(exploded, urls, parent);
    }

    public MyClassloader(boolean exploded, Archive rootArchive, URL[] urls, ClassLoader parent) {
        super(exploded, rootArchive, urls, parent);
    }
}
