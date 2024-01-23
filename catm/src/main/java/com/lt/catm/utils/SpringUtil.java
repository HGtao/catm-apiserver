package com.lt.catm.utils;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spring工具类 方便在非spring管理环境中获取bean
 */
@Component
public final class SpringUtil implements BeanFactoryPostProcessor, ApplicationContextAware {
    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    /**
     * 缓存类型
     */
    private static final Map<String, Class<?>> CLASS_MAP_CACHE = new ConcurrentHashMap<>();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtil.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws org.springframework.beans.BeansException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws org.springframework.beans.BeansException
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * 根据类型获取一组bean
     *
     * @param <T>   bean类型
     * @param clazz bean类
     * @return
     */
    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return beanFactory.getBeansOfType(clazz);
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    /**
     * 根据类全名从SpringBean容器获取Bean
     *
     * @param <T>
     * @param fullClassName 含包名的类名
     * @return bean
     * @throws NoSuchBeanDefinitionException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByClassName(String fullClassName) throws NoSuchBeanDefinitionException {
        try {
            Class<?> clazz = CLASS_MAP_CACHE.get(fullClassName);
            if (clazz == null) {
                clazz = Class.forName(fullClassName, false, applicationContext.getClassLoader());
                CLASS_MAP_CACHE.put(fullClassName, clazz);
            }
            return (T) beanFactory.getBean(clazz);
        } catch (ClassNotFoundException e) {
            throw new NoSuchBeanDefinitionException("");
        }
    }

    /**
     * 获取指定Class
     * 通常情况下此方法返回的类都是未初始化的
     *
     * @param fullClassName 全限定类名（com.xxx.xxx.Clazz)
     * @param build         如果不存在是否创建
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> getClassByName(String fullClassName, boolean build) throws ClassNotFoundException {
        Class<?> clazz = CLASS_MAP_CACHE.get(fullClassName);
        if (clazz == null && build) {
            clazz = Class.forName(fullClassName, false, applicationContext.getClassLoader());
            CLASS_MAP_CACHE.put(fullClassName, clazz);
        }
        return clazz;
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     *
     * @param invoker
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }
}
