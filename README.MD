if you are using this as a library in your vertx enabled program, just read through resources/saltConnection.groovy
write your own version of it, instead of launching this as a verticle/stand alone demo.


everything you send into the salt event system gets prefixed by
https://github.com/saltstack/salt/blob/6a691360808b1f3b7e9a10726b9c4803f8455aa1/salt/netapi/rest_cherrypy/app.py#L2136-L2139

['salt', 'netapi', 'hook' .. ( the rest of your tags here) ]

in salt address format:

salt/netapi/hook/...

so generally you need to setup a salt reactor to catch them, unless you change the base in app.py (cherry)
