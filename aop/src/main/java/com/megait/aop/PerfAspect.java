package com.megait.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class PerfAspect {

    // @Around("execution(* com.megait..*.*Service.*(..))")
    // @Around("@annotation(Dylogging)")
    @Around("bean(myServiceImpl)")
    public Object logPerf(ProceedingJoinPoint pjp) throws Throwable{
        long begin = System.currentTimeMillis();
        System.out.println("CLASS : " + pjp.getSignature().getName());

        Object retVal = pjp.proceed();

        System.out.println("time : " + (System.currentTimeMillis() - begin) / 1000 + "." + (System.currentTimeMillis() - begin) % 1000 + "  sec");
        System.out.println();
        return retVal;
    }


}