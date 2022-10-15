package xiamomc.survivalcompetition.messages;

import xiamomc.pluginbase.messages.FormattableMessage;

public class CommandStrings extends AbstractSCStrings
{
    public static FormattableMessage careerHelpString()
    {
        return getFormattable(getKey("career.help"),
                "设定自己的职业");
    }

    public static FormattableMessage gmCommandHelpString()
    {
        return getFormattable(getKey("game.help"),
                "管理游戏");
    }

    public static FormattableMessage gmReloadCommandHelpString()
    {
        return getFormattable(getKey("game.reload.help"),
                "重载插件配置");
    }

    public static FormattableMessage gmStopCommandHelpString()
    {
        return getFormattable(getKey("game.stopcurrent.help"),
                "停止当前游戏");
    }

    public static FormattableMessage joinGameCommandHelpString()
    {
        return getFormattable(getKey("joinsg.help"),
                "加入游戏");
    }

    private static String getKey(String key)
    {
        return "commands." + key;
    }
}
