package xiamomc.survivalcompetition.Misc;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class TeamInfo implements ConfigurationSerializable
{
    public Team Team = null;
    //todo: 将两个String转为Component使用?
    public String Name = "队伍名称";
    public String TeamPrefix = "PFX - ";
    public String Description = "队伍描述";
    public String Identifier = null;
    public NamedTextColor TeamColor;

    public TeamInfo()
    {
    }

    public TeamInfo(String name, String desc, String identifier)
    {
        this(name, name, desc, identifier, NamedTextColor.WHITE);
    }

    public TeamInfo(String name, String pfx, String desc, String identifier, NamedTextColor teamColor)
    {
        this.Name = name;
        this.TeamPrefix = pfx;
        this.Description = desc;
        this.Identifier = identifier;
        this.TeamColor = teamColor;
    }

    //NamedTextColor不支持序列化，只能先这样了
    @Override
    public @NotNull Map<String, Object> serialize()
    {
        var result = new LinkedHashMap<String, Object>();
        result.put("Name", this.Name);
        result.put("Prefix", this.TeamPrefix);
        result.put("Description", this.Description);
        result.put("Identifier", this.Identifier);
        result.put("TeamColor", this.TeamColor.asHexString());

        return result;
    }

    public static TeamInfo deserialize(Map<String, Object> map)
    {
        return new TeamInfo(
                (String) map.getOrDefault("Name", "未知队伍"),
                (String) map.getOrDefault("Prefix", "未知PFX"),
                (String) map.getOrDefault("Description", "未知描述"),
                (String) map.getOrDefault("Identifier", "NULL"),
                NamedTextColor.nearestTo(TextColor.fromHexString((String) map.getOrDefault("TeamColor", "#ffffff")))
        );
    }
}
