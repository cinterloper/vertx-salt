package net.iowntheinter.saltReactor.impl

import com.suse.salt.netapi.client.SaltClient

/**
 * Created by grant on 10/20/15.
 */
import com.suse.salt.netapi.event.EventListener
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.eventbus.EventBus
import com.suse.salt.netapi.datatypes.Event;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject
import net.iowntheinter.saltReactor.SVXSubscriptionManager

import javax.websocket.CloseReason;

class saltReactor implements EventListener {
    private Vertx vx
    private EventBus eb
    private JsonObject config
    private Logger log
    private SaltClient sc
    SVXSubscriptionManager mgr

    saltReactor(Vertx v, JsonObject c, SaltClient s) {
        vx = v;
        sc = s
        config = c
        eb = v.eventBus()
        log = LoggerFactory.getLogger("saltReactor")
        mgr = new SimplePipeSubscriptionManager( eb, sc)
    }
    CloseReason closeReason;


    @Override
    void notify(Event event) {

        try{
            mgr.process(event)
        } catch(e){
            log.error("there was an error processing the salt event: " +
                    "${event.getTag()} : err : ${e.getStackTrace()}")
        }
    }

    /**
     * Notify the listener that the backing event stream was closed.  Listener may
     * need to recreate the event stream or take other actions.
     * @param closeReason the close reason
     */
    @Override
    void eventStreamClosed(CloseReason closeReason) {
        println("saltReactor stream closed: ${closeReason}")
    }


}


