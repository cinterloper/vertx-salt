grant@nighthawk ~/sandbox/vertx/vertx-salt % java -jar build/libs/vertx-salt-3.2.0-fat.jar

Bad level value for property: io.netty.util.internal.PlatformDependent.level
hello slt
Invalid cookie header: "Set-Cookie: session_id=33d44b90db2b7c8ed7dc34fb5d953bfade19706f; expires=Mon, 21 Dec 2015 04:47:40 GMT; Path=/". Invalid 'expires' attribute: Mon, 21 Dec 2015 04:47:40 GMT

salt auth token: 33d44b90db2b7c8ed7dc34fb5d953bfade19706f Until: Mon Dec 21 06:47:41 UTC 2015 Perms : [.*, @runner, @wheel, @jobs]

Succeeded in deploying verticle

got event minion_start
result of vxbus send for [minion_start] : [status:true, error:null]
event: [type:minion_start, data:[_stamp:2015-12-20T18:47:41.985664, pretag:null, cmd:_minion_event, tag:minion_start, data:Minion master.diablo started at Sun Dec 20 18:47:41 2015, id:master.diablo], ident:null, verb:null]

got event salt/job/20151220184742090957/ret/edge-gamma
result of vxbus send for [salt, job, 20151220184742090957, ret, edge-gamma] : [status:true, error:null]
event: [type:null, data:[tgt_type:glob, jid:20151220184742090957, return:true, tgt:edge-gamma, schedule:__mine_interval, cmd:_return, pid:10576.0, _stamp:2015-12-20T18:47:42.092851, arg:[], fun:mine.update, id:edge-gamma], ident:null, verb:null]

got event salt/minion/master.diablo/start
result of vxbus send for [salt, minion, master.diablo, start] : [status:true, error:null]
event: [type:null, data:[_stamp:2015-12-20T18:47:42.490306, pretag:null, cmd:_minion_event, tag:salt/minion/master.diablo/start, data:Minion master.diablo started at Sun Dec 20 18:47:42 2015, id:master.diablo], ident:null, verb:null]

got event salt/job/20151220184744518087/ret/master.diablo
result of vxbus send for [salt, job, 20151220184744518087, ret, master.diablo] : [status:true, error:null]
event: [type:null, data:[tgt_type:glob, jid:20151220184744518087, return:true, tgt:master.diablo, schedule:__mine_interval, cmd:_return, pid:637.0, _stamp:2015-12-20T18:47:44.521059, arg:[], fun:mine.update, id:master.diablo], ident:null, verb:null]
