package com.megait.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyProxyService implements MyService {

    @Autowired
    MyServiceImpl myService;

    @Override
    public void createSomething() {
        long begin = System.currentTimeMillis();

        myService.createSomething();

        System.out.println(System.currentTimeMillis() - begin);
    }

    @Override
    public void doSomething() {
        long begin = System.currentTimeMillis();

        myService.createSomething();

        System.out.println(System.currentTimeMillis() - begin);
    }

    @Override
    public void deleteSomething() {
        myService.createSomething();
    }
}
