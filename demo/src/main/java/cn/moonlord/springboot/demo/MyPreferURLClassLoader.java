package cn.moonlord.springboot.demo;

import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class MyPreferURLClassLoader extends ClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(MyPreferURLClassLoader.class);

    private ClassLoader preferLoader;
    private ClassLoader currentLoader;

    public MyPreferURLClassLoader() {
        try {
            preferLoader = new URLClassLoader(new URL[]{
                    new URL("file:///D:/Software/apache-maven-3.6.0/mvnRepository/com/alibaba/fastjson/1.2.62/fastjson-1.2.62.jar")
            }, null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        this.currentLoader = Thread.currentThread().getContextClassLoader();
    }

    private  Class<?> findClass(ClassLoader loader, String name) {
            try {
                Method findClass = preferLoader.getClass().getDeclaredMethod("findClass", name.getClass());
                findClass.setAccessible(true);
                Class<?> type =  (Class<?>) findClass.invoke(preferLoader, name);
            } catch (Exception e1) {
                if (e1 instanceof NoSuchMethodException) {
                    try {
                        Method findClass = preferLoader.getClass().getSuperclass().getDeclaredMethod("findClass", name.getClass());
                        findClass.setAccessible(true);
                        return (Class<?>) findClass.invoke(preferLoader, name);
                    } catch (Exception e2) {
                        if (e1 instanceof NoSuchMethodException) {

                        }
                    }
                } else {
                    logger.error("findClass error, loader: {}, name: {}", loader, name);
                }
            }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Class<? extends ClassLoader> type = preferLoader.getClass();
            Method findClass = preferLoader.getClass().getDeclaredMethod("findClass", name.getClass());
            findClass.setAccessible(true);
            return (Class<?>) findClass.invoke(preferLoader, name);
        } catch (Exception ignored) {
            if(ignored instanceof InvocationTargetException) {
                Throwable target = ((InvocationTargetException) ignored).getTargetException();
                 if(target instanceof ClassNotFoundException) {
                     logger.info("findClass {} ClassNotFoundException: {}", preferLoader, target.getMessage());
                 }
            }
            else if (ignored instanceof NoSuchMethodException) {
                logger.info("findClass {} NoSuchMethodException: {}", preferLoader, ignored.getMessage());
            }
            else {
                ignored.printStackTrace();
            }
        }
        try {
            Method findClass = currentLoader.getClass().getDeclaredMethod("findClass", name.getClass());
            findClass.setAccessible(true);
            return (Class<?>) findClass.invoke(currentLoader, name);
        } catch (Exception ignored) {
            if(ignored instanceof InvocationTargetException) {
                Throwable target = ((InvocationTargetException) ignored).getTargetException();
                if(target instanceof ClassNotFoundException) {
                    logger.info("findClass {} ClassNotFoundException: {}", currentLoader, target.getMessage());
                }
            }
            else if (ignored instanceof NoSuchMethodException) {
                logger.info("findClass {} NoSuchMethodException: {}", currentLoader, ignored.getMessage());
            }
            else {
                ignored.printStackTrace();
            }
        }
        return super.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            try {
                return preferLoader.loadClass(name);
            } catch (Exception ignored) {
                if(ignored instanceof ClassNotFoundException) {
                    logger.info("loadClass {} ClassNotFoundException: {}", preferLoader, ignored.getMessage());
                }
                else {
                    ignored.printStackTrace();
                }
            }
            try {
                return currentLoader.loadClass(name);
            } catch (Exception ignored) {
                if(ignored instanceof ClassNotFoundException) {
                    logger.info("loadClass {} ClassNotFoundException: {}", preferLoader, ignored.getMessage());
                }
                else {
                    ignored.printStackTrace();
                }
            }
            return super.loadClass(name);
        }
    }

}
