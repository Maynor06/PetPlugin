package org.Pet;

import com.github.manolo8.darkbot.backpage.entities.ShipInfo;
import com.github.manolo8.darkbot.config.types.Option;
import com.github.manolo8.darkbot.core.manager.PetManager;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.extensions.Configurable;
import eu.darkbot.api.extensions.Feature;
import eu.darkbot.api.game.entities.Entity;
import eu.darkbot.api.game.entities.Ship;
import eu.darkbot.api.game.enums.PetGear;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.PetAPI;
import eu.darkbot.api.utils.ItemNotEquippedException;


@Feature(name = "PET Manager", description = "Manejo avanzado de módulos PET basado en la vida del jugador y la PET." )
public class PetModule implements Behavior, Configurable<PetModule.Config> {

    private final HeroAPI hero;
    private final Ship ship;
    private final PetManager petManager;
    private PetAPI petAPI;
    private Config config;
    private PetGear lastGear;

    @Configuration("pet_manager.config")
    public static class Config {
        public PetGear defaultModule = PetGear.PASSIVE;

        public boolean useDefaultModule = true;

        @Option("Min HP for use combo module")
        public boolean useComboModule = false;
        public @Number(min = 1, max = 100) int min_Hp_Combo = 50;

        @Option("Min HP for use hp link")
        public boolean useHpLink = false;
        public @Number(min = 1, max = 100) int min_Hp_Link = 40;

        @Option("Min HP for use module repair")
        public boolean useRepairModule = false;
        public @Number(min = 1, max = 100) int min_Repair_Module = 30;

        @Option("Min HP for use defense module")
        public boolean useDefenseModule = false;
        public @Number(min = 1, max = 100) int min_Defense_Module = 25;

        @Option("Min HP for use llama expiatoria")
        public boolean useLlamaExpiratoria = false;
        public @Number(min = 1, max = 100) int min_Llama_Expiratoria = 20;

    }

    public PetModule(HeroAPI hero, Ship ship, PetManager petManager) {
        this.hero = hero;
        this.ship = ship;
        this.petManager = petManager;
    }

    private void activeModuleDefense() throws ItemNotEquippedException {
        Entity target = hero.getTarget();

        if(target instanceof Ship){
            Ship ship = (Ship) target;

            if(ship.getEntityInfo().isEnemy()){
                if(petAPI.getGear() != PetGear.GUARD){
                    petAPI.setGear(PetGear.GUARD);
                }
            }
        }
    }

    private void useComboModule() throws ItemNotEquippedException {
        if(config.useComboModule && hero.getHealth().hpPercent() <= config.min_Hp_Combo){
            petAPI.setGear(PetGear.COMBO_REPAIR);
        }
    }

    private void useHPLink() throws ItemNotEquippedException {
        if (config.useHpLink && hero.getHealth().hpPercent() <= config.min_Hp_Link) {
            petManager.setGear(PetGear.HP_LINK);
        }

    }

    private void useRepairModule() throws ItemNotEquippedException {
        if (config.useRepairModule && petAPI.getHealth().hpPercent() <= config.min_Repair_Module ) {
            petManager.setGear(PetGear.REPAIR);
        }
    }

    private void useLlamaExpiratoria() throws ItemNotEquippedException {
        if (config.useLlamaExpiratoria && getSyncPlayerHealth() <= config.min_Llama_Expiratoria ){
            petManager.setGear(PetGear.SACRIFICIAL);
        }
    }

    private void managePetModule() throws ItemNotEquippedException {
        if(!petManager.isActive() ) return;
        PetGear selectedGear = config.defaultModule;

        activeModuleDefense();
        useComboModule();
        useHPLink();
        useRepairModule();
        useLlamaExpiratoria();

    }

    private int getSyncPlayerHealth() {
        return 50;
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

}
