package cn.yapeteam.ymixin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public class ASMTransformer {
    private Class<?> target;
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(ElementType.METHOD)
    public @interface Inject {
        String method();

        String desc();
    }
}
