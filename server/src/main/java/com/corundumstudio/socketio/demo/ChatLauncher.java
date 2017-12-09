package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.store.RedissonStoreFactory;
import org.redisson.Redisson;
import org.redisson.config.Config;

import java.util.concurrent.atomic.AtomicInteger;

public class ChatLauncher {

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }

    public static void main(String[] args) throws InterruptedException {
        int port = Integer.valueOf(args[0]);

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(port);

//        config.setTransports(Transport.WEBSOCKET);
        SocketConfig socketConfig = config.getSocketConfig();
        socketConfig.setReuseAddress(true);

        Config redissonConfig = new Config();
        redissonConfig.useSingleServer().setAddress("redis://127.0.0.1:6379");
        Redisson redisson = (Redisson) Redisson.create(redissonConfig);
        RedissonStoreFactory redisStoreFactory = new RedissonStoreFactory(redisson);

        config.setStoreFactory(redisStoreFactory);

        final AtomicInteger idGenerator = new AtomicInteger(0);


        final GameService gameService = new GameService(redisson);

        final SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println(client.getSessionId() + " connected");

                System.out.println(gameService.getGameMap());

                gameService.start(client.getSessionId().toString(), idGenerator.incrementAndGet());
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {

                System.out.println(client.getSessionId() + " disconnected");

                gameService.leave(client.getSessionId().toString(), true);

                System.out.println(gameService.getGameMap());
            }
        });

        server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                // broadcast messages to all clients
                System.out.println(client.getSessionId() + " send " + data);
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}
