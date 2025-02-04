package de.codingair.tradesystem.trade.listeners;

import de.codingair.codingapi.tools.time.TimeList;
import de.codingair.tradesystem.TradeSystem;
import de.codingair.tradesystem.trade.Trade;
import de.codingair.tradesystem.trade.commands.TradeCMD;
import de.codingair.tradesystem.trade.commands.TradeSystemCMD;
import de.codingair.tradesystem.utils.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class TradeListener implements Listener {
    private TimeList<Player> players = new TimeList<>();

    @EventHandler
    public void onLie(PlayerBedEnterEvent e) {
        TradeSystem.getInstance().getTradeCMD().removesAllInvitesFrom(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if(!TradeSystem.getInstance().getTradeManager().isRequestOnRightclick() || players.contains(e.getPlayer())) return;

        if(e.getRightClicked() instanceof Player) {
            Player p = e.getPlayer();
            Player other = (Player) e.getRightClicked();

            if(TradeSystem.getInstance().getTradeManager().isShiftclick() == p.isSneaking()) {
                players.add(p, 1);
                TradeCMD.request(p, other);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Trade trade = TradeSystem.getInstance().getTradeManager().getTrade(player);

            if(trade != null) {
                double finalDamage = e.getFinalDamage();
                if((TradeSystem.getInstance().getTradeManager().isCancelOnDamage() && finalDamage > 0) || (player.getHealth() - e.getFinalDamage() <= 0))
                    trade.cancel(Lang.getPrefix() + Lang.get("Trade_cancelled_by_attack"));
            }
        }
    }

}
