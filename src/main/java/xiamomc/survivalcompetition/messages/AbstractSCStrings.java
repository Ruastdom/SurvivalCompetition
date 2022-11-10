package xiamomc.survivalcompetition.messages;

import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.survivalcompetition.SurvivalCompetition;

public class AbstractSCStrings
{
    protected static FormattableMessage getFormattable(String key, String defaultValue)
    {
        return new FormattableMessage(SurvivalCompetition.getSCNameSpace(), key, defaultValue);
    }
}
