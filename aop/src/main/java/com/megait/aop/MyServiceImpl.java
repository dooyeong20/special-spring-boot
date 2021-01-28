package com.megait.aop;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MyServiceImpl implements MyService{

    @Dylogging
    @Override
    public void createSomething() {
        System.out.println("creating something ... ");
        sleep(1000);
        System.out.println("Done !");
        sleep(200);
    }
    @Dylogging
    @Override
    public void doSomething() {
        System.out.println("doing something ... ");
        sleep(1000);
        System.out.println("Done !");
        sleep(200);
    }

    @Override
    public void deleteSomething() {
        System.out.println("deleting something ... ");
        sleep(1000);
        System.out.println("Done !");
        sleep(200);
    }

    private void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
