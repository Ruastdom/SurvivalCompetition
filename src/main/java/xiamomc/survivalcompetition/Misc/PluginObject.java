package xiamomc.survivalcompetition.Misc;

import xiamomc.survivalcompetition.Exceptions.NullDependencyException;
import xiamomc.survivalcompetition.Managers.GameDependencyManager;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class PluginObject
{
    protected final SurvivalCompetition Plugin = SurvivalCompetition.GetInstance();
    protected final GameDependencyManager Dependencies = GameDependencyManager.GetInstance();

    protected PluginObject()
    {
        //自动对有Resolved的字段获取依赖
        for (Field field : this.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(Resolved.class))
            {
                //暂时让Resolved只对private生效
                if (Modifier.isPrivate(field.getModifiers()))
                {
                    field.setAccessible(true);

                    try
                    {
                        //获取目标Class
                        Class<?> targetClassType = field.getType();

                        //从DependencyManager获取值
                        Object value = Dependencies.Get(targetClassType, false);

                        //判断是不是null
                        if (value == null)
                            throw new NullDependencyException(this + "依赖" + targetClassType + ", 但其尚未被注册");

                        //设置值
                        field.set(this, value);
                    }
                    catch (IllegalAccessException e)
                    {
                        throw new RuntimeException(e);
                    }

                    field.setAccessible(false);
                }
                else
                    throw new RuntimeException("字段必须是private");
            }
        }
    }
}
