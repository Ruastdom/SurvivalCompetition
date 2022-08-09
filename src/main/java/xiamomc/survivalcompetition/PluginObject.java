package xiamomc.survivalcompetition;

import xiamomc.survivalcompetition.Exceptions.NullDependencyException;
import xiamomc.survivalcompetition.Managers.GameDependencyManager;

import java.util.LinkedHashMap;

public class PluginObject
{
    protected final SurvivalCompetition Plugin = SurvivalCompetition.GetInstance();
    protected final GameDependencyManager Dependencies = GameDependencyManager.GetInstance();

    protected void Schedule(Runnable runnable)
    {
    }
}
