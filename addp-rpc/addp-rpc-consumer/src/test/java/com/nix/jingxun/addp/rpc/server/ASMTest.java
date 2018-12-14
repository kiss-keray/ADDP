package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.consumer.proxy.ASM;
import com.nix.jingxun.addp.rpc.producer.test.Hello;
import org.junit.Test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import org.objectweb.asm.*;
/**
 * @author keray
 * @date 2018/12/14 16:33
 */
public class ASMTest {
    @Test
    public void consumerInterfaceTest() throws IOException {
        ClassReader cr = new ClassReader("com.nix.jingxun.addp.rpc.server.Account");
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassAdapter classAdapter = new AddSecurityCheckClassAdapter(cw);
        cr.accept(classAdapter, ClassReader.SKIP_DEBUG);
        byte[] data = cw.toByteArray();
        File file = new File("com/nix/jingxun/addp/rpc/server/Account.class");
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(data);
        fout.close();
    }

    @Test
    public void asmCreateConsumerProxy() throws Exception {
        Class<?> interfaceClazz = Hello.class;
        String implName = interfaceClazz.getName() + "Impl";
        ClassWriter cw= new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(Opcodes.V1_7,Opcodes.ACC_PUBLIC,"com/nix/jingxun/addp/rpc/producer/test/HelloImpl",null,"java/lang/Object",null);
        cw.visitEnd();

        // 构建无参构造方法
        // 第一个设置方法是修饰符 (ACC_PUBLIC、ACC_PROTECTED、ACC_PRIVATE)
        // 第二个设置参数是名称
        // 第三个设置方法描述符
        // 第四个设置参数是字段对应于泛型
        // 第五个设置方法抛出的异常
//        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC," ","()V", null, null);
//        // 开始访问方法的代码
//        mv.visitCode();
//        // 0 表示当前对象
//        mv.visitVarInsn(Opcodes.ALOAD, 0);
//        // 等同于super();
//        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", " ", "()V");
//        // 设置返回
//        mv.visitInsn(Opcodes.RETURN);
//        // 访问最大堆栈大小和方法的局部变量的最大数量
//        mv.visitMaxs(0, 0);
//        // 访问方法的最后
//        mv.visitEnd();


        MethodVisitor mv1 = cw.visitMethod(Opcodes.ACC_PUBLIC, "sayHello", "(Ljava/lang/String;)V", null, null);
        mv1.visitCode();
        mv1.visitFieldInsn(Opcodes.PUTFIELD, String.class.getName(), "str", Type.getDescriptor(String.class));
        mv1.visitEnd();
        byte[] data = cw.toByteArray();
        File file = new File("HelloImpl.class");
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(data);
        fout.close();
    }

    @Test
    public void helloImplTest() throws Exception, IllegalAccessException, InstantiationException {
        System.out.println(Type.getInternalName(ASM.class));
    }

    @Test
    public void createTest2() throws Exception{
        //定义一个叫做Example的类
        ClassWriter cw = new ClassWriter(0);
        cw.visit(Opcodes.V1_7,Opcodes.ACC_PUBLIC, "com/nix/jingxun/addp/rpc/producer/test/HelloImpl", null, "java/lang/Object", null);
        //生成默认的构造方法
        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null);
        //生成构造方法的字节码指令
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(1, 1);
        mw.visitEnd();

        Method method = ASM.class.getMethod("test",String.class,Integer.class,Boolean.class,Object.class,Class.class, List.class);
        int paramCount = method.getParameterCount();
        //生成main方法
        mw = cw.visitMethod(Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
//        mw.visitCode();
        int astoreI = paramCount;
        mw.visitVarInsn(Opcodes.ALOAD,0);
        mw.visitLdcInsn(method.getName());
        if (paramCount > 5) {
            mw.visitVarInsn(Opcodes.BIPUSH,paramCount);
        } else {
            mw.visitInsn(Opcodes.ICONST_0 + paramCount);
        }
        mw.visitMultiANewArrayInsn("[Ljava/lang/Object;",1);
        for (int i = 0;i < paramCount;i ++) {
            mw.visitInsn(Opcodes.DUP);
            if (i > 5) {
                mw.visitVarInsn(Opcodes.BIPUSH,i);
            } else {
                mw.visitInsn(Opcodes.ICONST_0 + i);
            }
            mw.visitVarInsn(Opcodes.ALOAD,i + 1);
            mw.visitInsn(Opcodes.AASTORE);
        }
        //生成main方法中的字节码指令
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ASM.class), "invoke", Type.getMethodDescriptor(ASM.class.getMethod("invoke",Object.class,String.class,Object[].class)));
        mw.visitInsn(Opcodes.POP);
        if (method.getReturnType().equals(void.class)) {
            mw.visitInsn(Opcodes.RETURN);
        } else {
            mw.visitInsn(Opcodes.ARETURN);
        }


//        mw.visitVarInsn(Opcodes.ALOAD,0);
//        mw.visitVarInsn(Opcodes.ASTORE,4);
//        mw.visitLdcInsn("test");
//        mw.visitVarInsn(Opcodes.ASTORE,5);
//        mw.visitInsn(Opcodes.ICONST_3);
//        mw.visitMultiANewArrayInsn("[Ljava/lang/Object;",1);
//        for (int i = 0;i < 3;i ++) {
//            mw.visitInsn(Opcodes.DUP);
//            mw.visitInsn(Opcodes.ICONST_0 + i);
//            mw.visitVarInsn(Opcodes.ALOAD,1 + i);
//            mw.visitInsn(Opcodes.AASTORE);
//        }
//        mw.visitVarInsn(Opcodes.ASTORE,6);
//        mw.visitInsn(Opcodes.RETURN);



        mw.visitMaxs(Integer.MAX_VALUE, 1);
        //字节码生成完成
        mw.visitEnd();


        byte[] data = cw.toByteArray();
        File file = new File("HelloImpl.class");
        FileOutputStream fout = new FileOutputStream(file);
        fout.write(data);
        fout.close();


    }

    @Test
    public void test() throws Exception{
    }
}

class AddSecurityCheckMethodAdapter extends MethodAdapter {
    public AddSecurityCheckMethodAdapter(MethodVisitor mv) {
        super(mv);
    }

    public void visitCode() {
        visitMethodInsn(Opcodes.INVOKESTATIC, "SecurityChecker",
                "checkSecurity", "()V");
    }
}
class AddSecurityCheckClassAdapter extends ClassAdapter {

    public AddSecurityCheckClassAdapter(ClassVisitor cv) {
        //Responsechain 的下一个 ClassVisitor，这里我们将传入 ClassWriter，
        // 负责改写后代码的输出
        super(cv);
    }

    // 重写 visitMethod，访问到 "operation" 方法时，
    // 给出自定义 MethodVisitor，实际改写方法内容
    public MethodVisitor visitMethod(final int access, final String name,
                                     final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,exceptions);
        MethodVisitor wrappedMv = mv;
        if (mv != null) {
            // 对于 "operation" 方法
            if (name.equals("operation")) {
                // 使用自定义 MethodVisitor，实际改写方法内容
                wrappedMv = new AddSecurityCheckMethodAdapter(mv);
            }
        }
        return wrappedMv;
    }
}