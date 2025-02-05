package org.Pet;

import com.github.manolo8.darkbot.config.types.Option;
import com.github.manolo8.darkbot.core.manager.PetManager;
import eu.darkbot.api.config.ConfigSetting;
import eu.darkbot.api.config.annotations.Configuration;
import eu.darkbot.api.config.annotations.Number;
import eu.darkbot.api.extensions.Behavior;
import eu.darkbot.api.extensions.Configurable;
import eu.darkbot.api.extensions.Feature;
import eu.darkbot.api.game.entities.Npc;
import eu.darkbot.api.managers.EntitiesAPI;
import eu.darkbot.api.game.entities.Entity;
import eu.darkbot.api.game.entities.Ship;
import eu.darkbot.api.game.enums.PetGear;
import eu.darkbot.api.managers.HeroAPI;
import eu.darkbot.api.managers.PetAPI;
import eu.darkbot.api.utils.ItemNotEquippedException;

import java.util.List;
import java.util.stream.Collectors;


@Feature(name = "PET Manager", description = "Manejo avanzado de módulos PET basado en la vida del jugador y la PET." )
public class PetModule implements Behavior, Configurable<PetModule.Config> {

    private final HeroAPI hero;
    private final PetManager petManager;
    private PetAPI petAPI;
    private Config config;
    private final EntitiesAPI entities;

    private long lastMegaMinaUse = 0;
    private long lastDamageModuleUse = 0;
    private long lastLifeModuleUse = 0;

    private static final long MEGA_MINA_COOLDOWN = 30000; //30 segundos
    private static final long DAMAGE_MODULE_COOLDOWN = 20000; // 20 segundos
    private static final long LIFE_MODULE_COOLDOWN = 25000; // 25 segundos

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



    public PetModule(HeroAPI hero, PetManager petManager, EntitiesAPI entities, PetAPI petAPI) {
        this.hero = hero;
        this.petManager = petManager;
        this.entities = entities;
        this.petAPI = petAPI;
    }

    private void useMegaMine(long now) throws ItemNotEquippedException {
        if(now - lastMegaMinaUse < MEGA_MINA_COOLDOWN) return ;

        List<Ship> enemies = entities.getShips().stream()
                .filter(ship -> ship.getEntityInfo().isEnemy() == true )
                .collect(Collectors.toList());

        if(enemies.size() == 8){
            petAPI.setGear(PetGear.MEGA_MINE);
        }

    }

    // usar modulo de damage
    private void useDamageModule(long now) throws ItemNotEquippedException {
        if(now - lastDamageModuleUse < DAMAGE_MODULE_COOLDOWN) return ;

        Entity target = hero.getTarget();
        if(target instanceof Npc ){
            petAPI.setGear(PetGear.BEACON_COMBAT);
        }
    }

    //usar modulo de damage
    private void useLifeModule(long now) throws ItemNotEquippedException {
        if(now - lastLifeModuleUse < LIFE_MODULE_COOLDOWN) return ;

        Entity target = hero.getTarget();
        if(target instanceof Npc ){
            petAPI.setGear(PetGear.BEACON_HP);
        }

    }

    // modulo defensa
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

    // combo module
    private void useComboModule() throws ItemNotEquippedException {
        if(config.useComboModule && hero.getHealth().hpPercent() <= config.min_Hp_Combo){
            petAPI.setGear(PetGear.COMBO_REPAIR);
        }
    }

    // usar hp link
    private void useHPLink() throws ItemNotEquippedException {
        if (config.useHpLink && hero.getHealth().hpPercent() <= config.min_Hp_Link) {
            petManager.setGear(PetGear.HP_LINK);
        }

    }

    // use Repair Module
    private void useRepairModule() throws ItemNotEquippedException {
        if (config.useRepairModule && petAPI.getHealth().hpPercent() <= config.min_Repair_Module ) {
            petManager.setGear(PetGear.REPAIR);
        }
    }

    // use Llama Expiatoria
    private void useLlamaExpiratoria() throws ItemNotEquippedException {
        if (config.useLlamaExpiratoria && getSyncPlayerHealth() <= config.min_Llama_Expiratoria ){
            petManager.setGear(PetGear.SACRIFICIAL);
        }
    }


    private void managePetModule() throws ItemNotEquippedException {
        if(!petManager.isActive() ) return;
        PetGear selectedGear = config.defaultModule;
        long now = System.currentTimeMillis();

        try {
            activeModuleDefense();
            useComboModule();
            useHPLink();
            useRepairModule();
            useLlamaExpiratoria();
            useMegaMine(now);
            useDamageModule(now);
            useLifeModule(now);
        } catch (ItemNotEquippedException e) {
            System.out.println(e.getMessage());;
        }

    }

    private int getSyncPlayerHealth() {
        return 50;
    }



    @Override
    public void setConfig(ConfigSetting<Config> configSetting) {
        if(configSetting == null){
            System.out.println("Error: configuración de PET Manager no encontrada.");
            return;
        }
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
