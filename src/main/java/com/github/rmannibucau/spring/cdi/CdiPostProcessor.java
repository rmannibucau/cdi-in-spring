package com.github.rmannibucau.spring.cdi;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

public class CdiPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        final BeanManager beanManager = BeanManagerCatcher.getBeanManager();
        final CreationalContext creationalContext = beanManager.createCreationalContext(null);
        final AnnotatedType annotatedType = beanManager.createAnnotatedType(bean.getClass());
        final InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(bean, creationalContext);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }
}
