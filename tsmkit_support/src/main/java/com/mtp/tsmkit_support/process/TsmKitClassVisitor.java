package com.mtp.tsmkit_support.process;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class TsmKitClassVisitor extends ClassVisitor implements Opcodes {

    private String className;

    private String mClassPath;

    private final ClassWriter cw;

    public TsmKitClassVisitor(int i, ClassWriter classVisitor) {
        super(i, classVisitor);
        cw = classVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor methodVisitor = super.visitMethod(i, s, s1, s2, strings);
        TsmMethodVisitor tsmMethodVisitor = new TsmMethodVisitor(api, methodVisitor, i, s, s1);
        tsmMethodVisitor.setClassWriter(cw);
        tsmMethodVisitor.setClassVisitor(this);
        tsmMethodVisitor.setClassName(className);
        return tsmMethodVisitor;
    }

    public void setClassFilePath(String classFilePath) {
        this.mClassPath = classFilePath;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public void makeTsmkitRunnableClassV2(String methodName, String host, Type returnType, boolean isStatic, String tag, Type... types) {
        RunnableHandler.makeTsmkitRunnableClass(mClassPath, methodName, host, returnType, isStatic, tag, types);
    }
}
