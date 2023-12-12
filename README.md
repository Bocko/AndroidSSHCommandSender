# AndroidSSHCommandSender
Android app that allows you to send an SSH command to a remote host
  
# Features
## SSH
The app uses [JSCH](http://www.jcraft.com/jsch/) to send SSH commands.
## Settings Saving
When sending a command, all the settings will save automaticly so next time you open up the app all the previous settings will be restored. The setting are stored in a [Room DB](https://developer.android.com/jetpack/androidx/releases/room).
## Encryption
When saving the settings the password will be encrypted and when loading the app again it will be decrypted. All the encryption and decryption are handled with the Android [Keystore](https://developer.android.com/privacy-and-security/keystore) and [Cipher](https://developer.android.com/reference/javax/crypto/Cipher)
  
# Screenshots
## Dark Mode
  
<img src="https://github.com/Bocko/AndroidSSHCommandSender/assets/61477246/7de0c44c-e158-40eb-88dc-a7ca4a0dabe5" height="600"> 
<img src="https://github.com/Bocko/AndroidSSHCommandSender/assets/61477246/4943df20-7eb1-4ac5-84d7-c43bf7e2a37b" height="600"> 
  
## Light Mode
  
<img src="https://github.com/Bocko/AndroidSSHCommandSender/assets/61477246/439ec12c-1209-4590-b239-8b14ecab2a13" height="600"> 
<img src="https://github.com/Bocko/AndroidSSHCommandSender/assets/61477246/916594da-6be6-40fd-8947-c90da96fbeed" height="600"> 

# Notice
If you are using the app to shut down a host, be aware that if you start the shutdown, you can't send any other commands after that because SSH doesn't let you connect if the host is shutting down.
