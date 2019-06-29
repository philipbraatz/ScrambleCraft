package com.doorfail.scramblecraft.util;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ReflectionUtils
{
    private static final HashMap<String, String> methodRemapTable = new HashMap();
    private static final HashMap<String, String> fieldRemapTable = new HashMap();

    private static final HashMap<String, Class> methodReturnTable = new HashMap();
    private static final HashMap<String, Class[]> methodParameterTable = new HashMap();

    static  {
        System.out.println("Loading remap tables...");
        BufferedReader br = new BufferedReader(new InputStreamReader(ReflectionUtils.class.getResourceAsStream("/assets/scramblecraft/srg/mcp-srg.srg")));
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("//") && !line.startsWith("#") &&
                            line.contains(":")) {
                        int pos = line.indexOf(":");
                        String cat = line.substring(0, pos);
                        line = line.substring(pos + 1).trim();
                        String[] parts = line.split(Pattern.quote(" "));
                        if (cat.equals("FD")) {
                            String name1 = parts[0].replace("/", ".");
                            String name2 = parts[1].replace("/", ".");
                            fieldRemapTable.put(name1, name2);
                            fieldRemapTable.put(name2, name1); continue;
                        }  if (cat.equals("MD")) {
                            String returnString = parts[1].substring(parts[1].indexOf(")") + 1).trim();
                            Class returnClass = null;
                            try {
                                returnClass = getClassForString(returnString);
                            } catch (Throwable throwable) {}

                            if (returnClass != null) {
                                Class[] parameterClasses = getClassesForString(parts[3]);
                                String long1 = parts[0].replace("/", ".");
                                String long2 = parts[2].replace("/", ".");

                                methodReturnTable.put(long1, returnClass);
                                methodReturnTable.put(long2, returnClass);

                                methodParameterTable.put(long1, parameterClasses);
                                methodParameterTable.put(long2, parameterClasses);

                                methodRemapTable.put(long1, long2);
                                methodRemapTable.put(long2, long1);
                            }

                        }
                    }
                } catch (Throwable throwable) {}
            }

            br.close();
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }


    public static void init() { System.out.println("Finished loading remap tables."); }


    private static Class[] getClassesForString(String parametersString) {
        List<Class> classes = new ArrayList<Class>();
        String parameters = parametersString.substring(parametersString.indexOf("(") + 1, parametersString.indexOf(")"));
        if (!parameters.isEmpty()) {
            while (parameters.length() > 0) {
                String partial;
                if (parameters.startsWith("L")) {
                    partial = parameters.substring(0, parameters.indexOf(";") + 1);
                } else {
                    partial = parameters.substring(0, 1);
                }
                parameters = parameters.substring(Math.max(1, partial.length()));

                Class clazz = getClassForString(partial);
                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }
        return (Class[])classes.toArray(new Class[classes.size()]);
    }

    private static Class getClassForString(String returnString) {
        Class returnClass = null;
        if (returnString.length() > 1) {
            if (returnString.contains(";")) {
                returnString = returnString.substring(1, returnString.indexOf(";")).replace("/", ".").trim();
                if (returnString.startsWith("L")) {
                    returnString = returnString.substring(1);
                }
                try {
                    returnClass = Class.forName(returnString);
                } catch (ClassNotFoundException classNotFoundException) {}
            }

        }
        else if (returnString.equals("I")) {
            returnClass = int.class;
        } else if (returnString.equals("V")) {
            returnClass = void.class;
        } else if (returnString.equals("F")) {
            returnClass = float.class;
        } else if (returnString.equals("D")) {
            returnClass = double.class;
        } else if (returnString.equals("Z")) {
            returnClass = boolean.class;
        } else if (returnString.equals("S")) {
            returnClass = short.class;
        } else if (returnString.equals("B")) {
            returnClass = byte.class;
        } else if (returnString.equals("C")) {
            returnClass = char.class;
        } else if (returnString.equals("J")) {
            returnClass = long.class;
        }
        return returnClass;
    }

    public static Method getPrivateMethod(Class parent, Object instance, String name, Class... args) {
        String alternateName = (String)methodRemapTable.get(parent.getCanonicalName() + "." + name);
        if (alternateName == null) {
            alternateName = name;
        } else {
            alternateName = alternateName.substring(alternateName.lastIndexOf(".") + 1);
        }
        Method ret = ReflectionHelper.findMethod(parent, name, alternateName, args);
        if (ret != null) {
            ret.setAccessible(true);
            if ((ret.getModifiers() & 0x10) == 16) {
                try {
                    Field modifiersField = Method.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(ret, ret.getModifiers() & 0xFFFFFFEF);
                } catch (Throwable throwable) {}
            }
        }

        return ret;
    }

    public static Field getPrivateField(Class parent, String name) {
        String alternateName = fieldRemapTable.get(parent.getCanonicalName() + "." + name);
        if (alternateName == null) {
            alternateName = name;
        } else {
            alternateName = alternateName.substring(alternateName.lastIndexOf(".") + 1);
        }

        Field ret = null;
        try {
            ret = parent.getDeclaredField(alternateName);
        } catch (Throwable ex) {
            try {
                ret = parent.getDeclaredField(name);
            } catch (Throwable ex2) {
                ex.printStackTrace();
                ex2.printStackTrace();
            }
        }

        if (ret != null) {
            ret.setAccessible(true);
            if ((ret.getModifiers() & 0x10) == 16) {
                try {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(ret, ret.getModifiers() & 0xFFFFFFEF);
                } catch (Throwable throwable) {}
            }
        }

        return ret;
    }

    public static void setPrivateFieldValue(Class parent, Object instance, String name, Object value) {
        Field ret = getPrivateField(parent, name);
        if (ret != null) {
            try {
                ret.set(instance, value);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Object getPrivateFieldValue(Class parent, Object instance, String name) {
        Field ret = getPrivateField(parent, name);
        if (ret != null)
            try {
                return ret.get(instance);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        return null;
    }
}