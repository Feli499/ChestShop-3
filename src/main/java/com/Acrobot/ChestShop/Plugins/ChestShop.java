package com.Acrobot.ChestShop.Plugins;

import static com.Acrobot.Breeze.Utils.BlockUtil.isChest;
import static com.Acrobot.Breeze.Utils.BlockUtil.isSign;

import com.Acrobot.ChestShop.Events.Protection.ProtectionCheckEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ChestShop implements Listener {
    @EventHandler
    public static void onProtectionCheck(ProtectionCheckEvent event) {
        if (event.getResult() == Event.Result.DENY || event.isBuiltInProtectionIgnored()) {
            return;
        }

        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!canAccess(player, block)) {
            event.setResult(Event.Result.DENY);
        }
    }

    public static boolean canAccess(Player player, Block block) {
        if (Permission.has(player, Permission.ADMIN) || !canBeProtected(block)) {
            return true;
        }

        if (isSign(block)) {
            Sign sign = (Sign) block.getState();

            if (!ChestShopSign.isChestShop(sign)) {
                return true;
            }

            if (!isShopMember(player, sign)) {
                return false;
            }
        }

        if (isChest(block)) {
            Sign sign = uBlock.getConnectedSign(block);

            if (sign != null && !isShopMember(player, sign)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canBeProtected(Block block) {
        return isSign(block) || isChest(block);
    }

    private static boolean isShopMember(Player player, Sign sign) {
        ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(sign);
        if (chestShopMetaData == null)
            return false;
        return chestShopMetaData.canAccess(player);
    }
}
