package xiamomc.survivalcompetition.Misc;

import org.slf4j.Logger;
import xiamomc.survivalcompetition.Annotations.Initializer;
import xiamomc.survivalcompetition.Annotations.Resolved;
import xiamomc.survivalcompetition.Exceptions.NullDependencyException;
import xiamomc.survivalcompetition.Managers.GameDependencyManager;
import xiamomc.survivalcompetition.ScheduleInfo;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class PluginObject
{
    protected final SurvivalCompetition Plugin = SurvivalCompetition.GetInstance();
    protected final GameDependencyManager Dependencies = GameDependencyManager.GetInstance();
    protected final Logger Logger = Plugin.getSLF4JLogger();

    private List<Field> fieldsToResolve;

    private Method initializerMethod;

    protected PluginObject()
    {
        initialDependencyResolve();
    }

    //region 依赖处理

    private void initialDependencyResolve()
    {
        //region 获取初始化方法

        var initializerMethods = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Initializer.class)).toList();

        if (initializerMethods.size() > 1) throw new RuntimeException("不能拥有多个初始化方法");
        else initializerMethods.stream().findFirst().ifPresent(m -> this.initializerMethod = m);

        //endregion

        //region 解析需要获取依赖的字段

        var ftr = new ArrayList<>(Arrays.stream(this.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Resolved.class)).toList());

        var fieldToResolveNow = ftr.stream()
                .filter(f -> f.getAnnotation(Resolved.class).shouldSolveImmediately()).toList();

        for (Field f : fieldToResolveNow)
        {
            resolveField(f);
            ftr.remove(f);
        }

        fieldsToResolve = ftr;

        this.addSchedule(d -> this.resolveRemainingDependencies());

        //endregion
    }

    private void resolveRemainingDependencies()
    {
        //自动对有Resolved的字段获取依赖
        for (Field field : fieldsToResolve)
        {
            resolveField(field);
        }

        fieldsToResolve.clear();
        fieldsToResolve = null;

        //执行初始化方法
        if (initializerMethod != null)
        {
            //和Resolved一样，只对private生效
            if (Modifier.isPrivate(initializerMethod.getModifiers()))
            {
                //获取参数
                var parameters = initializerMethod.getParameters();

                //对应的值
                var values = new ArrayList<>();

                //逐个获取依赖
                for (var p : parameters)
                {
                    var targetClassType = p.getType();
                    Object value = Dependencies.Get(targetClassType, false);

                    if (value == null) throwDependencyNotFound(targetClassType);

                    values.add(value);
                }

                //尝试调用
                try
                {
                    initializerMethod.setAccessible(true);

                    initializerMethod.invoke(this, values.toArray());

                    initializerMethod.setAccessible(false);
                }
                catch (IllegalAccessException | InvocationTargetException e)
                {
                    initializerMethod.setAccessible(false);
                    throw new RuntimeException(e);
                }
            }
            else
                throw new RuntimeException("初始化方法不能是private");
        }
    }

    private void resolveField(Field field)
    {
        //暂时让Resolved只对private生效
        if (Modifier.isPrivate(field.getModifiers()))
        {
            field.setAccessible(true);

            //Logger.info("Resolving " + field.getName() + "(" + field.getType() + ")" + "in " + this);

            try
            {
                //获取目标Class
                Class<?> targetClassType = field.getType();

                //从DependencyManager获取值
                Object value = Dependencies.Get(targetClassType, false);

                //判断是不是null
                if (value == null && !field.getAnnotation(Resolved.class).allowNull())
                    throwDependencyNotFound(targetClassType);

                //设置值
                field.set(this, value);
            }
            catch (IllegalAccessException e)
            {
                field.setAccessible(false);
                throw new RuntimeException(e);
            }

            field.setAccessible(false);
        }
        else
            throw new RuntimeException("字段必须是private");
    }

    private void throwDependencyNotFound(Class<?> targetClassType)
    {
        throw new NullDependencyException(this.getClass().getSimpleName()
                + "依赖"
                + targetClassType.getSimpleName()
                + ", 但其尚未被注册");
    }

    //endregion

    //region Schedules

    //todo: 使AddSchedule最终由PluginObject自己处理，而不是发给插件
    protected ScheduleInfo addSchedule(Consumer<?> c)
    {
        return this.addSchedule(c, 0);
    }

    protected ScheduleInfo addSchedule(Consumer<?> c, int delay)
    {
        return Plugin.schedule(c, delay);
    }

    //endregion
}
