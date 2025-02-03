package org.Pet;

import com.github.manolo8.darkbot.core.manager.PetManager;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.extensions.Configurable;
import eu.darkbot.api.managers.HeroAPI;

import java.lang.reflect.Method;


public class PetModule implements Behavior, Configurable<PetModule.Config> {

    private final HeroAPI hero;
    private final PetManager petManager;
    private Config config;

    public static class Config {
        public  boolean useDefaultModule = true;

        public boolean useComboModule = false;
        public @Number(min = 1, max = 100) int minHpCombo = 50;

        public boolean useHpLink = false;
        public @Number(min = 1, max = 100) int maxHpLink = 40;

        public boolean useRepairModule = false;
        public @Number(min = 1, max = 100) int minRepairModule = 30;

        public boolean useDefenseModule = false;
        public @Number(min = 1, max = 100) int minDefenseModule = 25;

        public boolean useLlamadaExpiratoria = false;
        public @Number(min = 1, max = 100) int minLlamadaExpiratoria = 20;

    }

    public PetModule(HeroAPI hero, PetManager petManager) {
        this.hero = hero;
        this.petManager = petManager;
    }

    @Override
    public void setConfig(ConfigSetting<Config> configSetting) {
        this.config = configSetting.getValue();
    }


    @Override
    public void onTickBehavior() {
        managePetModule();
    }

    private void managePetModule() {
        if(!petManager.isActive() ) return;

        if(config.useComboModule && hero.getHealth().hpPercent() <= config.minHpCombo ) {

        }


    }

    public static void main(String[] args) {
        

        
        for (Object gear : petManager1.getGears()) {
            System.out.println(gear);
        }


    }
}
