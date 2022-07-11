package com.mtp.tsmkit_support.plugin;

import com.mtp.tsmkit_support.process.TsmKitClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TsmTransformManager {

    private static final String javac_p = new File("").getAbsolutePath() + "/app/build/intermediates/javac/debug/classes/com/mtp/tsmkit/MainActivity.class";

    public static void main(String[] args) throws Exception {
        Thread.sleep(500);
        File file = new File(javac_p);
        startTsmTransform(file);
    }

    public static void startTsmTransform(File input) {
        try {
            FileInputStream fis = new FileInputStream(input);
            byte[] buffer = new byte[fis.available()];
            int read = fis.read(buffer);
            if (read <= 0) {
                System.out.println("TsmTransformManager: read size  is 0");
                return;
            }
            ClassReader classReader = new ClassReader(buffer);
            ClassWriter classWriter = new ClassWriter(0);
            TsmKitClassVisitor visitor = new TsmKitClassVisitor(Opcodes.ASM9, classWriter);
            visitor.setClassFilePath(input.getAbsolutePath());
            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
            FileOutputStream fos = new FileOutputStream(input);
            fos.write(classWriter.toByteArray());
            fos.flush();
        } catch (Exception e) {
            System.out.println("TsmTransformManager:" + e.getMessage());
        }
    }
}