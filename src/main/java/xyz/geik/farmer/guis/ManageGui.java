package xyz.geik.farmer.guis;

import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import xyz.geik.farmer.Main;
import xyz.geik.farmer.api.FarmerAPI;
import xyz.geik.farmer.helpers.gui.GuiHelper;
import xyz.geik.farmer.model.Farmer;
import xyz.geik.farmer.model.FarmerLevel;

/**
 * Manage gui can be openable
 * by owner or administrator only
 */
public class ManageGui {

    /**
     * Constructor of class
     */
    public ManageGui() {}

    /**
     * Shows gui to player also contains event of it
     *
     * @param player to show gui
     * @param farmer of region
     */
    public static void showGui(Player player, Farmer farmer) {
        // Gui interface array
        String[] guiSetup = Main.getLangFile().getStringList("manageGui.interface").toArray(new String[0]);
        // Inventory object
        InventoryGui gui = new InventoryGui(Main.getInstance(), null, PlaceholderAPI.setPlaceholders(null, Main.getLangFile().getText("manageGui.guiName")), guiSetup);
        // Filler for empty slots
        gui.setFiller(GuiHelper.getFiller());
        // Change state of Farmer Icon
        gui.addElement(new DynamicGuiElement('t', (viewer) -> {
            return new StaticGuiElement('t',
                // Placing item depending on state of farmer (Collecting or not)
                GuiHelper.getStatusItem(farmer.getState()),
                1,
                // Event of status change
                click -> {
                    if (farmer.getState() == 0)
                      farmer.setState(1);
                    else
                      farmer.setState(0);
                    gui.draw();
                    player.sendMessage(Main.getLangFile().getText("toggleFarmer")
                          .replace("{status}", (farmer.getState() == 0) ?
                                  Main.getLangFile().getText("toggleOFF") :
                                  Main.getLangFile().getText("toggleON")));
                    return true;
                });
        }));
        // Users gui opener
        gui.addElement(new StaticGuiElement('u',
                GuiHelper.getItem("manageGui.users"),
                1,
                click -> {
                    UsersGui.showGui(player, farmer);
                    return true;
                })
        );
        // Level icon
        gui.addElement(new DynamicGuiElement('l', (viewer) -> {
            return new StaticGuiElement('l',
                // Level item
                GuiHelper.getLevelItem(farmer),
                1,
                // Event of level item click
                click -> {
                    int nextLevelIndex = FarmerLevel.getAllLevels().indexOf(farmer.getLevel())+1;
                    if (!(FarmerLevel.getAllLevels().size()-1 < nextLevelIndex)) {
                        FarmerLevel nextLevel = FarmerLevel.getAllLevels()
                                .get(nextLevelIndex);
                        if (Main.getEconomyIntegrations().getBalance(player) >= nextLevel.getReqMoney()) {
                            if (nextLevel.getPerm() != null && !player.hasPermission(nextLevel.getPerm()))
                                player.sendMessage(Main.getLangFile().getText("noPerm"));
                            else {
                                Main.getEconomyIntegrations().withdrawPlayer(player, nextLevel.getReqMoney());
                                farmer.setLevel(nextLevel);
                                farmer.getInv().setCapacity(nextLevel.getCapacity());
                                player.sendMessage(Main.getLangFile().getText("levelUpgraded")
                                        .replace("{level}", String.valueOf(nextLevelIndex+1))
                                        .replace("{capacity}", String.valueOf(nextLevel.getCapacity())));
                            }
                        }
                        else
                            player.sendMessage(Main.getLangFile().getText("notEnoughMoney")
                                    .replace("{req_money}", String.valueOf(nextLevel.getReqMoney())));
                        gui.draw();
                    }
                    return true;
                });
            })
        );
        // Module icon
        if (FarmerAPI.getModuleManager().isModulesUseGui())
            gui.addElement(new StaticGuiElement('m',
                    GuiHelper.getItem("manageGui.modules"),
                    1,
                    click -> {
                        ModuleGui.showGui(player, farmer);
                        return true;
                    })
            );
        gui.show(player);
    }

}
