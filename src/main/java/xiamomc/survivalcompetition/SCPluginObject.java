package xiamomc.survivalcompetition;

import xiamomc.pluginbase.PluginObject;

public abstract class SCPluginObject extends PluginObject<SurvivalCompetition>
{
    @Override
    protected String getPluginNamespace()
    {
        return SurvivalCompetition.instance.getNameSpace();
    }
}
