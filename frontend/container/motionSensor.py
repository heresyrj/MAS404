import RPi.GPIO as GPIO
import time

import argparse
import base64
import picamera
import json

from googleapiclient import discovery
from oauth2client.client import GoogleCredentials

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(11, GPIO.IN)         #Read output from PIR motion sensor

actions=[]
prev=-1
while True:
       i=GPIO.input(11)
       
       if i==0:                 #When output from motion sensor is LOW
             print(i)
             if prev == 1:
                   actions.append(i)
             prev=0
       elif i==1:               #When output from motion sensor is HIGH
             print (i)
             if prev == 0:
                   actions.append(i)
             prev=1

       actions_str = ''.join(str(v) for v in actions)

       if "101" in actions_str:
             print(actions)
             actions=[]
