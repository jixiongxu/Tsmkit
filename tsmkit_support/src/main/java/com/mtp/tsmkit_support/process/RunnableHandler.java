package com.mtp.tsmkit_support.process;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;

public class RunnableHandler extends ClassLoader implements Opcodes {

    private static final String SUFFIX = "TsmRunnableImp";

    public static void makeTsmkitRunnableClass(String savePath, String methodName, String host, Type returnType, boolean isStatic, String tag, Type... types) {

//        String msg = String.format("\n\n\nlocal_path:%s,\nmethodName:%s,\nhost:%s,\nreturnType:%s,\ntypes:%s\n\n\n", savePath, methodName, host, returnType.getDescriptor(), Type.getMethodDescriptor(returnType, types));
//        System.out.println(msg);

        ClassWriter classWriter = new ClassWriter(0);

        // 生成class
        String className = host.substring(0, host.lastIndexOf("/") + 1) + methodName + SUFFIX + tag;
        createRunnableClass(className, classWriter);

        //生成构造方法
        createConstructMethod(classWriter, returnType, types, className);
        classWriter.visitEnd();

        if (isStatic) {
            createStaticRun(classWriter, host, className, types, returnType, methodName);
        } else {
            // 生成setTarget方法
            createSetTarget(classWriter, host, className);
            // 生成run方法
            createRun(classWriter, host, className, types, returnType, methodName);
        }

        // 保存class
        String mRunnableClassPath = savePath.substring(0, savePath.lastIndexOf("/") + 1) + methodName + SUFFIX + tag + ".class";
        saveClass(mRunnableClassPath, classWriter);
    }

    /**
     * @param cv         cv
     * @param returnType returnType
     * @param inputTypes inputTypes
     * @param owner      com/example/test/TsmTestRunnable_TsmkitImp
     */
    private static void createConstructMethod(ClassWriter cv, Type returnType, Type[] inputTypes, String owner) {
        String descriptor = Type.getMethodDescriptor(returnType, inputTypes);

        MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, "<init>", descriptor, null, null);

        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        String var_tsm = "var_tsm";
        for (int index = 0; index < inputTypes.length; index++) {
            FieldVisitor fieldVisitor = cv.visitField(ACC_PRIVATE, var_tsm + index, inputTypes[index].getDescriptor(), null, null);
            fieldVisitor.visitEnd();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, index + 1);
            methodVisitor.visitFieldInsn(PUTFIELD, owner, var_tsm + index, inputTypes[index].getDescriptor());
        }
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 2);
        methodVisitor.visitEnd();
    }

    /**
     * @param cv         cv
     * @param host       com/example/test/TsmTestRunnable_TsmkitImp
     * @param owner      com/example/test/Test
     * @param types      types
     * @param returnType returnType
     * @param methodName methodName
     */
    private static void createRun(ClassWriter cv, String host, String owner, Type[] types, Type returnType, String methodName) {
        MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        String descriptor = Type.getMethodDescriptor(returnType, types);
        String targetDescriptor = "L" + host + ";";
        String var_tsm = "var_tsm";
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, owner, "target", targetDescriptor);
        for (int index = 0; index < types.length; index++) {
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, owner, var_tsm + index, types[index].getDescriptor());
        }
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, host, methodName, descriptor, false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitEnd();
    }


    /**
     * @param cv         cv
     * @param host       com/example/test/TsmTestRunnable_TsmkitImp
     * @param owner      com/example/test/Test
     * @param types      types
     * @param returnType returnType
     * @param methodName methodName
     */
    private static void createStaticRun(ClassWriter cv, String host, String owner, Type[] types, Type returnType, String methodName) {
        MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        String descriptor = Type.getMethodDescriptor(returnType, types);
        String var_tsm = "var_tsm";
        methodVisitor.visitVarInsn(ALOAD, 0);
        for (int index = 0; index < types.length; index++) {
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, owner, var_tsm + index, types[index].getDescriptor());
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, host, methodName, descriptor, false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitEnd();
    }

    /**
     * @param cv    cv
     * @param host  com/mtp/tsmkit_support/test/Test
     * @param owner com/mtp/tsmkit_support/test/TsmTestRunnable_TsmkitImp
     */
    private static void createSetTarget(ClassWriter cv, String host, String owner) {
        String descriptor = "L" + host + ";";
        String methodDescriptor = "(L" + host + ";)V";

        MethodVisitor methodVisitor = cv.visitMethod(ACC_PUBLIC, "setTarget", methodDescriptor, null, null);
        methodVisitor.visitCode();

        FieldVisitor fieldVisitor = cv.visitField(ACC_PRIVATE, "target", descriptor, null, null);
        fieldVisitor.visitEnd();

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitFieldInsn(PUTFIELD, owner, "target", descriptor);

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
    }

    /**
     * @param name com/example/test/TsmTestRunnable_TsmkitImp
     * @param cw   cw
     */
    private static void createRunnableClass(String name, ClassWriter cw) {
        cw.visit(V11, ACC_PUBLIC | ACC_SUPER, name, null, "java/lang/Object", new String[]{"java/lang/Runnable"});
    }

    /**
     * @param path com/mtp/tsmkit_support/test/TsmRunnableImp.class
     * @param cw   cw
     */
    private static void saveClass(String path, ClassWriter cw) {
        try {
            byte[] code = cw.toByteArray();
            FileOutputStream fos = null;
            fos = new FileOutputStream(path);
            fos.write(code);
            fos.close();
//            testByTheWay(cw, path.substring(path.lastIndexOf("/") + 1, path.length() - 6));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testByTheWay(ClassWriter cw, String className) throws Exception {
        byte[] code = cw.toByteArray();
        System.out.println("RunnableHandler---" + className);
        RunnableHandler loader = new RunnableHandler();
        Class<?> exampleClass = loader.defineClass(className, code, 0, code.length);
        Runnable obj = (Runnable) exampleClass.getConstructor().newInstance();
        obj.run();
    }
}
