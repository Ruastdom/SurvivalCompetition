package xiamomc.survivalcompetition.Misc;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.scoreboard.Team;

public class TeamInfo
{
    public Team Team = null;
    //todo: 将两个String转为Component使用?
    public String Name = "队伍名称";
    public String Description = "队伍描述";
    public String Identifier = null;
    public TextColor Color;

    public TeamInfo()
    {
    }

    public TeamInfo(String name, String desc, String identifier)
    {
        this(name, desc, identifier, TextColor.color(255, 255, 255));
    }

    public TeamInfo(String name, String desc, String identifier, TextColor color)
    {
        this.Name = name;
        this.Description = desc;
        this.Identifier = identifier;
        this.Color = color;
    }
}
