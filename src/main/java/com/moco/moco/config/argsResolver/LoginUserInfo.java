package com.moco.moco.config.argsResolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 파라미터에 사용할 수 있는 어노테이션
@Retention(RetentionPolicy.RUNTIME) // 리플렉션 등을 활용할 수 있도록 런타임까지 어노테이션 정보를 남김
public @interface LoginUserInfo {

}
