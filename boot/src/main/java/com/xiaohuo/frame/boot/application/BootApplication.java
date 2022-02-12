package com.xiaohuo.frame.boot.application;

import com.xiaohuo.frame.boot.annotation.BootStarter;
import com.xiaohuo.frame.boot.annotation.BootStarterMethod;
import com.xiaohuo.frame.boot.constants.ConstantsPath;
import com.xiaohuo.frame.file.PackageScanner;

import java.lang.reflect.Method;

public class BootApplication {

    public static void run() {
        PackageScanner packageScanner = new PackageScanner() {

            @Override
            public void dealClass(Class<?> clazz)  {
                if (!clazz.isAnnotationPresent(BootStarter.class)) {
                    return;
                }
                try {
                    for (Method method : clazz.getMethods()) {
                        if (method.isAnnotationPresent(BootStarterMethod.class)) {
                            System.out.println(method.getName());
                            String[] args = new String [] {};
                            method.invoke(null, (Object) args );
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        packageScanner.scanPackage(ConstantsPath.ScanPath);
    }


}
