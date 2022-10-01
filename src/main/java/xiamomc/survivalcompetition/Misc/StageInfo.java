package xiamomc.survivalcompetition.Misc;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Serializable;
import xiamomc.pluginbase.Serialize.ConfigSerializeUtils;

import java.util.Map;

public class StageInfo implements ConfigurationSerializable
{
    /**
     * 阶段名
     */
    @Serializable("Name")
    public String name;

    /**
     * 标题名称
     */
    @Serializable("TitleMain")
    public String titleMain;

    /**
     * 副标题名称
     */
    @Serializable("TitleSub")
    public String titleSub;

    /**
     * 持续多久（多少刻）
     */
    @Serializable("Lasts")
    public int lasts;

    /**
     * 是否允许保留物品栏（死亡不掉落）
     */
    @Serializable("AllowKeepInventory")
    public boolean allowKeepInventory = true;

    /**
     * 是否扩散玩家
     */
    @Serializable("SpreadsPlayer")
    public boolean spreadsPlayer;

    /**
     * 是否重新分队
     */
    @Serializable("RefreshTeams")
    public boolean refreshTeams;

    /**
     * 是否允许职业选择
     */
    @Serializable("AllowCareerSelect")
    public boolean AllowCareerSelect;

    public StageInfo(String name, String titleMain, String titleSub,
                     int lasts, boolean spreadsPlayer,
                     boolean refreshTeams, boolean allowCareerSelect)
    {
        this.name = name;
        this.titleMain = titleMain;
        this.titleSub = titleSub;
        this.lasts = lasts;
        this.spreadsPlayer = spreadsPlayer;
        this.refreshTeams = refreshTeams;
        AllowCareerSelect = allowCareerSelect;
    }

    private StageInfo()
    {
    }

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        return ConfigSerializeUtils.serialize(this);
    }

    public static StageInfo deserialize(Map<String, Object> map)
    {
        return ConfigSerializeUtils.deSerialize(new StageInfo(), map);
    }
}
