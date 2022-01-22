package cn.moonlord.springboot.demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyPreferURLClassLoader extends ClassLoader {

    private final ClassLoader preferLoader;

    public MyPreferURLClassLoader(ClassLoader preferLoader) {
        this.preferLoader = preferLoader;
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
                System.out.println("not found: "+ target.getMessage());
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
                    System.out.println("not found: "+ ignored.getMessage());
                }
                else {
                    ignored.printStackTrace();
                }
                return super.loadClass(name);
            }
        }
    }

}
