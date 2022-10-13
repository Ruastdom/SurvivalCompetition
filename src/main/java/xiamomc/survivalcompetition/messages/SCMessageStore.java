package xiamomc.survivalcompetition.messages;

import xiamomc.pluginbase.messages.IStrings;
import xiamomc.pluginbase.messages.MessageStore;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.util.ArrayList;
import java.util.List;

public class SCMessageStore extends MessageStore
{
    private final List<Class<IStrings>> cachedClassList = new ArrayList<>();

    private final List<Class<?>> rawClassList = List.of(
            CommandStrings.class
    );

    @Override
    protected List<Class<IStrings>> getStrings()
    {
        if (cachedClassList.size() == 0)
        {
            rawClassList.forEach(c -> cachedClassList.add((Class<IStrings>) c));
        }

        return cachedClassList;
    }

    @Override
    protected String getPluginNamespace()
    {
        return SurvivalCompetition.instance.getNameSpace();
    }
}
