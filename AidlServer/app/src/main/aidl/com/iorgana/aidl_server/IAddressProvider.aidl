// IAddressProvider.aidl
package com.iorgana.aidl_server;

/**
* When we build the project, Android will use that `Aidl` to generate a `Stub` bridge in directory `generated`.
* This Stub act like a bridge/interface between the client and the server.
* To generate the Stub class, we will re-build the project:
* -> Build -> Rebuild project.
* In `generated` directory, we will get:
* ```java
*     public static abstract Stub extends Binder implements IAddressProvider{...}
* ```
*/
interface IAddressProvider {

    String obtainAddress(boolean isIpv6);
}