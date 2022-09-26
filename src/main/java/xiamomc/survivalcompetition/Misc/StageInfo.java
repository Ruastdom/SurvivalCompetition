package xiamomc.survivalcompetition.Misc;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.Misc.Serialize.ConfigSerializeUtils;

import java.util.Map;

public class StageInfo implements ConfigurationSerializable
{
    /**
     * 阶段名
     */
    public String Name;

    /**
     * 标题名称
     */
    public String TitleMain;

    /**
     * 副标题名称
     */
    public String TitleSub;

    /**
     * 持续多久（多少刻）
     */
    public int Lasts;

    /**
     * 是否允许保留物品栏（死亡不掉落）
     */
    public boolean AllowKeepInventory = true;

    /**
     * 是否扩散玩家
     */
    public boolean SpreadsPlayer;

    /**
     * 是否重新分队
     */
    public boolean RefreshTeams;

    /**
     * 是否允许职业选择
     */
    public boolean AllowCareerSelect;

    public StageInfo(String name, String titleMain, String titleSub,
                     int lasts, boolean spreadsPlayer,
                     boolean refreshTeams, boolean allowCareerSelect)
    {
        Name = name;
        TitleMain = titleMain;
        TitleSub = titleSub;
        Lasts = lasts;
        SpreadsPlayer = spreadsPlayer;
        RefreshTeams = refreshTeams;
        AllowCareerSelect = allowCareerSelect;
    }

    private StageInfo()
    {
    }

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        return ConfigSerializeUtils.Serialize(this);
    }

    public static StageInfo deserialize(Map<String, Object> map)
    {
        return ConfigSerializeUtils.DeSerialize(new StageInfo(), map);
    }
}
