/**
 * Created by grant on 10/20/15.
 */

import com.google.gson.reflect.TypeToken
import com.suse.salt.netapi.AuthModule
import com.suse.salt.netapi.client.SaltClient
import com.suse.salt.netapi.config.ClientConfig
import com.suse.salt.netapi.event.EventListener
import com.suse.salt.netapi.event.EventStream
import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.JsonObject
import io.vertx.groovy.core.Vertx as GVertx
import io.vertx.core.Vertx
import net.iowntheinter.saltReactor.impl.saltReactor

println("simple example in groovy, but you can do this in java," +
        "everything else compiles to a class")


gv = vertx as GVertx
Vertx v = gv.getDelegate() as Vertx

EventBus eb = v.eventBus()
//this should switch to https, but we need to register the ca with this lib to access it
URI uri = URI.create("http://127.0.0.1:8000");
SaltClient client = new SaltClient(uri);
cfg = client.getConfig()
cfg.put(ClientConfig.SOCKET_TIMEOUT , 0)
//auth as a unix user (PAM) on the salt-master system
def UN = "user"
def pass = "changeme"
def token;


token = client.login(UN, pass, AuthModule.PAM);
println("salt auth token: " + token.token + " Until: " + token.expire + " Perms : " + token.getPerms() )

EventListener sr = new saltReactor(v,new JsonObject(), client)

es = new EventStream(cfg)
es.addEventListener(sr)

sr.mgr.manage("saltBridgeChannel")
eb.send("saltBridgeChannel",
        new JsonObject(
                '{"action":"saltPush","addr":"dst/salt/address","data":{"another":"jstruct"}}'))