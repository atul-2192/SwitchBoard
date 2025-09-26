package com.SwitchBoard.AuthService.Config;



import org.hibernate.annotations.IdGeneratorType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IdGeneratorType(NanoIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface NanoId {
}
