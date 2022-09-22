package xiamomc.survivalcompetition;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xiamomc.survivalcompetition.Annotations.Initializer;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.Managers.IMultiverseManager;
import xiamomc.survivalcompetition.Managers.IPlayerListManager;
import xiamomc.survivalcompetition.Misc.Colors;
import xiamomc.survivalcompetition.Misc.PluginObject;
import xiamomc.survivalcompetition.Annotations.Resolved;

import java.util.UUID;

public class StartingGame extends PluginObject
{
    public StartingGame()
    {
    }

    @Initializer
    private void init()
    {
        noticeGameStarting();
        dayTriggers();
        gameEnding();
    }

    @Resolved
    private IMultiverseManager imm;

    @Resolved
    private IGameManager igm;

    @Resolved
    private IPlayerListManager ipm;

    public boolean generateNewWorld()
    {
        String worldName = igm.getNewWorldName();

        if (imm.createWorlds(worldName))
        {
            imm.createSMPWorldGroup(worldName);
            imm.linkSMPWorlds(worldName);
            Bukkit.getServer().broadcast(Component.translatable("新的比赛世界已生成，正在传送玩家到新世界......", Colors.Green));
            this.AddSchedule(c ->
            {
                for (UUID uuid : ipm.getList())
                {
                    imm.tpToWorld(Bukkit.getPlayer(uuid).getName(), worldName);
                }
                igm.startGame();
            });
        }
        else
        {
            Bukkit.getServer().broadcast(Component.translatable("世界生成出错！请联系管理员检查 log", Colors.Red));
            return false;
        }

        return true;
    }

    public void noticeGameStarting()
    {
        Bukkit.getServer().broadcast(Component.translatable("正在生成新的世界......", Colors.Red));

        this.AddSchedule(c -> generateNewWorld());
    }

    public void dayTriggers()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                igm.firstDayTrigger(ipm.getList());
            }
        }.runTaskLater(SurvivalCompetition.instance, 0);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                igm.secondDayTrigger(ipm.getList());
            }
        }.runTaskLater(SurvivalCompetition.instance, 1500);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                igm.thirdDayTrigger(ipm.getList());
            }
        }.runTaskLater(SurvivalCompetition.instance, 3000);
    }

    public void gameEnding()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                igm.endGame(ipm.getList());
            }
        }.runTaskLater(SurvivalCompetition.instance, 4500);
    }
}
