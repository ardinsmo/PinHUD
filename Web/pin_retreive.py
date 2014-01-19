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

parse_headers = {
	'X-Parse-Application-Id' : '2ICGEDAbu9xpPISmefOo6y4VvEYcw7W0oOF7Lp2T',
	'X-Parse-REST-API-Key' : '6gGagp8hxoPD2FUEEiJrRxqwxbP8jJWJ0aRCJI9y',
	'Content-Type' : 'application/json'
}
def gather_data_from_parse():

	url = 'https://api.parse.com/1/classes/Boards/'
	username_array = []
	boards_array = []
	parseID = []
	request = urllib2.Request(url, data=None, headers=parse_headers)
	results = urllib2.urlopen(request)
	data = results.read()
	information = json.loads(data)
	information = information['results']
	for gather in information:
		username_array.append(gather['username'])
		boards_array.append(gather['company_board'])
		parseID.append(gather['userId'])

	username_board = {
		'usernames' : username_array,
		'boards' : boards_array,
		'userId' : parseID
	}

	print("Gather_data_from_parse")
	print(username_board)
	return(username_board)

#Such cluster fuck
#much wow
def gather_data_from_pinterest(username_board):
	Pinterest.configure_client(config['APP_ID'], config['APP_SECRET'])
	my_client = raw_client(config['APP_ID'], config['APP_SECRET'])
	usernames = username_board['usernames']
	company_board = username_board['boards']
	parseID = username_board['userId']

	how_many = len(parseID)
	count = 0

	while(count <= how_many):
		url = 'https://api.parse.com/1/classes/Pins/'
		pin_count = 0
		number_of_pins_to_return = 250

		#Get the Boards ID so you can get its pins.
		current_parse_id = parseID[count]
		current_user = usernames[count]
		current_board = company_board[count]
		count += count + 1
		#if current_user != "nordstrom":
		#	continue
		#hellish work around to get the board ID
		board = my_client.boards
		builder_user = getattr(board, current_user) # Current_user should be a string
		response = builder_user(current_board).get()
		board_id = response[0]['id']
		
		#Get the pin information that we need
		the_pins = my_client.boards(board_id).pins.get(page_size=number_of_pins_to_return)
		
		#Number_of_pins_to_return = the pins
		while(pin_count < number_of_pins_to_return):
			repin_count = the_pins[0][pin_count]['repin_count']
			description = the_pins[0][pin_count]['description']
			image_large = the_pins[0][pin_count]['image_large_url']
			pin_id = the_pins[0][pin_count]['id']

			data = {
				'Name' : description,
				'Image' : image_large,
				'PinID' : pin_id,
				'Repins': repin_count,
				'forceDisplay' : 'false',
				'userId' : current_parse_id
			}
			#make connection to parse, fuck parse
			connection = httplib.HTTPSConnection('api.parse.com', 443)
			connection.connect()
			connection.request('POST', '/1/classes/Pins', json.dumps({
			"Name": description,
			"Image": image_large,
			"Repins" : repin_count,
	       "forceDisplay" : bool(0),
	       "userId" : current_parse_id,
	       "PinID" : pin_id
	       }), {
	       "X-Parse-Application-Id": "2ICGEDAbu9xpPISmefOo6y4VvEYcw7W0oOF7Lp2T",
	       "X-Parse-REST-API-Key": "6gGagp8hxoPD2FUEEiJrRxqwxbP8jJWJ0aRCJI9y",
	       "Content-Type": "application/json"
	     })
			what_happened = json.loads(connection.getresponse().read())
			print what_happened
			pin_count = pin_count + 1
			
#Make description name, forceDisplay to false
#GET THE userId from parse and get it in the username_board dict.  fuck me.
def main():
	username_board = gather_data_from_parse()
	gather_data_from_pinterest(username_board)

	print(username_board)
	print()


if __name__ == '__main__':
	main()