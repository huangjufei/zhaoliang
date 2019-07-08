/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.stylefeng.guns.core.aop;

import cn.stylefeng.guns.core.common.exception.BizExceptionEnum;
import cn.stylefeng.guns.core.common.exception.InvalidKaptchaException;
import cn.stylefeng.guns.core.controller.valid.RequestParamterError;
import cn.stylefeng.guns.core.log.LogManager;
import cn.stylefeng.guns.core.log.factory.LogTaskFactory;
import cn.stylefeng.guns.core.shiro.ShiroKit;
import cn.stylefeng.roses.core.reqres.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.DisabledAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.lang.reflect.UndeclaredThrowableException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static cn.stylefeng.roses.core.util.HttpContext.getIp;
import static cn.stylefeng.roses.core.util.HttpContext.getRequest;

/**
 * 全局的的异常拦截器（拦截所有的控制器）（带有@RequestMapping注解的方法上都会拦截）
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午3:19:56
 */
@ControllerAdvice
@Order(-1)
public class GlobalExceptionHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 拦截业务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponseData bussiness(ServiceException e) {
        LogManager.me().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getId(), e));
        getRequest().setAttribute("tip", e.getMessage());
        log.error("业务异常:", e);
        return new ErrorResponseData(e.getCode(), e.getMessage());
    }

    /**
     * 用户未登录异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unAuth(AuthenticationException e) {
        log.error("用户未登陆：", e);
        return "/login.html";
    }

    /**
     * 账号被冻结异常
     */
    @ExceptionHandler(DisabledAccountException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountLocked(DisabledAccountException e, Model model) {
        String username = getRequest().getParameter("username");
        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "账号被冻结", getIp()));
        model.addAttribute("tips", "账号被冻结");
        return "/login.html";
    }

    /**
     * 账号密码错误异常
     */
    @ExceptionHandler(CredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String credentials(CredentialsException e, Model model) {
        String username = getRequest().getParameter("username");
        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "账号密码错误", getIp()));
        model.addAttribute("tips", "账号密码错误");
        return "/login.html";
    }

    /**
     * 验证码错误异常
     */
    @ExceptionHandler(InvalidKaptchaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String credentials(InvalidKaptchaException e, Model model) {
        String username = getRequest().getParameter("username");
        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "验证码错误", getIp()));
        model.addAttribute("tips", "验证码错误");
        return "/login.html";
    }

    /**
     * 无权访问该资源异常
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponseData credentials(UndeclaredThrowableException e) {
        getRequest().setAttribute("tip", "权限异常");
        log.error("权限异常!", e);
        return new ErrorResponseData(BizExceptionEnum.NO_PERMITION.getCode(), BizExceptionEnum.NO_PERMITION.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponseData notFount(RuntimeException e) {
    	if(ShiroKit.getUser() != null) {
	        LogManager.me().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getId(), e));
	        getRequest().setAttribute("tip", "服务器未知运行时异常");
    	}
        log.error("运行时异常:", e);
        return new ErrorResponseData(BizExceptionEnum.SERVER_ERROR.getCode(), BizExceptionEnum.SERVER_ERROR.getMessage());
    }
    
    /**
	 * 未知异常处理
	 */
	@ExceptionHandler(Throwable.class)
	public ErrorResponseData unknow(Throwable e) {
		log.error(e.getMessage(),e);
		return new ErrorResponseData(e.getMessage());
	}
	/**
	 * 请求参数错误处理
	 */
	@ExceptionHandler(RequestParamterError.class)
	public ErrorResponseData requestParamterError(HttpServletRequest request ,HttpServletResponse response ,RequestParamterError e) {
		String reqResource = request.getScheme() +"://" + request.getServerName() 
			+":" + request.getServerPort() +request.getContextPath() + request.getRequestURI();
		log.debug(reqResource + " 请求，参数错误；参数列表：" + request.getQueryString());
		StringBuffer msg = new StringBuffer("参数错误");
		Errors errors = e.getError();
		if(errors.hasErrors()) {
			msg.append(": ");
			List<FieldError> fieldErrors = errors.getFieldErrors();
			for(int i = 0 ,len = fieldErrors.size() ;i < len ;++i) {
				FieldError fe = fieldErrors.get(i);
				String message = fe.getDefaultMessage();
				String fieldName = fe.getField();
				msg.append("参数-").append(fieldName).append("=>").append(Objects.isNull(message)?"缺少或错误":message).append(";");
			}
		}
		return new ErrorResponseData(msg.toString());
	}
	/**
	 * 缺少请求参数处理
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ErrorResponseData missingServletRequestParameterException(MissingServletRequestParameterException ex) {
		log.debug("请求参数错误，缺少参数 :" + ex.getParameterName());
		return new ErrorResponseData("缺少参数" + ex.getParameterName());
	}
	/**
	 * 违反约束处理
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ErrorResponseData constraintViolationException(ConstraintViolationException e) {
		Set<ConstraintViolation<?>> violations =  e.getConstraintViolations();
		Iterator<ConstraintViolation<?>> iter = violations.iterator();
		StringBuffer sb = new StringBuffer();
		while(iter.hasNext()) {
			ConstraintViolation<?> violation = iter.next();
			sb.append(violation.getMessage()).append(",");
		}
		return new ErrorResponseData(sb.toString());
	}
	/**
	 * 请求参数类型转换错误异常处理
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ErrorResponseData numberFormatException(MethodArgumentTypeMismatchException ex) {
		return new ErrorResponseData("参数格式不匹配：" + ex.getName());
	}
}
