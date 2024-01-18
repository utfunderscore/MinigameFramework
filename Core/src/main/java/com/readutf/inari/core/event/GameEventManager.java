package com.readutf.inari.core.event;

import com.readutf.inari.core.event.adapters.ChatAdapter;
import com.readutf.inari.core.event.adapters.EntityDamageAdapter;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;

public class GameEventManager implements Listener {

    private static final Logger logger = LoggerManager.getInstance().getLogger(GameEventManager.class);

    private final JavaPlugin javaPlugin;
    private final Map<UUID, Map<Class<? extends Event>, List<GameEventListener>>> gameIdToEventMethod;
    private final Map<Class<? extends Event>, GameEventAdapter> eventAdapterMap;
    private @Getter
    final List<Class<? extends Event>> activeDebugs;

    public GameEventManager(JavaPlugin javaPlugin, GameManager gameManager) {
        this.javaPlugin = javaPlugin;
        this.gameIdToEventMethod = new HashMap<>();
        this.eventAdapterMap = new HashMap<>();
        this.activeDebugs = new ArrayList<>();

        registerEventAdapter(EntityDamageEvent.class, new EntityDamageAdapter(gameManager));
        registerEventAdapter(AsyncPlayerChatEvent.class, new ChatAdapter(gameManager));
    }

    public void registerEventAdapter(Class<? extends Event> eventType, GameEventAdapter adapter) {
        eventAdapterMap.put(eventType, adapter);

        Bukkit.getPluginManager().registerEvent(eventType, this, EventPriority.NORMAL, new CustomEventExecutor(), javaPlugin);
    }

    public void scanForListeners(Game game, Object object) {

        Class<?> clazz = object.getClass();

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(GameEventHandler.class)) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != 1) {
                    logger.warn("Method " + method.getName() + " in class " + clazz.getName() + " must have only one parameter of type Event");
                    continue;
                }
                if (!Event.class.isAssignableFrom(parameters[0])) {
                    logger.warn("Method " + method.getName() + " in class " + clazz.getName() + " must have only one parameter of type Event");
                    continue;
                }
                Class<? extends Event> eventClass = (Class<? extends Event>) parameters[0];
                Map<Class<? extends Event>, List<GameEventListener>> eventMethodMap = gameIdToEventMethod.getOrDefault(game.getGameId(), new HashMap<>());
                List<GameEventListener> listeners = eventMethodMap.getOrDefault(eventClass, new ArrayList<>());
                listeners.add(new GameEventListener(object, method));
                eventMethodMap.put(eventClass, listeners);
                gameIdToEventMethod.put(game.getGameId(), eventMethodMap);
            }

        }

    }

    public class CustomEventExecutor implements EventExecutor {

        @Override
        public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
            boolean debugEvent = activeDebugs.contains(event.getClass());

            if(debugEvent) {
                logger.debug("Received event " + event.getClass().getSimpleName());
            }

            GameEventAdapter gameEventAdapter = eventAdapterMap.get(event.getClass());
            if (gameEventAdapter == null) {
                if(debugEvent) {
                    logger.debug("Failed to find GameEventAdapter for event " + event.getClass().getSimpleName());
                }
                return;
            }

            GameAdapterResult result = gameEventAdapter.getGame(event);
            if (!result.isSuccessful()) {
                if(debugEvent) {
                    logger.debug("Failed to find game for event " + event.getClass().getSimpleName() + " because " + result.getFailReason());
                }
                return;
            }

            Game game = result.getGame();
            if (game == null) {
                logger.warn("Successful GameAdapterResult should not return null");
                return;
            }


            List<GameEventListener> gameListeners = findEventListeners(game, event.getClass());

            if(debugEvent) {
                logger.debug("Found " + gameListeners.size() + " listeners for event " + event.getClass().getSimpleName());
            }

            for (GameEventListener gameListener : gameListeners) {
                try {
                    gameListener.getMethod().invoke(gameListener.getOwner(), event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        private List<GameEventListener> findEventListeners(Game game, Class<? extends Event> eventClass) {
            Map<Class<? extends Event>, List<GameEventListener>> listeners = gameIdToEventMethod.getOrDefault(game.getGameId(), new HashMap<>());
            if (listeners.containsKey(eventClass)) {
                return listeners.get(eventClass);
            }

            for (Class<? extends Event> aClass : listeners.keySet()) {
                if (aClass.isAssignableFrom(eventClass)) {
                    return listeners.get(aClass);
                }
            }

            return new ArrayList<>();
        }

    }


}

