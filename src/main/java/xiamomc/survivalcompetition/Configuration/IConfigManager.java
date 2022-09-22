package xiamomc.survivalcompetition.Configuration;

public interface IConfigManager
{
    /**
     * 从配置获取值
     * @param path 配置中的路径
     * @return 获取到的值
     */
    public Object Get(String path);

    /**
     * 向配置路径设置值
     * @param path 目标路径
     * @param value 要设置的值
     * @return 设置是否成功
     */
    public boolean Set(String path, Object value);

    /**
     * 恢复默认配置
     * @return 操作是否成功
     */
    public boolean RestoreDefaults();

    /**
     * 刷新配置
     */
    public void Refresh();
}
