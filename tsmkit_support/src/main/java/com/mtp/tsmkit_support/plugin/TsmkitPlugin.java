package com.mtp.tsmkit_support.plugin;

import com.android.build.gradle.AppExtension;
import com.sun.istack.NotNull;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class TsmkitPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project target) {

        System.out.println("TsmkitPlugin:start tsmkit transform");

        target.getExtensions().create("Tsmkit", PluginExtensions.class);

        AppExtension appExtension = target.getExtensions().getByType(AppExtension.class);
        final TsmkitTransform tsmkitTransform = new TsmkitTransform();
        appExtension.registerTransform(tsmkitTransform);

        target.afterEvaluate(new Action<Project>() {
            @Override
            public void execute( Project project) {
                PluginExtensions pluginExtensions = project.getExtensions().getByType(PluginExtensions.class);
                tsmkitTransform.setEnable(pluginExtensions.enable);
            }
        });
    }
}
