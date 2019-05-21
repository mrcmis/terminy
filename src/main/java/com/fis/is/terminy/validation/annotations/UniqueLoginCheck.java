package com.fis.is.terminy.validation.annotations;

import com.fis.is.terminy.validation.validators.UniqueLoginValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Constraint(validatedBy = UniqueLoginValidator.class)
public @interface UniqueLoginCheck {
    String message() default "Unique Validation Failure";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
