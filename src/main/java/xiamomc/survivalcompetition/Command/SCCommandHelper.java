package xiamomc.survivalcompetition.Command;

import xiamomc.pluginbase.Command.CommandHelper;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.util.List;

public class SCCommandHelper extends CommandHelper<SurvivalCompetition>
{
    private final List<IPluginCommand> commands = List.of(
            new CareerCommand(),
            new GMCommand(),
            new JoinGameCommand()
    );

    @Override
    public List<IPluginCommand> getCommands()
    {
        return commands;
    }

    @Override
    protected XiaMoJavaPlugin getPlugin()
    {
        return SurvivalCompetition.GetInstance();
    }

    @Override
    protected String getPluginNamespace()
    {
        return SurvivalCompetition.GetInstance().getNameSpace();
    }
}
