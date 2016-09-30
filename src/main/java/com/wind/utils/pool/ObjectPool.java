package com.snda.gfriend.common.pool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxiaofeng02 on 2016/6/23.
 */
public class ObjectPool {
    private final static Map<Class, Pool> typeObjectMap = new HashMap<Class, Pool>();
    //所有类型的对象默认数量
    private static final int size = 100;

    public static void init(Class type, int capacity, Constructor constructor) {
        if(null == type){
            throw new NullPointerException("Parameter[type] is null.");
        }
        if(0 == capacity){
            throw new IllegalArgumentException("Parameter[capacity] can not be 0.");
        }
        if(null == constructor){
            throw new NullPointerException("Parameter[constructor] is null.");
        }

        if(null == typeObjectMap.get(type)){
            synchronized (ObjectPool.class){
                if(typeObjectMap.get(type) == null){
                    typeObjectMap.put(type, new Pool(capacity, new ArrayList<Object>(capacity), constructor));
                }
            }
        }
    }

    public static void init(Class type, int capacity, Class... parameterTypes) throws NoSuchMethodException {
        Constructor constructor = type.getConstructor(parameterTypes);
        init(type, capacity, constructor);
    }

    public static void init(Class type, int capacity, Object... parameters) throws NoSuchMethodException {
        if(null == typeObjectMap.get(type)){
            Class[] parameterTypes = new Class[parameters.length];
            for(int i = 0; i < parameters.length; i++){
                parameterTypes[i] = parameters[i].getClass();
            }

            init(type, capacity, parameterTypes);
        }
    }

    public static <T> T obtain(Class<T> type, Object... parameters) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        init(type, size, parameters);

        Pool singlePool = typeObjectMap.get(type);
        synchronized (singlePool){
            List<Object> list = singlePool.list;
            int size = list.size();
            Object result = null;
            if (size > 0) {
                result = list.remove(size - 1);
            } else {
                Constructor constructor = singlePool.constructor;
                result = constructor.newInstance(parameters);
            }

            return type.cast(result);
        }
    }

    public static void release(Object object){
        if(object == null){
            return;
        }

        Pool singlePool = typeObjectMap.get(object.getClass());
        if(singlePool == null){
            throw new IllegalStateException("Can not call release before init or obtain.");
        }
        synchronized (singlePool){
            List<Object> list = singlePool.list;
            if(list.size() < singlePool.capacity){
                list.add(object);
            }
        }
    }

    public static void clear(){
        typeObjectMap.clear();
    }

    public static void clear(Class type){
        typeObjectMap.remove(type);
    }

    private static class Pool{
        int capacity = size;
        Constructor constructor;
        List<Object> list;

        Pool(int capacity, List<Object> list, Constructor constructor){
            this.capacity = capacity;
            this.list = list;
            this.constructor = constructor;
        }
    }
}
