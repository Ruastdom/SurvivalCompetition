package xiamomc.survivalcompetition.Managers;

import net.md_5.bungee.api.chat.BaseComponent;

import java.util.List;

public interface ICareerManager {
    List<BaseComponent> getCareerList();
    void initCareersComponents();
    void clear();
    boolean addToCareer( String playerName, String careerName );
    String getPlayerCareer( String playerName );
}
