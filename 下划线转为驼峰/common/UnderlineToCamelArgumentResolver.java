package cn.gwm.sales.inter.order.common;

import cn.gwm.sales.inter.order.annotation.RequestParameters;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * @author xiaoyueya
 */
public class UnderlineToCamelArgumentResolver extends AbstractCustomizeResolver {

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is
     * supported by this resolver.
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter;
     * {@code false} otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParameters.class);
    }

    /**
     * 装载参数
     *
     * @param methodParameter       方法参数
     * @param modelAndViewContainer 返回视图容器
     * @param nativeWebRequest      本次请求对象
     * @param webDataBinderFactory  数据绑定工厂
     * @return the resolved argument value, or {@code null}
     * @throws Exception in case of errors with the preparation of argument values
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Object org=handleParameterNames(methodParameter, nativeWebRequest);
        valid(methodParameter,modelAndViewContainer,nativeWebRequest,webDataBinderFactory,org);
        return org;
    }

    /**
     * 处理参数
     * @param parameter
     * @param webRequest
     * @return
     */
    private Object handleParameterNames(MethodParameter parameter, NativeWebRequest webRequest) {
        Object obj = BeanUtils.instantiateClass(parameter.getParameterType());
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        Iterator<String> paramNames = webRequest.getParameterNames();
        while (paramNames.hasNext()) {
            String paramName = paramNames.next();
            Object o = webRequest.getParameter(paramName);
            String finalName = StringHelpers.underLineToCamel(paramName);
            /**
             * 如果是date类型转化则将字符串转化成date类型
             */
            Class cl = wrapper.getPropertyType(finalName);
            if (cl == Date.class){
                String dateTime = String.valueOf(o);
                //转化成date
                SimpleDateFormat dateFormat;
                if (TimeUtils.inclusionTime(dateTime)){
                    dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                }else{
                    dateFormat = new SimpleDateFormat(Constants.SHORT_DATE_FORMAT);
                }
                try {
                    //添加成时间格式
                    wrapper.setPropertyValue(finalName, dateFormat.parse(String.valueOf(o)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                wrapper.setPropertyValue(finalName, o);
            }
        }
        return obj;
    }

}
