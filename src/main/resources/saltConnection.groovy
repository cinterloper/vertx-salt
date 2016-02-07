/**
 * Created by grant on 10/20/15.
 */
import com.google.gson.reflect.TypeToken
import com.suse.saltstack.netapi.calls.LocalCall
import com.suse.saltstack.netapi.config.ClientConfig
import com.suse.saltstack.netapi.event.EventListener
import com.suse.saltstack.netapi.event.EventStream
import com.suse.saltstack.netapi.client.SaltStackClient
import com.suse.saltstack.netapi.AuthModule
import com.suse.saltstack.netapi.datatypes.target.MinionList
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import net.iowntheinter.saltReactor.impl.saltReactor

println("hello slt")
//this should switch to https, but we need to register the ca with this lib to access it
URI uri = URI.create("http://127.0.0.1:8000");
SaltStackClient client = new SaltStackClient(uri);
cfg = client.getConfig()
cfg.put(ClientConfig.SOCKET_TIMEOUT , 0)
//auth as a unix user (PAM) on the salt-master system
def UN = "user"
def pass = "changeme"
def token;

def returnMap = [
        "zfs.list":new TypeToken<Map<String,List>>(){},
        "test.ping":new TypeToken<Boolean>(){},
        "cmd.run":new TypeToken<String>(){}
]

token = client.login(UN, pass, AuthModule.PAM);
println("salt auth token: " + token.token + " Until: " + token.expire + " Perms : " + token.getPerms() )

EventListener sr = new saltReactor(vertx,new JsonObject(), client)

es = new EventStream(cfg)
es.addEventListener(sr)
