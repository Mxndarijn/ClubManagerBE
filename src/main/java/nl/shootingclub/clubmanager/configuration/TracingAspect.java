package nl.shootingclub.clubmanager.configuration;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    public TracingAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("execution(* nl.shootingclub.clubmanager.configuration.datafetcher..*(..))")
    public Object traceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        // Create a new span for each method
        Span newSpan = tracer.nextSpan().name(joinPoint.getSignature().getName()).start();

        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            // Proceed with the actual method execution
            return joinPoint.proceed();
        } finally {
            newSpan.end();  // End the span after the method execution
        }
    }
}
