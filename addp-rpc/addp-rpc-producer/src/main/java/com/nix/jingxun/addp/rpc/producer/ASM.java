package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.util.CommonUtil;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/16 01:53
 */
public class ASM {

    public static  RPCInvoke changeBean(Object bean) throws Exception {
        Class impl = bean.getClass();
        String newClassPath = CommonUtil.className2FilePath(impl.getName() + "Proxy");
        ClassWriter cw = new ClassWriter(0);
        cw.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC, newClassPath, null, "java/lang/Object",new String[]{Type.getInternalName(RPCInvoke.class)});
        //创建全局变量
        cw.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,"impl",Type.getDescriptor(Object.class),null,null);

        createInit(cw,newClassPath);
        createInvokeMethod(impl.getMethods(),cw,newClassPath);
        cw.visitEnd();
        Class<?> clazz = CommonUtil.createClassLoader(cw.toByteArray(),CommonUtil.filepath2ClassName(newClassPath));
        Constructor constructor = clazz.getConstructor(Object.class);
        return (RPCInvoke) constructor.newInstance(bean);
    }

    //生成构造方法
    private static void createInit(ClassWriter cw,String owner) {
        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/Object;)V", null, null);
        mw.visitCode();
//        0: aload_0
//        1: invokespecial #1                  // Method java/lang/Object."<init>":()V
//        4: aload_0
//        5: aload_1
//        6: putfield      #2                  // Field impl:Ljava/lang/Object;
//        9: return
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V",false);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitFieldInsn(Opcodes.PUTFIELD,owner,"impl",Type.getDescriptor(Object.class));
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(Integer.MAX_VALUE, 2);
        mw.visitEnd();
    }

    private static void createInvokeMethod(Method[] methods, ClassWriter cw, String owner) throws Exception {
        Method invokeMethod = RPCInvoke.class.getMethod("invoke",String.class,Object[].class);
        //实现RPCInvoke的invoke方法
        MethodVisitor mw = cw.visitMethod(
                Opcodes.ACC_PUBLIC,
                invokeMethod.getName(),
                // 方法签名
                Type.getMethodDescriptor(invokeMethod),
                // 范型签名（一般不用）
                null,
                // 异常
                Stream.of(invokeMethod.getExceptionTypes()).map(Class::getName).toArray(String[]::new));
        mw.visitCode();
        // 给实现类每个方法代理
        for (Method method:methods) {
            //lcd 方法签名
            mw.visitLdcInsn(getMethodSign(method.getName(),method.getParameterTypes()));
            //aload_1 aload RPCInvoke#invoke的sign参数
            mw.visitVarInsn(Opcodes.ALOAD,1);
            // 载入方法签名后equals方法
            Method equals = String.class.getMethod("equals", Object.class);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(String.class),equals.getName(), Type.getMethodDescriptor(equals),false);
            // ifeq指令
            Label label = new Label();
            mw.visitJumpInsn(Opcodes.IFEQ,label);
            //if true 加载this
            mw.visitVarInsn(Opcodes.ALOAD,0);
            // 获取impl变量 getfield      #2                  // Field impl:Ljava/lang/Object;
            mw.visitFieldInsn(Opcodes.GETFIELD,owner,"impl",Type.getDescriptor(Object.class));
            // 获取变量后转换类型
            if (!method.getDeclaringClass().equals(Object.class)) {
                mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(method.getDeclaringClass()));
            }
            // 加载方法执行参数
            for (int i = 0;i < method.getParameterCount();i ++) {
                // 加载RPCInvoke#invoke的args
                mw.visitVarInsn(Opcodes.ALOAD,2);
                // 跳到相应未知
                if (i > 5) {
                    mw.visitVarInsn(Opcodes.BIPUSH, i);
                } else {
                    mw.visitInsn(Opcodes.ICONST_0 + i);
                }
                // aaload 指令
                mw.visitInsn(Opcodes.AALOAD);
                // 强制转换
                checkCast(mw,method.getParameterTypes()[i]);
            }
            //执行方法
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(method.getDeclaringClass()),method.getName(), Type.getMethodDescriptor(method),false);
            // 判断执行的方法是否需要返回
            if (!method.getReturnType().equals(void.class)) {
                returnCast(mw,method.getReturnType());
                // 执行areturn指令
                mw.visitInsn(Opcodes.ARETURN);
            } else {
                mw.visitInsn(Opcodes.ACONST_NULL);
                mw.visitInsn(Opcodes.ARETURN);
            }
            mw.visitLabel(label);
            mw.visitFrame(Opcodes.F_SAME,0,null,0,null);
        }
        // else return null
        mw.visitInsn(Opcodes.ACONST_NULL);
        mw.visitInsn(Opcodes.ARETURN);
        mw.visitMaxs(Integer.MAX_VALUE, 3);
        mw.visitEnd();
    }

    private static void checkCast(MethodVisitor mw,Class type) throws NoSuchMethodException {
        if (type.equals(int.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Integer.class));
            Method method = Integer.class.getMethod("intValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Integer.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(long.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Long.class));
            Method method = Long.class.getMethod("longValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Long.class),method.getName(),Type.getMethodDescriptor(method),false);
        }else if (type.equals(double.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Double.class));
            Method method = Double.class.getMethod("doubleValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Double.class),method.getName(),Type.getMethodDescriptor(method),false);
        }else if (type.equals(float.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Float.class));
            Method method = Float.class.getMethod("floatValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Float.class),method.getName(),Type.getMethodDescriptor(method),false);
        }else if (type.equals(boolean.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Boolean.class));
            Method method = Boolean.class.getMethod("booleanValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Boolean.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(char.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Character.class));
            Method method = Character.class.getMethod("charValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Character.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(byte.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Byte.class));
            Method method = Byte.class.getMethod("byteValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Byte.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(short.class)) {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(Short.class));
            Method method = Short.class.getMethod("shortValue");
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL,Type.getInternalName(Short.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (!type.equals(Object.class)){
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(type));
        }
    }
    // 返回值基本类型转换成封装类型
    private static void returnCast(MethodVisitor mw,Class type) throws NoSuchMethodException {
        if (type.equals(int.class)) {
            Method method = Integer.class.getMethod("valueOf", int.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Integer.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(long.class)) {
            Method method = Long.class.getMethod("valueOf", long.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Long.class),method.getName(),Type.getMethodDescriptor(method),false);
        }else if (type.equals(double.class)) {
            Method method = Double.class.getMethod("valueOf", double.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Double.class),method.getName(),Type.getMethodDescriptor(method),false);
        }else if (type.equals(float.class)) {
            Method method = Float.class.getMethod("valueOf", float.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Float.class),method.getName(),Type.getMethodDescriptor(method),false);
        }else if (type.equals(boolean.class)) {
            Method method = Boolean.class.getMethod("valueOf", boolean.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Boolean.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(char.class)) {
            Method method = Character.class.getMethod("valueOf", char.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Character.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(byte.class)) {
            Method method = Byte.class.getMethod("valueOf", byte.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Byte.class),method.getName(),Type.getMethodDescriptor(method),false);
        } else if (type.equals(short.class)) {
            Method method = Short.class.getMethod("valueOf", short.class);
            mw.visitMethodInsn(Opcodes.INVOKESTATIC,Type.getInternalName(Short.class),method.getName(),Type.getMethodDescriptor(method),false);
        }
    }

    public static String getMethodSign(String methodName,Class[] paramTypes) {
        StringBuffer sign = new StringBuffer(methodName);
        if (paramTypes != null) {
            Stream.of(paramTypes).forEach(item -> sign.append(item.getName()));
        }
        return sign.toString();
    }
}
