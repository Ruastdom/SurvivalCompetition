package xiamomc.survivalcompetition.Managers;

import org.jetbrains.annotations.Nullable;
import xiamomc.survivalcompetition.Careers.AbstractCareer;

import java.util.List;

public interface ICareerManager
{
    /**
     * 获取职业列表
     *
     * @return 职业列表
     */
    List<AbstractCareer> getCareerList();

    /**
     * 清空所有玩家的职业
     */
    void clear();

    /**
     * 添加某一玩家到职业中
     *
     * @param playerName   目标玩家名
     * @param internalName 职业的内部名称
     * @return 添加过程是否成功
     */
    boolean addToCareer(String playerName, String internalName);

    /**
     * 获取某一玩家的职业
     *
     * @param playerName 目标玩家名
     * @return 职业信息（可能返回null）
     */
    @Nullable
    AbstractCareer getPlayerCareer(String playerName);
}
