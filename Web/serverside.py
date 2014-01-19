import SocketServer
from pinterest.client import raw_client
from pinterest.models.model import Pinterest
from pinterest.models.model import User
import urllib2
import urllib
import json
import httplib

config = { 
	'APP_SECRET' : '66dc7f0be65f23186cd630394d0254ee22eb8e39',
	'APP_ID' : '1435540',	
}

class MyTCPServer(SocketServer.ThreadingTCPServer):
    allow_reuse_address = True

class MyTCPServerHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        try:

            data = json.loads(self.request.recv(1024).strip())
            # process the data, i.e. print it:
            print(data)
            for key, value in data.iteritems():
                #with print_lock:
                print key, value
                print(data)
                    #
                    #pinterest_username
                    #personal_board
                    #company_pinterest_username
                    #company_board
                    #my_client = raw_client(APP_ID, APP_SECRET)
                    #persons_pins = my_client.boards.pinterest_username(personal_board).pins.get(page_size = 10000)
                    #company_pins = my_client.boards.company_pinterest_username(company_board).pins.get(page_size = 100000)


        except Exception, e:
            print "Exception while receiving message: ", e


#StartUp
try:

        server = MyTCPServer(('10.255.148.178', 13375), MyTCPServerHandler)
        server.serve_forever()

except Exception, e:
    print "Exception while forking: ", e