package xiamomc.survivalcompetition.Managers;

public interface IMultiverseManager {
    boolean createWorlds( String worldName );
    boolean deleteWorlds( String worldName );
    void createSMPWorldGroup( String worldName );
    boolean linkSMPWorlds( String worldName );
    void tpToWorld( String playerName, String worldName);
}
