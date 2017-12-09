package com.corundumstudio.socketio.demo;

import org.redisson.Redisson;
import org.redisson.api.RMap;

import java.util.Map;

public class GameService {
    private Redisson redisson;

    private final RMap<Integer, Game> gameMap;

    private final RMap<String, Integer> userMap;


    public GameService(Redisson redisson) {
        this.gameMap = redisson.getMap("game");
        this.userMap = redisson.getMap("user");
    }

    public void start(String user, int gameId) {
        gameMap.put(gameId, new Game(gameId, user, false));
        userMap.put(user, gameId);
    }

    public void leave(String user, boolean result) {
        Integer gameId = userMap.get(user);
        if (gameId != null) {
            end(gameId, result);
        }
    }

    public void end(int gameId, boolean result) {
        Game o = gameMap.get(gameId);
        if (o != null) {
            o.setResult(result);
            gameMap.put(gameId, o);
            userMap.remove(o.getUser());
        }
    }

    public Map<Integer, Game> getGameMap() {
        return gameMap;
    }
}
