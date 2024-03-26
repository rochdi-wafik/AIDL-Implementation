// IAddressProvider.aidl
package com.iorgana.aidl_server;

/**
 * Create AIDL File
 -------------------------------------------------------------------
  - Create AIDL file with the same package-name/file-name/content of the server aidl.
  - When we create client aidl file, we will get:
  > app -> aidl -> com.mypackage.client -> IAddressProvider.aidl
  - We have to rename the package to match the server package, like this:
  > app -> aidl -> com.mypackage.server -> IAddressProvider.aidl
  - Make sure that client aidl is the same in server aidl, including the package name.
 */
interface IAddressProvider {

    String obtainAddress(boolean isIpv6);
}