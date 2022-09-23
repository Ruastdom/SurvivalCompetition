package xiamomc.survivalcompetition.Misc;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
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
                     int lasts, boolean spreadsPlayer, boolean refreshTeams, boolean allowCareerSelect)
    {
        Name = name;
        TitleMain = titleMain;
        TitleSub = titleSub;
        Lasts = lasts;
        SpreadsPlayer = spreadsPlayer;
        RefreshTeams = refreshTeams;
        AllowCareerSelect = allowCareerSelect;
    }

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        var result = new LinkedHashMap<String, Object>();
        result.put("Name", this.Name);
        result.put("TitleMain", this.TitleMain);
        result.put("TitleSub", this.TitleSub);
        result.put("Lasts", this.Lasts);
        result.put("SpreadsPlayer", this.SpreadsPlayer);
        result.put("RefreshTeams", this.RefreshTeams);
        result.put("AllowCareerSelect", this.AllowCareerSelect);

        return result;
    }

    public static StageInfo deserialize(Map<String, Object> map)
    {
        return new StageInfo(
                (String) map.getOrDefault("Name", "未知名称"),
                (String) map.getOrDefault("TitleMain", "未知标题"),
                (String) map.getOrDefault("TitleSub", "未知副标题"),
                (int) map.getOrDefault("Lasts", -1),
                (boolean) map.getOrDefault("SpreadsPlayer", false),
                (boolean) map.getOrDefault("RefreshTeams", false),
                (boolean) map.getOrDefault("AllowCareerSelect", false)
        );
    }
}
