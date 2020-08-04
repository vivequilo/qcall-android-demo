
# QCall Android 📱
Android library for video calls using Qüilo S.A servers. 

__Install QCall Android__

With __gradle__   <img src="https://jitpack.io/docs/favicon.ico" width="20"/>
First add the $TOKEN provided to you by Quilo unde your gradle.properties
<img src="https://i.ibb.co/F0p1tJp/Screen-Shot-2020-07-30-at-11-22-03-AM.png" width="100"/>
```gradle
authToken=$TOKEN
```

Second add to the **project module** build.gradle file the following. (add the token as the credential)
```gradle
maven {  
  url 'https://jitpack.io'
  credentials { username authToken }
}
```
Next add the following to the **app module**  build.gradle file the add following
```gradle
//Under andriod braces
compileOptions {  
  sourceCompatibility JavaVersion.VERSION_1_8  
  targetCompatibility JavaVersion.VERSION_1_8  
}  
  
kotlinOptions {  
  jvmTarget = '1.8'  
}
```

In the same **app module**  build.gradle file add the following in dependencies
```gradle
def qcall_version = '0.0.1' 
dependencies {
	implementation "com.github.vivequilo:qcall-android:$qcall_version"
}
```

## Example building a room
```kotlin
/**
* Must ask for permissions in order to record camera
*/
class MainAcitvity : AppCompatActivity {
    private lateinit var room: Room
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        room = Room
                .Builder(activity, "reploy", "APIKEY", roomId = "1")
                .setMetadata(JsonObject().apply { addProperty("name", "Augusto") })
                .build()
    }
}
```
## Example handle on local stream
```kotlin
/**
* Must ask for permissions in order to record camera
*/
override fun onLocalStream(localPeerId: String, localStream: MediaStream) {  
    super.onLocalStream(localPeerId, localStream)  
	val local_view = findViewById<QVideoRenderer>(R.id.local_view)
    localStream.videoTracks.firstOrNull()?.let {  
		local_view.setVideoTrack(it)  
    }  
}
```
## Required permissions
Required permissions in the AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />  
<uses-permission android:name="android.permission.CAMERA" />  
<uses-permission android:name="android.permission.RECORD_AUDIO"/>  
<uses-permission android:name="android.permission.INTERNET" />  
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

## Mount Service
In the AndroidManifest.xml add under the application tag the following.
```xml
<service android:name="com.vivequilo.qcall_lib.peerKt.services.RoomService"/>
```

##  Video component - QVideoRenderer
> **Tip:**  The moment the video call is closed you should notify the user and for easy use you can make a recyclerview to show the video calls.
### Functions
Name | Parameters | Description
--- | --- | ---
setVideoTrack | `track: VideoTrack` | Sets the VideoTrack to as current track to render on the surface view.

## Client Class
**Properties** 📦
Name | Type | Description
--- | --- | ---
videoTrack | `VideoTrack?` | Getter of the first video track of current stream.
videoTracks | `ArrayList<VideoTrack?>` | Getter of the list of all current audio tracks.
audioTrack | `AudioTrack?` | Getter of the first audio track of current stream.
audioTracks | `ArrayList<AudioTrack?>` | Getter of the list of all current video tracks.

**Functions** 👾
Name | Parameters | Description | Returns
--- | --- | --- | ---
isNotSame | `id: String` | Checks if the client is the same with another client | Boolean
isNotSame | `client: Client` | Checks if the client is the same with another client | Boolean
closeCall | | Closes the specific call with this client abruptly. | Unit

## Room Class
> **Tip:**  If you want to reduce the quality go check the VideoCapturerConstraint.
> To set it you must use **setVideoCaptureConstraint** in the builder.

**Properties** 📦
Name | Type | Description
--- | --- | ---
roomObserver | `RoomObserver` | Observer for the room events
audioDevices | `MutableSet<PeerAudioManager.AudioDevice>` | Current audio devices
currentDevice | `PeerAudioManager.AudioDevice` | Current audio device
isMuted | `Boolean` | Getter if the call is muted
isHidden | `Boolean` | Getter if the video is hidden
localClient | `Client` | Local Client instance class.
clients | `ArrayList<Client>` | Getter if the video is hidden
stream | `MediaStream` | The local media stream.
clientIds | `ArrayList<Int>` | The remote ids of the callers.


**Functions** 👾
Name | Parameters | Description | Returns
--- | --- | --- | ---
startVideoCapture | `localView: QVideoRenderer` | Starts to capture of the local stream and sets the current stream property. Also renders the local stream on the localView | Unit
startVideoCapture |  | Starts to capture of the local stream and sets the current stream property. | Unit
connect | `onSuccess: ((List<Client>) -> Unit) onError: (Any) -> Unit`| Connects the user to the desired room| Unit
close |  | Closes the room and the connections gracefully. | Unit
switchCamera |  | Toggles between front camera and back camera. | Unit
setSpeaker | `boolean: Boolean` | Sets if the speaker should be turned on or off in the phone as audio output. | Unit
toggleSpeaker |  | Toggles if the output of the android device should be speaker or earphone. | Unit
toggleMute |  | Toggles the mic it muted and enabled to the other users. | Unit
setIsMute | `boolean: Boolean` | Sets with desired value if the audioTrack should be muted or not to the others. | Unit
toggleCamera |  | Toggles the camera it visible and hidden to the other users. | Unit
setIsHidden | `boolean: Boolean` | Sets with desired value if the videoTrack should be visible or not to the others. | Unit
initView | `view: QVideoRenderer, mirror: Boolean` | Inits the view in the desired | Nothing

#### Companion Object Values

 - [x] `CAMERA_PERMISSION = Manifest.permission.CAMERA`
 - [x] `AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO`
 - [x] `ALL_PERMISSIONS_GRANTED = 999`
 - [x] `REQUIRED_PERMISSIONS = arrayOf(CAMERA_PERMISSION, AUDIO_PERMISSION)`

### Builder 🛠
Data class to build a Room Class instance.

**Constructor** 🔨
Name | Type | Description
--- | --- | ---
activity | `Activity` | The current activity of the application.
deploy | `String` | The deploy parameter where to build the room.
key | `String`| The api key provided by Qüilo S.A to connect the server.

**Functions** 👾
Name | Parameters | Description
--- | --- | ---
setPeerId | `id: String` | Sets the peerId to the room.
setEglBase | `eglBase: EglBase` | Sets the rootEglBase to the room.
setMetadata | `meta: JsonObject` | Sets the metadata to the room.
setRoomObserver | `observer: RoomObserver` | Sets the roomObserver to the room.
setMaxFrameRate | `rate: Int` | Sets the frameRateConstraints.max to the room
setMinFrameRate | `rate: Int` | Sets the frameRateConstraints.min to the room
setIdealFrameRate | `rate: Int` | Sets the frameRateConstraints.ideal to the room
setVideoCaptureConstraint | `constraint: VideoCapturerConstraint` | Sets the videoCapturerConstraint to the room. This defines the video quality of the capturer. By default it gets the current screen size and sets its resolution by 60% of its size
setDataConnectionListener | `listener: DataConnectionListener` | Sets the dataConnectionListener to the room. This listener handles the connection events.
build | | Returns the Room instance

## QVideoRenderer

**Functions** 👾
Name | Parameters | Description
--- | --- | ---
setVideoTrack | `track: QVideoTrack?` | Sets the video track
clearImage |  | Clears the image of the surface view
init | `base: QEglBase` | Sets the metadata to the room.
setEnableHardwareScaler | | Makes the renderer faster. Might be buggy in older versions
setMirror | `mirror: Boolean` | Sets if the output should be mirrored.
removeCurrentTrack | | Removes the current track in the surface and cleans image
pauseVideo | | Pauses the video.
setScalingType | `scalingType: ScalingType?` | Sets the scaling type of the video in the surface.

## Observers 🔬

### RoomObserver 

Class that observes the room changes.

**Functions** 👾
Name | Parameters | Description | Returns
--- | --- | --- | ---
onLocalStream | `localPeerId: String, localStream: MediaStream` | Callback when the local stream is set. | Unit
onStreamAdded | `caller: Client, remoteStream: MediaStream` | Callback when a remote stream is added to the room. | Unit
onStreamRemoved | `remotePeerId: String` | Callback when a remote stream is removed to the room. | Unit
onClientRemoved | `remotePeerId: String` | Callback when a remote client is removed to the room. | Unit
onConnectionEstablished | | Callback when the local client connects to the server (Not the call itself necessarily). | Unit
onStreamDenied | | Callback when a stream is denied. | Unit


## Listeners 🔊

### DataConnectionListener 

**Functions** 👾
Name | Parameters | Description | Returns
--- | --- | --- | ---
onConnectionOpen | | Callback when the data connection has opened. | Unit
onConnectionClosed | | Callback when the data connection has closed. | Unit
onDataString | `string: String, remotePeerId:String` | Callback when the data connection has received a string. | Unit
onDataJson | `jsonObject: JsonObject, remotePeerId:String` | Callback when the data connection has received a json. | Unit
onMessageFailed | `e: ArrayList<Exception>` | Callback when the data connection could not parse the answer. Should not happen. | Unit



