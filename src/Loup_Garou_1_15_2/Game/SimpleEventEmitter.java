package Loup_Garou_1_15_2.Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleEventEmitter
{
    protected Map<String, List<Runnable>> onEventRunnables = new HashMap<>();

    protected void emit(String eventName)
    {
        if(onEventRunnables.containsKey(eventName) && onEventRunnables.get(eventName) != null)
        {
            for (Runnable event : onEventRunnables.get(eventName))
            {
                event.run();
            }
        }
    }

    public void on(String eventName, Runnable runnable)
    {
        if(!onEventRunnables.containsKey(eventName))
        {
            onEventRunnables.put(eventName, new ArrayList<Runnable>());
        }

        onEventRunnables.get(eventName).add(runnable);
    }
}
