#!/usr/bin/env python

# Imports
from flask import Flask, render_template, url_for, request

# G Vars
app = Flask(__name__)
app.debug = True

# Fns

# Site
#\
@app.route("/")
def index():
    return render_template("index.html")
#\
@app.route("/api", methods=['POST'])
def get_data():
    #\
    #\
    data = request.json# This pulls the data from the POST
    print data # This will show you what it looks like
    #\
    print "Hello there '%s', '%s' is a very nice email." % (data['name'], data['email'])
    return "" # Returns a nothing
    #\
    #\
#\

# main
def main():
    #\
    #\
    app.run('0.0.0.0')
    #\
    #\
if __name__ == "__main__":
    main()