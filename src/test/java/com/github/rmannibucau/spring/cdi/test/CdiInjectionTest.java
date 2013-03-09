package com.github.rmannibucau.spring.cdi.test;

import com.github.rmannibucau.spring.cdi.BeanManagerCatcher;
import com.github.rmannibucau.spring.cdi.CdiPostProcessor;
import org.aopalliance.aop.Advice;
import org.apache.commons.logging.Log;
import org.apache.ziplock.JarLocation;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.AfterAdvice;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.BeanResolver;
import org.springframework.util.Assert;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class CdiInjectionTest {
    @Deployment
    public static WebArchive war() {
        return ShrinkWrap.create(WebArchive.class, "cdi-in-spring-test.war")
                    .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                    .addClasses(CdiBean.class, Container.class, SpringBean.class)
                    .addAsResource(new ClassLoaderAsset("test-ctx.xml"), "test-ctx.xml")
                    .addAsLibraries(ShrinkWrap.create(JavaArchive.class, "cdi-in-spring.jar")
                            .addPackage(CdiPostProcessor.class.getPackage())
                            .addAsServiceProvider(Extension.class, BeanManagerCatcher.class))
                    .addAsLibraries(JarLocation.jarLocation(ApplicationContext.class))
                    .addAsLibraries(JarLocation.jarLocation(BeanUtils.class))
                    .addAsLibraries(JarLocation.jarLocation(AfterAdvice.class))
                    .addAsLibraries(JarLocation.jarLocation(BeanResolver.class))
                    .addAsLibraries(JarLocation.jarLocation(Assert.class))
                    .addAsLibraries(JarLocation.jarLocation(Log.class))
                    .addAsLibraries(JarLocation.jarLocation(Advice.class));
    }

    @Test
    public void checkInjection() {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("test-ctx.xml");
        try {
            assertEquals("cdi", ctx.getBean("spring", SpringBean.class).container());
        } finally {
            ctx.close();
        }
    }

    public static interface Container {
        String value();
    }

    public static class CdiBean implements Container {
        @Override
        public String value() {
            return "cdi";
        }
    }

    public static class SpringBean {
        @Inject
        private Container cdi;

        public String container() {
            return cdi.value();
        }
    }
}
