package net.iowntheinter.saltReactor.impl

import com.suse.saltstack.netapi.client.SaltStackClient

/**
 * Created by grant on 10/20/15.
 */
import com.suse.saltstack.netapi.event.EventListener
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.eventbus.EventBus
import io.vertx.groovy.core.shareddata.SharedData
import com.suse.saltstack.netapi.datatypes.Event;
import io.vertx.groovy.core.Vertx;
import io.vertx.core.json.JsonObject

import javax.websocket.CloseReason;

class saltReactor implements EventListener {
    private Vertx vx
    private SharedData sd
    private EventBus eb
    private JsonObject config
    private Logger log
    private SaltStackClient sc
    def mgr

    saltReactor(Vertx v, JsonObject c, SaltStackClient s) {
        vx = v;
        sc = s
        sd = v.sharedData()
        config = c
        eb = v.eventBus()
        log = LoggerFactory.getLogger("saltReactor")
        mgr = new SimplePipeSubscriptionManager(sd, eb, sc)
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


