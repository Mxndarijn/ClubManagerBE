//package nl.shootingclub.clubmanager.configuration;
//
//import io.micrometer.tracing.Span;
//import io.micrometer.tracing.Tracer;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@ConditionalOnProperty(value = {"management.tracing.enabled"}, havingValue = "true", matchIfMissing = true)
//public class TracingAspect2 {
//
//    private final Tracer tracer;
//
//    public TracingAspect2(Tracer tracer) {
//        this.tracer = tracer;
//    }
//
//    @Around("execution(* nl.shootingclub.clubmanager.repository..*(..)) || execution(* nl.shootingclub.clubmanager.service..*(..)) || execution(* nl.shootingclub.clubmanager.controller..*(..))")
//
//
//
//    public Object traceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
//        // Create a new span for each method
//        Span newSpan = tracer.nextSpan().name(joinPoint.getSignature().getName()).start();
//
//        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
//            // Proceed with the actual method execution
//            return joinPoint.proceed();
//        } finally {
//            newSpan.end();  // End the span after the method execution
//        }
//    }
//}
