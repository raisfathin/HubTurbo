package util.events;

import com.google.common.eventbus.Subscribe;

@FunctionalInterface
public interface UpdateProgressEventHandler extends EventHandler {
    @Subscribe
    void handle(UpdateProgressEvent e);
}
