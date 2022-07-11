package com.mtp.tsmkit_support.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class TsmkitTransform extends Transform {

    private boolean enable = false;

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String getName() {
        return "TSMTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput next : inputs) {
            Collection<DirectoryInput> directoryInputs = next.getDirectoryInputs();
            Collection<JarInput> jarInputs = next.getJarInputs();
            Iterator<JarInput> jarIterator = jarInputs.iterator();
            Iterator<DirectoryInput> iteratorDir = directoryInputs.iterator();
            while (jarIterator.hasNext()) {
                JarInput jar = jarIterator.next();
                processJar(jar.getFile());
                File dest = transformInvocation.
                        getOutputProvider().
                        getContentLocation(
                                jar.getName(),
                                jar.getContentTypes(),
                                jar.getScopes(),
                                Format.JAR);
                try {
                    FileUtils.copyFile(jar.getFile(), dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (iteratorDir.hasNext()) {
                DirectoryInput dir = iteratorDir.next();
                File dest = transformInvocation.
                        getOutputProvider().
                        getContentLocation(
                                dir.getName(),
                                dir.getContentTypes(),
                                dir.getScopes(),
                                Format.DIRECTORY);
                processDir(dir.getFile());
                try {
                    FileUtils.copyDirectory(dir.getFile(), dest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processJar(File jar) {
        if (!enable) {
            System.out.println("TSMTransform:transform--->" + false);
        }
    }

    private void processDir(File source) {
        if (!enable) {
            System.out.println("TSMTransform:transform--->" + false);
            return;
        }
        if (source.getAbsolutePath().endsWith("BuildConfig.class")) {
            return;
        }
        if (source.getAbsolutePath().endsWith("TsmRunnableImp.class")) {
            return;
        }
        if (source.isFile() && source.getAbsolutePath().endsWith(".class")) {
            TsmTransformManager.startTsmTransform(source);
        } else if (source.isDirectory()) {
            File[] files = source.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getAbsolutePath().endsWith(".class");
                }
            });
            if (files == null) {
                return;
            }
            for (File f : files) {
                processDir(f);
            }
        }
    }
}
