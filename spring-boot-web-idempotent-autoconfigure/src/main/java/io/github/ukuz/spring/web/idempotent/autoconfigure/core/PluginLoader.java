package io.github.ukuz.spring.web.idempotent.autoconfigure.core;

import io.github.ukuz.spring.web.idempotent.autoconfigure.utils.Holder;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ukuz90
 * @date 2019-05-16
 */
public class PluginLoader<T> {

    private final static ConcurrentHashMap<Class<?>, PluginLoader> LOADERS = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<T>>> cachedClassHolder = new Holder<>();

    private final Class<T> type;
    private final static String PLUGIN_SERVICES = "META-INF/services";
    private final static String PLUGIN_UKUZ = "META-INF/ukuz";

    private PluginLoader(Class<T> type) {
        this.type = type;
    }

    public static PluginLoader getLoader(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Plugin class must not be null");
        }
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("Plugin class " + clazz + " is not an interface!");
        }
        if (!clazz.isAnnotationPresent(Spi.class)) {
            throw new IllegalArgumentException("Plugin class " + clazz + " must annotated with @Spi");
        }
        PluginLoader loader = LOADERS.get(clazz);
        if (loader == null) {
            LOADERS.putIfAbsent(clazz, new PluginLoader(clazz));
            loader = LOADERS.get(clazz);
        }
        return loader;

    }

    public T getAdpativePlugin() {
        return null;
    }

    public T getPlugin(String key) {
        return null;
    }

    private Map<String, Class<T>> getPluginClass() {
        Map<String, Class<T>> classMap = this.cachedClassHolder.getVal();
        if (classMap == null) {
            synchronized (this.cachedClassHolder) {
                classMap = this.cachedClassHolder.getVal();
                if (classMap == null) {
                    classMap = loadPluginClass();
                    this.cachedClassHolder.setVal(classMap);
                }
            }
        }
        return classMap;
    }

    private Map<String, Class<T>> loadPluginClass() {
        Map<String, Class<T>> pluginClasses = new HashMap<>();
        loadDirectory(pluginClasses, PLUGIN_UKUZ);
        loadDirectory(pluginClasses, PLUGIN_SERVICES);
        return pluginClasses;
    }

    private void loadDirectory(Map<String, Class<T>> pluginClasses, String dir) {
        String fileName = dir + "/" + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls == null) {
                return;
            }
            Collections.list(urls).forEach(url -> {
                if (url != null) {
                    loadResource(pluginClasses, url);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadResource(Map<String, Class<T>> pluginClasses, URL url) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line ;
            while((line = br.readLine()) != null) {
                int index = line.indexOf("#");
                if (index != -1) {
                    line = line.substring(0, index);
                }
                line = line.trim();
                String key = null;
                index = line.indexOf("=");
                if (index != -1) {
                    key = line.substring(0, index).trim();
                    line = line.substring(index+1).trim();
                }
                if (line.length() > 0) {
                    loadClass(pluginClasses, key, line);
                }
            }
        } catch (Exception e) {
        }
    }

    private void loadClass(Map<String, Class<T>> pluginClasses, String key, String className) {
        try {
            Class clazz = Class.forName(className);
            if (!StringUtils.hasText(key)) {
                key = getPrefixClassName(clazz);
            }
            pluginClasses.putIfAbsent(key, clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getPrefixClassName(Class clazz) {
        String prefix;
        int index = clazz.getSimpleName().indexOf(type.getSimpleName());
        if (index != -1) {
            prefix = clazz.getSimpleName().substring(0, index).toLowerCase();
        } else {
            prefix = clazz.getSimpleName().toLowerCase();
        }
        return prefix;
    }

    private ClassLoader findClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable e) {
        }
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }

}
