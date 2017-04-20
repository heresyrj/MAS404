import time
import takePhoto
import rangeSensor

getPredict = takePhoto.getPredict
getDistance = rangeSensor.distance

actions=[]
prev = 0 # 0 --- door closed   1---door open

def thresholdedRegister(doorIsClosed):
      print(doorIsClosed)
      global prev
      global actions
      if doorIsClosed==False:# door open     #When output from motion sensor is LOW
            if prev == 0:
                  actions.append(1)
            prev=1
      elif doorIsClosed==True:               #When output from motion sensor is HIGH
            if prev == 1:
                  actions.append(0)
            prev=0
      print(actions)

try:
    while True:
      distance = getDistance()
      print(distance)
      thresholdedRegister(distance < 80 and distance > 10) #closed True
      actions_str = ''.join(str(v) for v in actions)
      if "101" in actions_str:
            print(actions)
            actions=[]
            getPredict()
      time.sleep(0.5)

    # Reset by pressing CTRL + C
except KeyboardInterrupt:
    print("\nMeasurement stopped by User")
    GPIO.cleanup()


