package xyz.geik.farmer.modules.autoharvest;

import lombok.Getter;
import xyz.geik.farmer.Main;
import xyz.geik.farmer.modules.FarmerModule;
import xyz.geik.glib.shades.xseries.XMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * AutoHarvest module main class
 */
@Getter
public class AutoHarvest extends FarmerModule {

    private boolean requirePiston = false, checkAllDirections = false, withoutFarmer = false, checkStock = true, defaultStatus = false;
    private String customPerm = "farmer.autoharvest";
    private List<String> crops = new ArrayList<>();

    private static AutoHarvest instance;
    /**
     * Get instance of the module
     *
     * @return
     */
    public static AutoHarvest getInstance() {
        return instance;
    }

    /**
     * Load module
     */
    @Override
    public void onLoad() {
        setName("AutoHarvest");
        setDescription("Automatically harvests crops");
        setModulePrefix("AutoHarvest");
        setConfig(Main.getInstance());
        instance = this;
        if (!getConfig().getBoolean("settings.feature"))
            this.setEnabled(false);
    }

    /**
     * Enable module
     */
    @Override
    public void onEnable() {
        setHasGui(true);
        getCrops().addAll(getConfig().getStringList("settings.items"));
        requirePiston = getConfig().getBoolean("settings.requirePiston");
        checkAllDirections = getConfig().getBoolean("settings.checkAllDirections");
        withoutFarmer = getConfig().getBoolean("settings.withoutFarmer");
        checkStock = getConfig().getBoolean("settings.checkStock");
        customPerm = getConfig().getString("settings.customPerm");
        defaultStatus = getConfig().getBoolean("settings.defaultStatus");
        registerListener(new AutoHarvestEvent());
        registerListener(new AutoHarvestGuiCreateEvent());
        setLang(Main.getConfigFile().getSettings().getLang(), Main.getInstance());
    }

    @Override
    public void onReload() {
        if (!this.isEnabled())
            return;
        if (!getCrops().isEmpty())
            getCrops().clear();
        getCrops().addAll(getConfig().getStringList("settings.items"));
        requirePiston = getConfig().getBoolean("settings.requirePiston");
        checkAllDirections = getConfig().getBoolean("settings.checkAllDirections");
        withoutFarmer = getConfig().getBoolean("settings.withoutFarmer");
        checkStock = getConfig().getBoolean("settings.checkStock");
        customPerm = getConfig().getString("settings.customPerm");
        defaultStatus = getConfig().getBoolean("settings.defaultStatus");
    }

    @Override
    public void onDisable() {

    }

    /**
     * Checks if auto harvest collect this crop.
     *
     * @param material
     * @return
     */
    public static boolean checkCrop(XMaterial material) {
        return getInstance().getCrops().stream().anyMatch(crop -> material.equals(XMaterial.valueOf(crop)));
    }
}
