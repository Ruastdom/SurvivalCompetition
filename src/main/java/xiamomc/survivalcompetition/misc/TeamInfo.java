package xiamomc.survivalcompetition.misc;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.NotSerializable;
import xiamomc.pluginbase.Annotations.Serializable;
import xiamomc.pluginbase.Serialize.ConfigSerializeUtils;

import java.util.Map;

public class TeamInfo implements ConfigurationSerializable
{
    @NotSerializable
    public Team team = null;

    @Serializable("Name")
    public String name = "队伍名称";

    @Serializable("Description")
    public String description = "队伍描述";

    @Serializable("Identifier")
    public String identifier = null;

    @Serializable(value = "Prefix")
    public String teamPrefix = "PFX - ";

    @NotSerializable
    public NamedTextColor teamColor;

    private TeamInfo()
    {
    }

    public TeamInfo(String name, String desc, String identifier)
    {
        this(name, name, desc, identifier, NamedTextColor.WHITE);
    }

    public TeamInfo(String name, String pfx, String desc, String identifier, NamedTextColor teamColor)
    {
        this.name = name;
        this.teamPrefix = pfx;
        this.description = desc;
        this.identifier = identifier;
        this.teamColor = teamColor;
    }

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        var result = ConfigSerializeUtils.serialize(this);
        result.put("teamColor", this.teamColor.asHexString()); //NamedTextColor不支持序列化，只能先这样了

        return result;
    }

    public static TeamInfo deserialize(Map<String, Object> map)
    {
        var result = ConfigSerializeUtils.deSerialize(new TeamInfo(), map);
        result.teamColor = NamedTextColor.nearestTo(TextColor.fromHexString((String) map.getOrDefault("teamColor", "#ffffff")));
        return result;
    }
}
