package com.github.rmannibucau.spring.cdi;

import org.springframework.util.ClassUtils;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.Method;

public class BeanManagerCatcher implements Extension {
    private static BeanManager bm = null;

    public void setBeanManager(final @Observes AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
        bm = beanManager;
    }

    // there is no perf issue here since that's only done with Spring context inititialization
    public static BeanManager getBeanManager() {
        try { // if DeltaSpike available rely on its implementation
            final Class<?> clazz = ClassUtils.getDefaultClassLoader().loadClass("org.apache.deltaspike.core.api.provider.BeanManagerProvider");
            final Method instance = clazz.getMethod("getInstance");
            final Method bm = clazz.getMethod("getBeanManager");
            return BeanManager.class.cast(bm.invoke(instance.invoke(null)));
        } catch (final Exception cnfe) {
            // no-op
        } catch (final NoClassDefFoundError ncdfe) {
            // no-op
        }

        try {
            return BeanManager.class.cast(new InitialContext().lookup("java:comp/BeanManager"));
        } catch (final NamingException ne) {
            // no-op
        }

        return bm; // fallback, will mainly work for WARs
    }
}
