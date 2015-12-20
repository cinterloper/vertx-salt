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

//   t = new MinionList("balthazarServer")
//   cmd="zfs.list"


//   ri = client.callSync( new LocalCall(cmd, Optional.empty() ,Optional.empty(), returnMap[cmd] ),t)
//
//  println("sync call returned : ${ri}")




/* **************************async************************************
this dosnet work?

     ar = client.callAsync( new LocalCall(cmd, Optional.empty() ,Optional.empty(), returnMap[cmd] ),t)
     String jid = ar.getJid().toString()
     println("jobid: ${jid}")
     ris = client.getJobResult(jid) //something freeks out here, and this blocks hard for some reason... harder then the  sync methods

     caught exception: com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected a string but was BEGIN_ARRAY at line 1 column 78 path $.info[0].Target

     ris.each { ri ->
         ri.getResults().each { minion, ans ->
             println "result: ${minion} : ${ans}"
         }
     }
     ******************async****************************
     */


//println("jid: ${ar.getJid()} mins: ${ar.getMinions()}")
//def jid = job.getJid()
//println("job id: " + jid);

//ResultInfoSet runjob = client.getJobResult(job)

//println("this job: " )
//runjob.each {it -> println(it.getResults())}
//client.events()
/*
jobs.each({k,v ->
    println(k)
    println(jobs.get(k))
})*/
/*} catch(e){
  println("caught exception: ${e.toString()} \n"+e.getStackTrace())
}*/
