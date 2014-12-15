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

Architecture
------------



