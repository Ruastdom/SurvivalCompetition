package xiamomc.survivalcompetition.managers;

import org.bukkit.entity.Player;

import java.util.List;

public interface IPlayerListManager
{
    /**
     * Gets the player list for current game.
     *
     * @return A list containing all players who participate in the game
     */
    List<Player> getPlayers();

    /**
     * Check if a player is playing the game
     *
     * @param player Target player
     * @return Whether this player is playing the game
     */
    boolean contains(Player player);

    /**
     * Adds a player to the player list
     *
     * @param player Target player
     * @return Whether the operation was successful
     */
    boolean add(Player player);

    /**
     * Removes a player from the player list
     *
     * @param player Target player
     * @return Whether the operation was successful
     */
    boolean remove(Player player);

    /**
     * Removes all offline players from player list
     *
     * @return Whether ths operation was successful
     */
    boolean removeAllOffline();

    /**
     * Check if player list is empty
     *
     * @return Whether the list is empty or not
     */
    boolean isEmpty();

    /**
     * Gets how many players are playing the game
     *
     * @return Player count
     */
    int listAmount();

    /**
     * Clears the list
     *
     * @return Whether ths operation was successful
     */
    boolean clear();
}
