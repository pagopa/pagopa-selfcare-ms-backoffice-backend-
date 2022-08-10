package it.pagopa.selfcare.pagopa.web.validator;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Validator;

@Aspect
@Component
public class NameControllerResponseValidator  {//TODO change Name

    @Autowired
    public NameControllerResponseValidator(Validator validator) {
    }

    @Pointcut("execution(* it.pagopa.selfcare.pagopa.web.controller.*.*(..))")
    public void controllersPointcut() {
        // Do nothing because is a pointcut
    }

}
