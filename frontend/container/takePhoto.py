import argparse
import base64
import picamera
import json
from datetime import datetime,tzinfo,timedelta

#for vision API
from googleapiclient import discovery
from oauth2client.client import GoogleCredentials
#for firebase
from firebase import firebase
firebase = firebase.FirebaseApplication('https://mas404-7d518.firebaseio.com/', None)

""" """""""""""""""""""""""""""""""""""""""""""""""" For Timestamp """""""""""""""""""""""""""""""""""""""""""""
class Zone(tzinfo):
    def __init__(self,offset,isdst,name):
        self.offset = offset
        self.isdst = isdst
        self.name = name
    def utcoffset(self, dt):
        return timedelta(hours=self.offset) + self.dst(dt)
    def dst(self, dt):
            return timedelta(hours=1) if self.isdst else timedelta(0)
    def tzname(self,dt):
         return self.name

GMT = Zone(0,False,'GMT')
EST = Zone(-4,False,'EST')
""" """""""""""""""""""""""""""""""""""""""""""""""" For Timestamp """""""""""""""""""""""""""""""""""""""""""""

def prettyPrint(_dict):
    print(json.dumps(_dict, indent=4, sort_keys=True))  #Print it out and make it somewhat pretty.

def takephoto():
    camera = picamera.PiCamera()
    camera.capture('image.jpg')
    camera.close()

mannualFilter = ['apple','banana', 'watermelon', 'eggplant', 'carrot', 'broccoli'] #change here
vegies = ['Eggplant', 'Carrot', 'Broccoli']

def filterResult(response): # response from Google CV
    # prettyPrint(response['responses'])
    items = response['responses'][0]['labelAnnotations']
    filtered = [ item['description'].capitalize() for item in items if item['description'] in mannualFilter and item['score'] >= 0.4]
    return filtered

def updateFirebase(newList):
    global vegies
    now = datetime.now(EST)
    timestamp = str(now)[:19]

    inventory = firebase.get('/inventory', None)
    oldList = list(inventory.keys())
    # print(inventory)
    prettyPrint(oldList)
    prettyPrint(newList)

    # oldList is all items in box
    # newList is all newly recognized items

    # for each new item recognized
    # if it doesn't in inventory, add it with putinDate and putoutDate
    # if it exist in inventory, check
    # 		if putoutDate is No, set putOutDate
    #       else renew putinDate and set putoutDate to No

    # for newItem in newList:
    # 	if newItem in oldList:
    # 		key = '/inventory/' + newItem
    # 		info = firebase.get(key, None)
    # 		if(info['putinDate'] !=== 'No'):
    # 			value = {'putinDate' : timestamp, 'putoutDate' : 'No', 'count': 1}
    #         res = firebase.patch(key, data=value, params={'print': 'pretty'})
            # print(res)

    for oldItem in oldList:
        if oldItem not in (newList + vegies):
            key = '/inventory/' + oldItem + '/'
            print(key)
            value = {'putoutDate' : timestamp, 'count': 1}
            res = firebase.patch(key, data=value, params={'print': 'pretty'})
            print(res)

    for newItem in newList:
        if newItem not in oldList:
            key = '/inventory/' + newItem + '/'
            value = { 'putinDate' : timestamp, 'putoutDate' : 'No', 'count': 1}
            res = firebase.patch(key, data=value, params={'print': 'pretty'})
            print(res)
        else:
        	key = '/inventory/' + newItem
        	info = firebase.get(key, None)
        	if(info['putinDate'] != 'No'):
        		value = {'putinDate' : timestamp, 'putoutDate' : 'No', 'count': 1}
        		res = firebase.patch(key, data=value, params={'print': 'pretty'})

def getPredict():
    takephoto() # First take a picture
    """Run a label request on a single image"""

    credentials = GoogleCredentials.get_application_default()
    service = discovery.build('vision', 'v1', credentials=credentials)

    with open('image.jpg', 'rb') as image:
        image_content = base64.b64encode(image.read())
        service_request = service.images().annotate(body={
            'requests': [{
                'image': {
                    'content': image_content.decode('UTF-8')
                },
                'features': [{
                    'type': 'LABEL_DETECTION',
                    'maxResults': 20
                }]
            }]
        })
        response = service_request.execute()
        
    filtered = filterResult(response)
    updateFirebase(filtered)

if __name__ == '__main__':
    getPredict()