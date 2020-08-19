package pro.gravit.launcher.events;

import java.util.UUID;
import pro.gravit.launcher.LauncherNetworkAPI;
import pro.gravit.launcher.request.WebSocketEvent;

public abstract class RequestEvent implements WebSocketEvent {
  @LauncherNetworkAPI
  public UUID requestUUID;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\RequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */