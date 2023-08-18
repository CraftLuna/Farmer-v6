package xyz.geik.farmer.modules.voucher;

import lombok.Getter;
import xyz.geik.farmer.Main;
import xyz.geik.farmer.helpers.Settings;
import xyz.geik.farmer.modules.FarmerModule;

/**
 * Voucher module main class
 */
@Getter
public class Voucher extends FarmerModule {

    /**
     * Instance of the module
     * -- GETTER --
     */
    @Getter
    private static Voucher instance;

    private boolean overrideFarmer = false, giveVoucherWhenRemove = false;

    /**
     * onLoad method of module
     */
    @Override
    public void onLoad() {
        this.setName("Voucher");
        this.setDescription("Voucher module");
        this.setModulePrefix("Voucher");
        instance = this;
        this.setConfig(Main.getInstance());
        if (!getConfig().getBoolean("settings.feature"))
            this.setEnabled(false);
    }

    /**
     * onEnable method of module
     */
    @Override
    public void onEnable() {
        this.setLang(Settings.lang, Main.getInstance());
        registerListener(new VoucherEvent());
        overrideFarmer = getConfig().get("settings.useWhenFarmerExist", false);
        giveVoucherWhenRemove = getConfig().get("settings.giveVoucherWhenRemove", false);
    }

    /**
     * onReload method of module
     */
    @Override
    public void onReload() {
        if (!this.isEnabled())
            return;
        overrideFarmer = getConfig().get("settings.useWhenFarmerExist", false);
        giveVoucherWhenRemove = getConfig().get("settings.giveVoucherWhenRemove", false);
    }

    /**
     * onDisable method of module
     */
    @Override
    public void onDisable() {}
}
