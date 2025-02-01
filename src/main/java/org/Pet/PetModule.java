package org.Pet;

import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.extensions.Plugin;
import com.github.manolo8.darkbot.extensions.PluginInfo;
import com.sun.source.util.JavacTask;

public class PetModule implements Plugin {

    private HeroManager heroManager;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void init(JavacTask task, String... args) {

    }
}
