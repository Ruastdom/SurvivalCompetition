package xiamomc.survivalcompetition.Misc;

import xiamomc.survivalcompetition.Exceptions.NullDependencyException;
import xiamomc.survivalcompetition.Managers.GameDependencyManager;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class PluginObject
{
    protected final SurvivalCompetition Plugin = SurvivalCompetition.GetInstance();
    protected final GameDependencyManager Dependencies = GameDependencyManager.GetInstance();

    private List<Field> fieldsToResolve;

    protected PluginObject()
    {
        //解析需要立即获取的依赖
        var ftr = new ArrayList<>(Arrays.stream(this.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Resolved.class)).toList());

        var fieldToResolveNow = ftr.stream()
                .filter(f -> f.getAnnotation(Resolved.class).ShouldSolveImmediately()).toList();

        for (Field f : fieldToResolveNow)
        {
            resolve(f);
            ftr.remove(f);
        }

        fieldsToResolve = ftr;

        this.AddSchedule(d -> this.resolveDependencies());
    }

    //todo: 使AddSchedule最终由PluginObject自己处理，而不是发给插件
    protected void AddSchedule(Consumer<?> c)
    {
        Plugin.Schedule(c);
    }

    private void resolveDependencies()
    {
        //自动对有Resolved的字段获取依赖
        for (Field field : fieldsToResolve)
        {
            resolve(field);
        }

        fieldsToResolve.clear();
        fieldsToResolve = null;
    }

    private void resolve(Field field)
    {
        //暂时让Resolved只对private生效
        if (Modifier.isPrivate(field.getModifiers()))
        {
            field.setAccessible(true);

            //Plugin.getLogger().info("Resolving " + field.getName() + "(" + field.getType() + ")" + "in " + this);

            try
            {
                //获取目标Class
                Class<?> targetClassType = field.getType();

                //从DependencyManager获取值
                Object value = Dependencies.Get(targetClassType, false);

                //判断是不是null
                if (value == null && ! field.getAnnotation(Resolved.class).AllowNull())
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
