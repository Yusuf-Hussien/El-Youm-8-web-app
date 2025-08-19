package phi.elyoum8.config.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectHandler {

    //@Around("execution(* phi.elyoum8.util.migration.*.*(..))")
    @Around("@annotation(phi.elyoum8.config.aspects.TrackExecutionTime)")
    public Object loggingAroundDataMigration(ProceedingJoinPoint joinPoint) throws Throwable
    {
        long startTime = System.currentTimeMillis()/1000;

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis()/1000;
        log.info("{} Done Successfully in {} seconds", joinPoint.getSignature().getName(),endTime-startTime);
        return result;
    }

}
