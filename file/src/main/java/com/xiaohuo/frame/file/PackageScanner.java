package com.xiaohuo.frame.file;

import com.xiaohuo.frame.constants.ConstantsString;
import com.xiaohuo.frame.constants.FileType;
import com.xiaohuo.frame.constants.Symbol;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;

public abstract class PackageScanner {

    public PackageScanner() {
    }

    // scanPackage方法的重载
    public void scanPackage(Class<?> clazz) {
        scanPackage(clazz.getPackage().getName());
    }

    public void scanPackage(String packageName) {
        // 将包名称转换为路径名称的形式
        String packagePath = packageName.replace(Symbol.DOT, Symbol.SLASH_LEFT);
        try {
            // 由类加载器得到URL的枚举
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();

                // 处理jar包
                if (url.getProtocol().equals(FileType.JAR)) {
                    parse(url);
                } else {
                    File file = new File(url.toURI());

                    if (file.exists()) {
                        // 处理普通包
                        parse(file, packageName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    // 抽象方法，由用户自行处理扫描到的类
    public abstract void dealClass(Class<?> clazz) throws InvocationTargetException, IllegalAccessException;

    // jar包的扫描
    private void parse(URL url) throws IOException {
        Enumeration<JarEntry> jarEntries = ((JarURLConnection) url.openConnection())
                .getJarFile().entries();

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarName = jarEntry.getName();

            if (!jarEntry.isDirectory() && jarName.endsWith(Symbol.DOT + FileType.CLASS)) {
                // 将文件路径名转换为包名称的形式
                dealClassName(jarName.replace(Symbol.SLASH_LEFT, Symbol.DOT).replace(Symbol.DOT + FileType.CLASS, ConstantsString.BLANK));
            }
        }
    }

    // 普通包的扫描
    private void parse(File curFile, String packageName) {
        File[] fileList = curFile.listFiles(new FileFilter() {
            // 筛选文件夹和class文件，其余文件不处理
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(Symbol.DOT + FileType.CLASS);
            }
        });

        // 目录就是一颗树，对树进行递归，找到class文件
        for (File file : fileList) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                parse(file, packageName + Symbol.DOT + fileName);
            } else {
                String className = packageName + Symbol.DOT + fileName.replace(Symbol.DOT + FileType.CLASS, ConstantsString.BLANK);
                dealClassName(className);
            }
        }
    }

    // 将找到的class文件生成类对象
    private void dealClassName(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            // 注解、接口、枚举、原始类型不做处理
            if (!clazz.isAnnotation()
                    && !clazz.isInterface()
                    && !clazz.isEnum()
                    && !clazz.isPrimitive()) {
                dealClass(clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
