# --------------------------------------------------------------------------------------------
#  AIDL Interfaces
# --------------------------------------------------------------------------------------------

- AIDL stand for Android Interface Definition Language.
- It allow us to make communication between different processes (apps).
- When we make communication using only Intent: we can only send receive actions between multi apps.
- Using AIDL, we can create full communication, like the processes is in the same app.
- Which means, we can send/receive objects like string, int, instances, etc.

## AIDL allow to communicate between two different processes:
-AIDL allow to create interface to make communication between different processes (apps).
In android, an application cannot access a process of another application directly, 
So that we use AIDL to generate interface that let us make communication between two different processes.

## Why Aidl? Marshalling & Demarcating
- Suppose we have two applications: one build with c++ and another build with java.
- When we make communication between the both apps, we may want to pass objects like string, int, etc.
- The problem is: the string in c++ is not the string in cpp, each lang has its own data types.
- So the android framework take care of converting the objects to primitive types than can be understand by android.
* Marshalling: Converting objects of app to primitive type than can be understand by android.
* Demarcating: Convert android primitive types to objects of destination app.
- Example: Pass string object from cpp app to java app:
- [cpp string]  => Marshalling => [Android Primitive type] => Demarcating [java string]
=> When we use AIDL interfaces, android automatically take care of this mechanism.

## Example:
- In android, we have Gallery app and Camera app.
- When we take a photo, we can click on the photos icon to get all the taken photos.
- So camera app (client) communicate to gallery app (server) to retrieve the photos and show them.

## AIDL Uses Binder
- AIDL uses Binder to bind to a service of another process (app), like we bind to BoundServices.
- When we bind to Aidl Interface of another app, we are ready to access it.


In an AIDL file, an interface can be defined with the method signatures of the remote service. The AIDL parser generates a Java class from the interface, that can be used for two different purposes.
- It generates a Proxy class to give the client access to the service,
- It generates a abstract Stub class, that can be used by the service implementation to extend it to an anonymous class with the implementation of the remote methods.
When the AIDL android project is compiled, then java class ISampleService.java shall be generated for ISampleService.aidl file.

It will have abstract Stub class and a Proxy class.

The remote service has to create an Stub class object, and the same has to be returned to the client when the client calls bindService().

The onBind() of remote service shall return an Stub class object.

At the client's onServiceConnected(), user can get the proxy object of the stub defined at the remote service(the ISampleService.Stub.asInterface() returns the proxy class).

The proxy object can be used to call the remote methods of the Stub class implementation at the service process

## Facilitate communication between java & native code
- AIDL facilitate the communication between java/kotlin and native code:
When we have a project that includes native code components written in C++ (often referred to as the NDK or Native Development Kit), you can use AIDL to define an interface that acts as a bridge between the Java and C++ code. The AIDL interface defines the methods and data types that can be accessed from both `Java` and `C++`.

## Stub
Stub is a class that implements the remote interface in a way that you can use it as if it were a local one. It handles data unmarshalling/unmarshalling and sending/receiving to/from the remote service. The term 'stub' is generally used to describe this functionality in other RPC methods (COM, Java remoting, etc.), but it can mean slightly different things.

# --------------------------------------------------------------------------------------------
# Implementation
# --------------------------------------------------------------------------------------------

- Suppose we have two apps, Client app, and Server app.
- The client app want to get some data from Server app.
- So the workflow will be like this:
* 1. Create .Aidl file in server app.
* 2. Implement the Aidl interface.
* 3. Expose the interface to the client app.

- In the following example, a client app obtain random Address from server app.

# Server App: ===========================================================================

## Enable Aidl Feature
- Inside gradle.build (app-level)
``` groovy
android{
	....
	buildFeatures {
		...
        aidl = true
    }
}
```

## Create .Aidl File
> app -> New -> Aidl -> Aild file -> (Name) IAddressProvider.aidl
```java
	interface IAddressProvider{
		String obtainAddress(boolean isIpv6);
	}
```

## Generate Stub 
- When we build the project, Android will use that `Aidl` to generate a `Stub` bridge in directory `generated`.
- This Stub act like a bridge/interface between the client and the server.
- To generate the Stub class, we will re-build the project:
> Build -> Rebuild project.
- In `generated` directory, we will get:
```java
public static abstract Stub extends Binder implements IAddressProvider{...}
```

## Create Service (Exposing)
- The client connect to the server using its Service. 
- The service return Instance of Stub/IAddressProvider using IBinder. (BoundService)
- So the client bind to IAddressProvider using ServiceConnection, like we do to bind any service.
- Create bound service, and override onBind() method.
- in onBind(), instead of return the service instance, we return the Stub instance which contains our interface.

```java
class AddressService extends Service{
	@Override
	public IBinder onBind(Intent intent){
		return mBinder;
	}
	IAddressProvider.Stub mBinder = new IAddressProvider.Stub{
		@Override
		public String obtainAddress(boolean isIpv6){
			return (isIpv6) ? "::1" : "127.0.0.1"; // just example
		}
	};
}
```

- Declare the service, make service receive actions from outside (from client)
```xml
<service android:name=".AddressService" android:exported="true" android:enabled="true">
	<intent-filter>
		<action android:name="StartAddressService"/>
	</intent-filter>
</service>
```

# Client App: ===========================================================================
To connect to the server app and access its service, as well as its interface, we have to:
1. Create the same AIDL of the server inside the client.
2. Connect to its service using ServiceConnection

## Create AIDL File
- Create AIDL file with the same package-name/file-name/content of the server aidl.
- When we create client aidl file, we will get:
> app -> aidl -> com.mypackage.client -> IAddressProvider.aidl
- We have to rename the package to match the server package, like this:
> app -> aidl -> com.mypackage.server -> IAddressProvider.aidl
- Make sure that client aidl is the same in server aidl, including the package name.

## Connect to server service
- In our client app, to get the Stub/IAddressProvider, we only need to bind te the Service of server app.
- We make `ServiceConnection` and retrieve the `IAddressProvider` Instance.
- The Intent of service, we use the same action name we assign in service manifest declaration.
- Also, we set the package name of the server.
```java
public class Client{
	IAddressProvider addressProvider;
	// create service connection
	ServiceConnection serviceConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName component, IBinder binder){
			addressProvider = IAddressProvider.Stub.asInterface(binder);
		}
	};
	// bind to service
	Intent intent = new Intent();
	intent.setAction("StartAddressService");
	intent.setPackage("com.mypackage.server");
	bindService(intent, serviceConnection, BIND_AUTO_CREATE);

	// Access the interface
	String addressIpv6 = addressProvider.obtainAddress(true);
	String addressIpv4 = addressProvider.obtainAddress(false);
}
```
