package net.iowntheinter.saltReactor.impl

import com.suse.salt.netapi.client.SaltClient
import com.suse.salt.netapi.datatypes.Event
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.groovy.core.eventbus.EventBus
import net.iowntheinter.saltReactor.SVXSubscriptionManager

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Created by grant on 11/5/15.
 */
class SimplePipeSubscriptionManager implements SVXSubscriptionManager {
    private EventBus eb
    private Logger log
    private subscriptionChannel
    private SaltClient saltClient

    SimplePipeSubscriptionManager(EventBus e, SaltClient c) {
        eb = e
        log = LoggerFactory.getLogger("saltReactor:subscriptionManager")
        saltClient = c
    }
    /*examples:
    //perhaps subscribe to sub-tag based address truncated at 2nd level, containing the full tag in the msg
      ex salt/cloud (all)
    got event: salt/cloud/edge-gamma/requesting
    got event: salt/job/20151028033249814239/ret/saucer
    got event: salt/key
        {"_stamp":"2015-10-28T03:13:36.135827","act":"reject","id":"unallocated.barefruit.co.uk","result":true}
     */

    @Override
    public boolean process(Event e) {
        def data = e.getData()
        def tag = e.getTag()
        log.info("got event ${tag}")
        String type
        String ident
        String verb
        def result
        def dstAddr
        /*
        anything sent by vertx into salt will also get picked up here as:
        salt/netapi/hook/$ADDR
        perhaps, when sending messages into the salt bus, we should add a vertx source id
        then silence messages from ourselves, or possiably everything at this address
         */

        List fields = tag.tokenize('/')
        if (fields.last() == fields.first()) {
            dstAddr = fields.first();
        } else { //addressed to salt/job or salt/cloud, simple
            dstAddr = fields[0] + '/' + fields[1]
        }
        if (fields[0] != "salt") {
            type = fields[0]
            log.debug("data for this oddball: ${data}")
        }
        //do the vulcan mind meld
        sendToVertxBus(dstAddr, ['tags': fields, 'data': data], { res ->
            log.info('result of vxbus send for ' + fields + " : " + res)
            return (true)
        })
        log.info("event: ${["type": type, "data": data, "ident": ident, "verb": verb]}")


    }


    private boolean sendToVertxBus(channel, pkg, cb) {
        def ret = true
        try {
            eb.publish(channel, pkg)
        } catch (e) {
            ret = false
            cb([status:ret, error:e.getMessage()])
        }
        if (ret)
            cb([status:ret, error:null])
    }

    private boolean sendToSaltBus(tag, data, cb) {
        Future tsk = new CompletableFuture()
        def ret = true
        try {
            tsk=saltClient.sendEventAsync(tag as String, data as String)
        } catch (e) {
            ret = false
            cb([status:ret, error:e.getMessage()])
        }
        if (ret)
            cb([status:ret, error:null,future:tsk ])
    }

    @Override
    public boolean manage(String vxchannel) {
        def subscriptionChannel = eb.consumer(vxchannel)
        subscriptionChannel.handler({ message ->
            JsonObject jreq = new JsonObject()
            try {
                jreq = message.body() as JsonObject
            } catch (e) {
                log.error("error ${e}")
            }
            processEBReq(jreq, { response ->
                log.info('processed ${response}')
            })
            println("I have received a message: ${message.body()}")
        }).completionHandler({ res ->
            if (res.succeeded()) {
                log.info("The saltEventManger eb registration has reached all nodes")
            } else {
                log.error("saltEventManger eb Registration failed!")
            }
        })
    }

/*
   {
     "action":"saltPush",
     "addr":"dst/salt/address",
     "data": { "another":"jstruct" }
    }
 */
    private boolean processEBReq(JsonObject req, cb) {
        def type = req.getString("action")
        def response
        switch (type) {
            case 'list':
                break;
            case 'saltPush': //perhaps should block loops of this message
                def addr = req.getString("addr")
                def data = req.getJsonObject("data")
                sendToSaltBus(addr, data, { res ->
                    log.info("result of sending to salt ${addr} : ${res} ")
                })
                break;
        }


    }
}