package org.Pet;

import com.github.manolo8.darkbot.core.manager.PetManager;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.extensions.Configurable;
import eu.darkbot.api.extensions.Feature;
import eu.darkbot.api.game.enums.PetGear;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.utils.ItemNotEquippedException;


@Feature(name = "PET Manager", description = "Manejo avanzado de módulos PET basado en la vida del jugador y la PET." )
public class PetModule implements Behavior, Configurable<PetModule.Config> {

    private final HeroAPI hero;
    private final PetManager petManager;
    private Config config;
    private PetGear lastGear;

    @Configuration("pet_manager.config")
    public static class Config {
        public PetGear defaultModule = PetGear.PASSIVE;

        public  boolean useDefaultModule = true;

        public boolean useComboModule = false;
        public @Number(min = 1, max = 100) int minHpCombo = 50;

        public boolean useHpLink = false;
        public @Number(min = 1, max = 100) int minHpLink = 40;

        public boolean useRepairModule = false;
        public @Number(min = 1, max = 100) int minRepairModule = 30;

        public boolean useDefenseModule = false;
        public @Number(min = 1, max = 100) int minDefenseModule = 25;

        public boolean useLlamaExpiratoria = false;
        public @Number(min = 1, max = 100) int minLlamaExpiratoria = 20;

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
        try {
            managePetModule();
        } catch (ItemNotEquippedException e) {
            throw new RuntimeException(e);
        }
    }

    private void managePetModule() throws ItemNotEquippedException {
        if(!petManager.isActive() ) return;
        PetGear selectedGear = config.defaultModule;

        if(config.useComboModule && hero.getHealth().hpPercent() <= config.minHpCombo ) {
            petManager.setGear(PetGear.COMBO_REPAIR );
        }
        else if (config.useHpLink && hero.getHealth().hpPercent() <= config.minHpLink) {
            petManager.setGear(PetGear.HP_LINK);
        }
        else if (config.useRepairModule && hero.getHealth().hpPercent() <= config.minRepairModule ) {
            petManager.setGear(PetGear.REPAIR);
        }
        else if (config.useDefenseModule && hero.getHealth().hpPercent() <= config.minDefenseModule ) {
            petManager.setGear(PetGear.COMBO_GUARD);
        }
        else if (config.useLlamaExpiratoria && getSyncPlayerHealth() <= config.minLlamaExpiratoria ){
            petManager.setGear(PetGear.SACRIFICIAL);
        }

        if (lastGear != selectedGear) {
            petManager.setGear(selectedGear);
            lastGear = selectedGear;
        }
    }

    private int getSyncPlayerHealth() {
        return 50;
    }
}
