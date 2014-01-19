import SocketServer
import json
import urllib2
import os
import sys
from threading import Lock
import Queue
from urllib2 import urlopen

#define
print_lock = Lock()
printer_lock = Lock()
dLock = Lock()

orderNumber = 0
stageLookUp = {'1':"Downloading",'2':"Waiting in Queue",'3':"Setting Up",'4':"Printing",'5':"Reseting",'6':"Wait on Clear",'7':"Done!"}
progLock = Lock()
progressDic = dict()

class MyTCPServer(SocketServer.ThreadingTCPServer):
    allow_reuse_address = True

class MyTCPServerHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        try:

            data = json.loads(self.request.recv(1024).strip())
            # process the data, i.e. print it:

            for key, value in data.iteritems():
                with print_lock:
                    print key, value

                if key == 'order':
                    SubmitOrder(value,self)
                elif key == 'status':
                    getStatus(value,self)
                else:
                    with print_lock:
                        print "no order"


        except Exception, e:
            print "Exception while receiving message: ", e

def SubmitOrder(orderID,self):
    global orderNumber
    global progressDic
    #find download
    
    try:
        with print_lock:
            print "Search Order:",orderID
        
        url = "http://www.thingiverse.com/download:"
        url+=str(orderID)
        code = urlopen(url).code
        if (code / 100 < 4):
            #downloadQueue.put(str(url))
            with print_lock:
                print "added: ",url
            self.request.sendall(json.dumps({'receipt':str(orderNumber)}))
            #self.request.sendall("Hey")8
            with progLock:
                progressDic[str(orderNumber)] = 1
            orderNumber += 1

            if(os.fork() == 0):
                downloadQueue(url,orderNumber)
                sys.exit(0)
        else:
            with print_lock:
                print "Failed to connect!"
            self.request.sendall(json.dumps({'receipt':"-1"}))


    except Exception, e:
        print "Exception while fetching file: ", e
    #print json.dumps({'receipt':str(orderNumber)})
    
    


def getStatus(receipt,self):

    with print_lock:
        print "Get Status"
        self.request.sendall(json.dumps({"stage":progressDic[str(receipt)]}))
        


def downloadQueue(url,receipt):

    with dLock:
            # Open the url
        try:
            f = urlopen(url)
            with print_lock:
                print "downloading " + url
            # Open our local file for writing
            path = os.path.basename(url+".stl")
            with open(path, "wb") as local_file:
                local_file.write(f.read())
        #handle errors
        except HTTPError, e:
            print "HTTP Error:", e.code, url
        except URLError, e:
            print "URL Error:", e.reason, url
    with progLock:
        progressDic[str(receipt)] = 2
    with print_lock:
        print "Waiting on printer.."
    setupPrinter(path,receipt)

def setupPrinter(path,receipt):
    
    
    with printer_lock:
        with print_lock:
            print "Seting up Printer"
        with progLock:
            progressDic[str(receipt)] = 3
        #home printer
        #load file
        #printer warms up
        with print_lock:
            print "Printing!"
        with progLock:
            progressDic[str(receipt)] = 4
        #start printing
        #done printing 
        with print_lock:
            print "Reseting"
        with progLock:
            progressDic[str(receipt)] = 5
        #prompt to clear
        with progLock:
            progressDic[str(receipt)] = 6
        with print_lock:
            print "Hit enter when the printer is clear"
            variable = raw_input('\n')
        with progLock:
            progressDic[str(receipt)] = 7
            


    


    



#StartUp
try:

        server = MyTCPServer(('0.0.0.0', 13375), MyTCPServerHandler)
        server.serve_forever()

except Exception, e:
    print "Exception while forking: ", e