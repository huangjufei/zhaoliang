package cn.stylefeng.guns.core.aop;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;

import cn.stylefeng.guns.core.controller.valid.RequestParamterError;




/**
 * controller 请求参数统一校验拦截对象
 * 规定：
 * 1，需要被拦截的controller对象，其类名必须以Conroller结尾；
 */
@Aspect
@Configuration
public class ParamterValidationAop {
	
	/**
	 * 拦截所以以Controller结尾的所有对象
	 */
	@Around("execution (* cn..*Controller.*(..))")
	public Object validation(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		if(args.length == 0) {
			return pjp.proceed();
		}
		
		Errors errors = findErrors(args);
		if(Objects.nonNull(errors) && errors.hasErrors()) {
			throw new RequestParamterError(errors);
		}
		
		return pjp.proceed(args);
	}

	private Errors findErrors(Object[] args) {
		for(int i = args.length -1 ; i >=0 ;--i) {
			if(args[i] instanceof Errors) {
				return (Errors) args[i];
			}
		}
		return null;
	}
}
