if you are using this as a library in your vertx enabled program, just read through resources/saltConnection.groovy
write your own version of it, instead of launching this as a verticle/stand alone demo.


everything you send into the salt event system gets prefixed by
https://github.com/saltstack/salt/blob/develop/salt/netapi/rest_cherrypy/app.py#L2122

['salt', 'netapi', 'hook' .. ( the rest of your tags here) ]


so generally you need to setup a salt reactor to catch them, unless you change the base in app.py (cherry)