package com.mtp.tsmkit_support.process;

import org.objectweb.asm.AnnotationVisitor;

public class TsmKitAnnotationVisitor extends AnnotationVisitor {

    private int dispatcherId = 0;

    public int getDispatcherId() {
        return dispatcherId;
    }

    public TsmKitAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
        super(api, annotationVisitor);
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        super.visitEnum(name, descriptor, value);
        if ("Lcom/mtp/tsmkit_core/annotation/RunType;".equals(descriptor) && "dispatcher".equals(name)) {
            switch (value) {
                case "IO"://1
                    dispatcherId = 1;
                    break;
                case "CPU"://2
                    dispatcherId = 2;
                    break;
                case "Auto"://3
                    dispatcherId = 3;
                    break;
                case "AndroidMain": //0
                    dispatcherId = 0;
                    break;
                default:
                    break;//0
            }
        }
    }
}
