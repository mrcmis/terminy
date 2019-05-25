package com.fis.is.terminy.validation.annotations;

import com.fis.is.terminy.validation.validators.BlankLoginUserValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Constraint(validatedBy = BlankLoginUserValidator.class)
public @interface BlankLoginUserCheck {
    String message() default "Należy zalogować się na stronie firmy";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
