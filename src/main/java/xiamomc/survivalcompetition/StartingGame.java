package xiamomc.survivalcompetition;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xiamomc.survivalcompetition.Managers.*;

import java.util.UUID;

public class StartingGame extends PluginObject {
    public StartingGame(){
        noticeGameStarting();
        dayTriggers();
        gameEnding();
    }

    public boolean worldHandler(){
        IMultiverseManager imm = (IMultiverseManager) Dependencies.Get(IMultiverseManager.class);
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

        String worldName = igm.getNewWorldName();
        final boolean[] createWorldsStats = new boolean[1];
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getServer().broadcast(Component.text("正在等待新世界生成......", TextColor.color(255,0,0)));
                createWorldsStats[0] = imm.createWorlds(worldName);
                if(createWorldsStats[0]){
                    imm.createSMPWorldGroup(worldName);
                    imm.linkSMPWorlds(worldName);
                    Bukkit.getServer().broadcast(Component.text("新的比赛世界已生成，正在传送玩家到新世界......", TextColor.color(0,255,0)));
                    for (UUID uuid : ipm.getList()) {
                        imm.tpToWorld(Bukkit.getPlayer(uuid).getName(), worldName);
                    }
                    igm.startGame();
                } else {
                    Bukkit.getServer().broadcast(Component.text("世界生成出错！请联系管理员检查 log", TextColor.color(255,0,0)));
                }
            }
        }.runTask(SurvivalCompetition.instance);
        return true;
    }
    public void noticeGameStarting(){
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);
        worldHandler();
    }

    public void dayTriggers(){
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

        new BukkitRunnable(){
            @Override
            public void run() {
                igm.firstDayTrigger(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 0);
        new BukkitRunnable(){
            @Override
            public void run() {
                igm.secondDayTrigger(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 1500);
        new BukkitRunnable(){
            @Override
            public void run() {
                igm.thirdDayTrigger(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 3000);
    }

    public void gameEnding(){
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

        new BukkitRunnable(){
            @Override
            public void run() {
                igm.endGame(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 4500);
    }
}
