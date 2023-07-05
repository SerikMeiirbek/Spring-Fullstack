package com.serikscode;

import org.springframework.stereotype.Service;

@Service
public class FooService {

    public Main.Foo foo;

    public FooService(Main.Foo foo) {
        this.foo = foo;
    }

    String getFooName(){
        return foo.name();
    }
}
