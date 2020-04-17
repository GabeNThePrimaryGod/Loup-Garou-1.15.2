package Loup_Garou_1_15_2.Game;

import Loup_Garou_1_15_2.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleEventEmitter
{
    class Event
    {
        List<Consumer<Object>> onEventConsumers = new ArrayList<>();
        Object eventdata = null;
    }

    protected Map<String, Event> onEventRunnables = new HashMap<>();

    public void emit(String eventName, Object eventData)
    {
        if(onEventRunnables.containsKey(eventName) && onEventRunnables.get(eventName) != null)
        {
            Tools.consoleLog("on a l'event en stock");

            Event event = onEventRunnables.get(eventName);
            event.eventdata = eventData;

            for (Consumer<Object> eventConsumer : event.onEventConsumers)
            {
                eventConsumer.accept(event.eventdata);
            }
        }
    }

    public void on(String eventName, Consumer<Object> consumer)
    {
        if(!onEventRunnables.containsKey(eventName))
        {
            onEventRunnables.put(eventName, new Event());
        }

        onEventRunnables.get(eventName).onEventConsumers.add(consumer);
    }
}
