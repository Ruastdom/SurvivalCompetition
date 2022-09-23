package xiamomc.survivalcompetition.Configuration;

import java.util.function.Consumer;

public interface IConfigManager
{
    /**
     * 从配置获取值
     *
     * @param type 目标类型
     * @param path 配置中的路径
     * @return 获取到的值
     */
    public <T> T Get(Class<T> type, ConfigNode path);

    /**
     * 向配置路径设置值
     *
     * @param path  目标路径
     * @param value 要设置的值
     * @return 设置是否成功
     */
    public boolean Set(ConfigNode path, Object value);

    /**
     * 恢复默认配置
     *
     * @return 操作是否成功
     */
    public boolean RestoreDefaults();

    /**
     * 刷新配置
     */
    public void Reload();

    /**
     * 当配置被刷新时要进行的操作
     *
     * @param c 要添加的计划任务
     * @return 添加是否成功
     */
    public boolean OnConfigRefresh(Consumer<?> c);

    /**
     * 当配置被刷新时要进行的操作
     *
     * @param c 要添加的计划任务
     * @param runOnce 是否要立即执行
     * @return 添加是否成功
     */
    public boolean OnConfigRefresh(Consumer<?> c, boolean runOnce);
}
