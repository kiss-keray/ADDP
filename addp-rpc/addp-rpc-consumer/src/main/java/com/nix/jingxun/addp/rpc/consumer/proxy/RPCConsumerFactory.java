package com.nix.jingxun.addp.rpc.consumer.proxy;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.rpc.common.RPCType;
import com.nix.jingxun.addp.rpc.producer.test.Hello;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author keray
 * @date 2018/12/08 19:56
 */
public class RPCConsumerFactory {

    public static <T> T consumer(Class<T> interfaceClazz) {
        return consumer(interfaceClazz, RPCType.SYNC_EXEC_METHOD);
    }

    public static <T> T consumer(Class<T> interfaceClazz, int timeout) {
        return consumer(interfaceClazz, timeout, RPCType.SYNC_EXEC_METHOD);
    }

    public static <T> T consumer(Class<T> interfaceClazz, RPCType type) {
        return consumer(interfaceClazz, 30000, type);
    }


    public static <T> T consumer(Class<T> interfaceClazz, int timeout, RPCType type){
        try {

            RPCInterfaceAnnotation interfaceAnnotation = interfaceClazz.getAnnotation(RPCInterfaceAnnotation.class);
            String newClassPath = className2FilePath(interfaceClazz.getName()) + "Impl";
            ClassWriter cw = new ClassWriter(0);
            cw.visit(Opcodes.V1_7,Opcodes.ACC_PUBLIC, newClassPath, null, "java/lang/Object", new String[]{className2FilePath(interfaceClazz.getName())});
            //添加注解
            AnnotationVisitor annotationVisitor = cw.visitAnnotation(Type.getDescriptor(RPCInterfaceAnnotation.class),true);
            annotationVisitor.visit("appName",interfaceAnnotation.appName());
            annotationVisitor.visit("group",interfaceAnnotation.group());
            annotationVisitor.visit("version",interfaceAnnotation.version());
            annotationVisitor.visit("timeout",timeout);
//            if (type != null) {
//                annotationVisitor.visitEnum("type", Type.getInternalName(RPCType.class), type.name());
//            } else {
//                annotationVisitor.visitEnum("type", Type.getInternalName(RPCType.class), RPCType.SYNC_EXEC_METHOD.name());
//            }
            annotationVisitor.visitEnd();
            //生成默认的构造方法
            MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mw.visitCode();
            //生成构造方法的字节码指令
            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mw.visitInsn(Opcodes.RETURN);
            mw.visitMaxs(1, 1);
            mw.visitEnd();
            Method[] methods = interfaceClazz.getMethods();
            if (methods != null) {
                for (Method method:methods) {
                    createInterfaceMethod(method,cw);
                }
            }
            cw.visitEnd();

            // 生成class
            byte[] data = cw.toByteArray();
            File file = new File(RPCConsumerFactory.class.getResource("/").getPath() + newClassPath + ".class");
            FileOutputStream out = new FileOutputStream(file);
            out.write(data);
            out.close();
            return (T) Class.forName(filepath2ClassName(newClassPath)).newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void createInterfaceMethod(Method method,ClassWriter cw) throws Exception {
        int paramCount = method.getParameterCount();
        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, Stream.of(method.getExceptionTypes()).map(Class::getName).toArray(String[]::new));
        mw.visitCode();
        mw.visitVarInsn(Opcodes.ALOAD,0);
        mw.visitLdcInsn(method.getName());
        if (paramCount > 0) {
            // 设置方法参数值Object[]
            if (paramCount > 5) {
                mw.visitVarInsn(Opcodes.BIPUSH, paramCount);
            } else {
                mw.visitInsn(Opcodes.ICONST_0 + paramCount);
            }
            mw.visitMultiANewArrayInsn("[Ljava/lang/Object;", 1);
            for (int i = 0; i < paramCount; i++) {
                mw.visitInsn(Opcodes.DUP);
                if (i > 5) {
                    mw.visitVarInsn(Opcodes.BIPUSH, i);
                } else {
                    mw.visitInsn(Opcodes.ICONST_0 + i);
                }
                mw.visitVarInsn(Opcodes.ALOAD, i + 1);
                mw.visitInsn(Opcodes.AASTORE);
            }
            // 设置方法参数类型Class[]
            if (paramCount > 5) {
                mw.visitVarInsn(Opcodes.BIPUSH, paramCount);
            } else {
                mw.visitInsn(Opcodes.ICONST_0 + paramCount);
            }
            mw.visitMultiANewArrayInsn("[Ljava/lang/String;", 1);
            for (int i = 0; i < paramCount; i++) {
                mw.visitInsn(Opcodes.DUP);
                if (i > 5) {
                    mw.visitVarInsn(Opcodes.BIPUSH, i);
                } else {
                    mw.visitInsn(Opcodes.ICONST_0 + i);
                }
                mw.visitLdcInsn(method.getParameterTypes()[i].getName());
                mw.visitInsn(Opcodes.AASTORE);
            }
        } else {
            mw.visitInsn(Opcodes.ACONST_NULL);
            mw.visitInsn(Opcodes.ACONST_NULL);
        }
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ASM.class), "invoke", Type.getMethodDescriptor(ASM.class.getMethod("invoke",Object.class,String.class,Object[].class,String[].class)));
        if (method.getReturnType().equals(void.class)) {
            mw.visitInsn(Opcodes.POP);
            mw.visitInsn(Opcodes.RETURN);
        } else {
            mw.visitTypeInsn(Opcodes.CHECKCAST,Type.getInternalName(method.getReturnType()));
            mw.visitInsn(Opcodes.ARETURN);
        }
        mw.visitMaxs(Integer.MAX_VALUE, 1 + paramCount);
        //字节码生成完成
        mw.visitEnd();
    }

    private static String className2FilePath(String clazzName) {
        return clazzName.replaceAll("\\.","/");
    }
    private static String filepath2ClassName(String filepath) {
        return filepath.replaceAll("/",".");
    }
}
