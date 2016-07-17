package net.iowntheinter.saltReactor.impl

import com.suse.salt.netapi.client.SaltClient
import com.suse.salt.netapi.datatypes.Event
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.eventbus.EventBus
import net.iowntheinter.saltReactor.SVXSubscriptionManager
import io.vertx.core.eventbus.MessageConsumer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * Created by grant on 11/5/15.
 */
class subscriptionManager implements SVXSubscriptionManager {
    private EventBus eb
    private Logger log
    private subscriptionChannel
    private SaltClient saltClient
    private HashMap reflectors = new HashMap()

    subscriptionManager(EventBus e, SaltClient c) {
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
        def dstAddr
        /*
        anything sent by vertx into salt will also get picked up here as:
        salt/netapi/hook/$ADDR
        perhaps, when sending messages into the salt bus, we should add a vertx source id
        then silence messages from ourselves, or possiably everything at this address
         */

        List fields = tag.tokenize('/')
        if ((fields.last() == fields.first()) && fields.size() == 2) {
            dstAddr = fields.first();
        } else { //addressed to salt/job or salt/cloud, simple
            dstAddr = fields[0] + '/' + fields[1]
        }
        if (fields[0] != "salt") {
            type = fields[0]
            log.debug("recieved a message that does not begin with 'salt':\n${tag} :\n ${data}")
        }
        //do the vulcan mind meld
        sendToVertxBus(dstAddr, new JsonObject(['tags': fields, 'data': data]), { res ->
            log.info('result of vxbus send for ' + fields + " : " + res)
            return (true)
        })
        log.info("event: ${["type": type, "data": data, "ident": ident, "verb": verb]}")


    }

    private boolean removeReflector(String RID, cb) {
        try {
            MessageConsumer r = reflectors[RID] as MessageConsumer
            r.unregister()
        }
        catch (e) {
            log.error("error ${e}")
        }
        cb()
    }

    private boolean createReflector(String vxchannel, String saltaddr, cb) {
        def ret = true
        MessageConsumer subscriptionChannel = eb.consumer(vxchannel)
        subscriptionChannel.handler({ message ->
            String jreq = ""
            try {
                jreq = message.body().toString()
                sendToSaltBus(saltaddr, jreq, cb)
            } catch (e) {
                log.error("error ${e}")
            }
        })
        //store a refrence to the channel so it can be removed, return the id
        def RID = UUID.randomUUID().toString()
        reflectors[RID] = subscriptionChannel
        cb(RID)
    }

    private boolean sendToVertxBus(channel, JsonObject pkg, cb) {
        def ret = true
        try {
            eb.publish(channel, pkg)
        } catch (e) {
            ret = false
            cb([status: ret, error: e.getMessage()])
        }
        if (ret)
            cb([status: ret, error: null])
    }

    private boolean sendToSaltBus(String tag, String data, cb) {
        Future tsk = new CompletableFuture()
        def ret = true
        try {
            tsk = saltClient.sendEventAsync(tag as String, data as String)
        } catch (e) {
            ret = false
            cb([status: ret, error: e.getMessage()])
        }
        if (ret)
            cb([status: ret, error: null, future: tsk])
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
                message.reply(response)
            })
            log.info("I have received a message: ${message.body()}")
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
                cb(reflectors.keySet())
                break;
            case 'saltPush': //perhaps should block loops of this message
                String addr = req.getString("addr")
                String data = req.getJsonObject("data")
                sendToSaltBus(addr, data, { res ->
                    log.info("result of sending to salt ${addr} : ${res} ")
                    cb(res)
                })

                break;
            case 'reflect': //reflect all messages from an arbitrary vertx channel into the salt system
                String channel = req.getString("vertx_channel")
                String addr = req.getString("salt_address")
                createReflector(channel, addr, { String RID ->
                    cb(RID)
                })
                break;
            case 'unreflect': //remove a reflector
                String id = req.getString("reflector_id")
                removeReflector(id, cb)
                break

        }
    }
}