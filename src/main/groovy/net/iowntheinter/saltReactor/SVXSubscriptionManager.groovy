package net.iowntheinter.saltReactor

import com.suse.saltstack.netapi.datatypes.Event;

/**
 * Created by grant on 11/5/15.
 */
interface SVXSubscriptionManager {
    boolean process(Event e)
    boolean manage(String channel)

}
