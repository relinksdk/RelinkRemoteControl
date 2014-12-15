Relink remote control Specification
===================================

version: 0.1

status: draft


Concept
-------

The Relink Remote Control SDK allow one device (mobile phone, tablet or pc) to send messages or commands to another device (tablet based Relink device).

Some utilisation examples would be:
- Modify the remote device configuration
- Send some application messages (SMS, text, alarms, video connexion requests, etc.)

Constraints
-----------
The communication should be:

  *nearly real-time*: When a message is sent to the remote device, the time it takes to be taken into account should be less than 10s. On the user side, you should be able to interact with the device while having someone other the phone and seeing the result "live"
  
  *low bandwidth*: Some devices might work with a very limited mobile based internet connexion. Therefore, some regular polling mechanism is not an option.
  
  *easy to implement*: It should require as little knowledge as possible to implement this SDK by relying on mecanism already known by the android community. Without being a strict requirement, this version of the specification reckon the library should send an [intent](https://developer.android.com/reference/android/content/Intent.html "Intent").
  
  *secure*: The remote device can be used by people who do not understand it is possible for a device to be hacked and can take for granted any message received. We should ensure, at least, the common good practices should be implemented.

  *1-to-many*: From a single device, we should be able to send some messages to many remote ones. A use case would be a nursing home which would send a "Time to eat" message to all its residents.

Security
------------

- GCM registration ID should not be used directly. The local device will use a custom registration ID created from it registration ID, the remote device registration ID hashed with a salt. This way, another device knowing the custom registration ID won't be able to communicate with the remote device as the GCM server will notice the local registration ID doesn't match.

- The security is implemented both in the GCM server (filtering defined before) and in the remote device. The local device use its own private key to sign the message. The remote device will check with the public key provided during the binding. It will ensure no one usurpt the identity of the sender.


Binding
-------

A local device should be binded to a remote device to be able to send messages. The 1st binding should be done while the 2 devices is close physically (when you set the remote device up). We can imagine to use the bluetooth as a communication between both devices.

When connected, the local device is prompted to provide a 4 digits PIN code. The PIN code is available in the remote device app. This way, someone being near the device which could not manipulate it won't be able to bind the device.
If the PIN code is right, the device get binded and the custom registration code is sent to the GCM server (using HTTPS) and stored on the local device. The remote device receive the public key of the local device and store it.






