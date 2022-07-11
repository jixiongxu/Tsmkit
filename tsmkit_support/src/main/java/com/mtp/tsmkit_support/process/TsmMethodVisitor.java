package com.mtp.tsmkit_support.process;


import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class TsmMethodVisitor extends AdviceAdapter {

    private boolean inject = false;
    private TsmKitAnnotationVisitor mTsmKitAnnotationVisitor;
    private final String methodName;
    private final boolean isStaticMethod;
    private final boolean isPrivate;
    private String className;
    private TsmKitClassVisitor cv;
    private final Type mineType;

    /**
     * Constructs a new {@link AdviceAdapter}.
     *
     * @param api           the ASM API version implemented by this visitor. Must be one of {@link
     *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
     * @param methodVisitor the method visitor to which this adapter delegates calls.
     * @param access        the method's access flags (see {@link Opcodes}).
     * @param name          the method's name.
     * @param descriptor    the method's descriptor (see {@link Type Type}).
     */
    protected TsmMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
        mineType = Type.getMethodType(descriptor);
        methodName = name;
        isStaticMethod = access == ACC_STATIC + 1;
        isPrivate = access == ACC_PRIVATE;
    }

    public void setClassWriter(ClassWriter classWriter) {
    }

    public void setClassVisitor(TsmKitClassVisitor classWriter) {
        cv = classWriter;
    }

    public void setClassName(String name) {
        className = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String tsm_descriptor = "Lcom/mtp/tsmkit_core/annotation/TsmKit;";
        inject = tsm_descriptor.equals(descriptor);
        if (!"V".equals(mineType.getReturnType().getInternalName())) {
            inject = false;
        }
        if ("<init>".equals(methodName)) {
            inject = false;
        }
        if (inject) {
            AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible);
            mTsmKitAnnotationVisitor = new TsmKitAnnotationVisitor(api, annotationVisitor);
            return mTsmKitAnnotationVisitor;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        if (!inject) {
            return;
        }
        if (isPrivate) {
            try {
                throw new Exception("TsmKit not allow private method:\nat:" + className + ":" + methodName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Type[] argumentTypes = getArgumentTypes();
        String md5Tag = Utils.stringToMD5(Type.getMethodDescriptor(getReturnType(), argumentTypes));
        cv.makeTsmkitRunnableClassV2(methodName, className, mineType.getReturnType(), isStaticMethod, md5Tag, argumentTypes);
        editorOriginMethod(md5Tag);
    }

    private void editorOriginMethod(String tag) {
        String mTsmKitManagerOwner = "com/mtp/tsmkit_core/TsmKitManager";
        Type[] argumentTypes = getArgumentTypes();
        String runnableName = methodName + "TsmRunnableImp" + tag;
        String runnablePath = className.substring(0, className.lastIndexOf("/") + 1) + runnableName;
        String runnableTypeDescriptor = "L" + runnablePath + ";";

        int dispatcherId = mTsmKitAnnotationVisitor.getDispatcherId();

        mv.visitMethodInsn(INVOKESTATIC, mTsmKitManagerOwner, "getInstance", "()Lcom/mtp/tsmkit_core/TsmKitManager;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, mTsmKitManagerOwner, "currentRunOn", "()I", false);

        int currentRunOn = newLocal(Type.INT_TYPE);
        mv.visitVarInsn(ISTORE, currentRunOn);

        mv.visitCode();
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, currentRunOn);
        mv.visitIntInsn(BIPUSH, dispatcherId);
        Label label1 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, label1);
        Label label2 = new Label();
        mv.visitLabel(label2);

        //  xxxRunnable run = new xxxRunnable(var ...)
        mv.visitTypeInsn(NEW, runnablePath);
        mv.visitInsn(DUP);
        for (int i = 0; i < argumentTypes.length; i++) {
            mv.visitVarInsn(ALOAD, i + 1);
        }
        int runnable = newLocal(Type.getType(runnableTypeDescriptor));
        mv.visitMethodInsn(INVOKESPECIAL, runnablePath, "<init>", Type.getMethodDescriptor(mineType.getReturnType(), argumentTypes), false);
        mv.visitVarInsn(ASTORE, runnable);

        //xxxRunnable.setTarget(this)
        if (!isStaticMethod) {
            String desc = "(L" + className + ";)V";
            mv.visitVarInsn(ALOAD, runnable);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, runnablePath, "setTarget", desc, false);
        } else {
            mv.visitVarInsn(ALOAD, runnable);
            mv.visitVarInsn(ALOAD, 0);
        }
        mv.visitMethodInsn(INVOKESTATIC, mTsmKitManagerOwner, "getInstance", "()Lcom/mtp/tsmkit_core/TsmKitManager;", false);
        mv.visitVarInsn(ALOAD, runnable);

        String executeMethodName = "";
        switch (dispatcherId) {
            case 1:
                executeMethodName = "executeIO";
                break;
            case 2:
                executeMethodName = "executeCompute";
                break;
            case 3:
                executeMethodName = "executeAuto";
                break;
            default:
                executeMethodName = "executeAndroid";
                break;
        }
        mv.visitMethodInsn(INVOKEVIRTUAL, mTsmKitManagerOwner, executeMethodName, "(Ljava/lang/Runnable;)V", false);
        Label label3 = new Label();
        mv.visitLabel(label3);
        mv.visitInsn(RETURN);
        mv.visitLabel(label1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitEnd();
    }

}
