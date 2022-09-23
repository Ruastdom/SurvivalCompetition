package xiamomc.survivalcompetition.Misc;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.Annotations.NotSerializable;
import xiamomc.survivalcompetition.Annotations.Serializable;
import xiamomc.survivalcompetition.Misc.Serialize.ConfigSerializeUtils;

import java.util.Map;

public class TeamInfo implements ConfigurationSerializable
{
    @NotSerializable
    public Team Team = null;

    public String Name = "队伍名称";

    public String Description = "队伍描述";

    public String Identifier = null;

    @Serializable(target = "Prefix")
    public String TeamPrefix = "PFX - ";

    @NotSerializable
    public NamedTextColor TeamColor;

    private TeamInfo()
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

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        var result = ConfigSerializeUtils.Serialize(this);
        result.put("TeamColor", this.TeamColor.asHexString()); //NamedTextColor不支持序列化，只能先这样了

        return result;
    }

    public static TeamInfo deserialize(Map<String, Object> map)
    {
        var result = ConfigSerializeUtils.DeSerialize(new TeamInfo(), map);
        result.TeamColor = NamedTextColor.nearestTo(TextColor.fromHexString((String) map.getOrDefault("TeamColor", "#ffffff")));
        return result;
    }
}
