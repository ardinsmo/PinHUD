#!/usr/bin/env python

# Imports
from flask import Flask, render_template, url_for, request, jsonify
from pinterest.client import raw_client
from pinterest.models.model import Pinterest
from pinterest.models.model import User
import urllib2
import urllib
import json

# G Vars
app = Flask(__name__)
app.debug = True

config = { 
    'APP_SECRET' : '66dc7f0be65f23186cd630394d0254ee22eb8e39',
    'APP_ID' : '1435540',   
}

# Fns
#\
def test():
    #\
    #\
    pass
    #\
    #\
#\

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
    print request
    #\
    data = request.json# This pulls the data from the POST
    print data # This will show you what it looks like
    #\
    print "You said '%s', '%s', '%s', '%s' " % (data['user_pinterest_name'], data['user_board_name'], data['company_pinterest_name'], data['company_board_name']) # Returns a nothing
    repin_count_array = []
    description_array = []
    image_large_array = []

    #Get the pinterest information
    Pinterest.configure_client(config['APP_ID'], ['APP_SECRET'])
    my_client = raw_client(config['APP_ID'], config['APP_SECRET'])
    username = data['user_pinterest_name']
    company_username = data['company_pinterest_name']
    user_board = data['user_board_name']
    company_board = data['company_board_name']

    #Testing prints

    number_of_pins_to_return = 250
    pin_count = 0
    pin_count2 = 0

    #The hellish work around for making the User() for the client/company names
    the_user_board = my_client.boards
    builder_user = getattr(the_user_board, username)
    the_user_board = builder_user(user_board).get()
    user_board_id = the_user_board[0]['id']


    #User pins
    the_user_pins = my_client.boards(user_board_id).pins.get(page_size=number_of_pins_to_return)
    #print the_user_pins
    x = len(the_user_pins)
    print("LENGTH" + str(x))

    #Hellish work around for making the company User()
    the_company_board = my_client.boards
    builder_company = getattr(the_company_board, company_username)
    the_company_board = builder_company(company_board).get()
    company_board_id = the_company_board[0]['id']

    #Company pins
    the_company_pins = my_client.boards(company_board_id).pins.get(page_size=number_of_pins_to_return)
    #print the_company_pins
    print
    print

    while(pin_count < len(the_user_pins[0])):
        try:
<<<<<<< HEAD
=======
            # print pin_count
>>>>>>> 4662fc8957364fa2dc0521d3732d7c28b22837eb
            repin_count = the_user_pins[0][pin_count]['repin_count']
            description = the_user_pins[0][pin_count]['description']
            image_large = the_user_pins[0][pin_count]['image_large_url']
            pin_count2 = 0
            while(pin_count2 < len(the_company_pins[0])):
<<<<<<< HEAD
                image_large_comp = the_company_pins[0][pin_count2]['image_large_url']
                if(image_large == image_large_comp):
=======
                # print "sup" + str(pin_count2)
                image_large_comp = the_company_pins[0][pin_count2]['image_large_url']
                #print(image_large + " | " + image_large_comp)
                if(image_large == image_large_comp):
                    #print "match found"
>>>>>>> 4662fc8957364fa2dc0521d3732d7c28b22837eb
                    repin_count_array.append(repin_count)
                    description_array.append(description)
                    image_large_array.append(image_large)
                pin_count2 = pin_count2 + 1
<<<<<<< HEAD
=======
            #pin_count = pin_count + 1
>>>>>>> 4662fc8957364fa2dc0521d3732d7c28b22837eb
        except IndexError:
            print "YOLO"
        pin_count = pin_count + 1

    super_data = {
        'descriptions' : description_array,
        'image_large_urls' : image_large_array,
        'repins' : repin_count_array
    }
    
    return jsonify(results = super_data)
    #\
    #\
#\

# main
def main():
    #\
    #\
    app.run("0.0.0.0")
    #\
    #\
if __name__ == "__main__":
    main()