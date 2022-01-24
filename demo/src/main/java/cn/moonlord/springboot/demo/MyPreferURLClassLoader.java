package cn.moonlord.springboot.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class MyPreferURLClassLoader extends ClassLoader {

    private ClassLoader preferLoader;
    private ClassLoader currentLoader;

    public MyPreferURLClassLoader(ClassLoader currentLoader) {
        try {
            preferLoader = new URLClassLoader(new URL[]{
                    new URL("file:///D:/Software/apache-maven-3.6.0/mvnRepository/com/alibaba/fastjson/1.2.62/fastjson-1.2.62.jar")
            }, null);
        } catch(Exception e) {
            e.printStackTrace();
        }
        this.currentLoader = currentLoader;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Method findClass = preferLoader.getClass().getDeclaredMethod("findClass", name.getClass());
            findClass.setAccessible(true);
            return (Class<?>) findClass.invoke(preferLoader, name);
        } catch (Exception ignored) {
            Throwable target = ((InvocationTargetException) ignored).getTargetException();
            if(ignored instanceof InvocationTargetException && target instanceof ClassNotFoundException) {
                System.out.println("findClass preferLoader not found: "+ target.getMessage());
            }
            else {
                ignored.printStackTrace();
            }
        }
        try {
            Method findClass = currentLoader.getClass().getDeclaredMethod("findClass", name.getClass());
            findClass.setAccessible(true);
            return (Class<?>) findClass.invoke(preferLoader, name);
        } catch (Exception ignored) {
            Throwable target = ((InvocationTargetException) ignored).getTargetException();
            if(ignored instanceof InvocationTargetException && target instanceof ClassNotFoundException) {
                System.out.println("findClass currentLoader not found: "+ target.getMessage());
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
                    System.out.println("loadClass preferLoader not found: "+ ignored.getMessage());
                }
                else {
                    ignored.printStackTrace();
                }
            }
            try {
                return currentLoader.loadClass(name);
            } catch (Exception ignored) {
                if(ignored instanceof ClassNotFoundException) {
                    System.out.println("loadClass currentLoader not found: "+ ignored.getMessage());
                }
                else {
                    ignored.printStackTrace();
                }
            }
            return super.loadClass(name);
        }
    }

}
