Encrypted File Server
--------------------------------
A file server and client that uses a [Feistel cipher](https://en.wikipedia.org/wiki/Feistel_cipher) (based on [Tiny Encryption Algorithm](https://en.wikipedia.org/wiki/Tiny_Encryption_Algorithm) with a slight modification--it uses 256-bit keys) to communicate and transfer files over encrypted channels. The application is written primarily in Java, with the TEA encryption routines being in C, and interfaced via [JNI](http://docs.oracle.com/javase/7/docs/technotes/guides/jni/). The server can handle multiple client connections in parallel.

###Compiling

All the following commands build and put the targets in ```bin```. Tested on OS X and Linux. Requires Java/Javac 1.7, JNI, and gcc.

```sh
# To build everything
$ make
# Or
$ make rebuild

# To build the server
$ make server

# To build the client
$ make client
```

###Running

Recommended way: There is a handy Python script ```run``` provided that sets up the class and JNI load paths before launching either the server or client.

```sh
# To run the server:
# If -root is not used, the current directory is the root directory
$ ./run server <port> <path to TEA keys file> -root <path to server file root>

# To run the client:
# If -host is not used, defaults to localhost
$ ./run client <port> <path to TEA key file> -host <server host name>
```

The above two are equivalent to:

```sh
# Server:
$ java -Djava.library.path=./bin/com/udeyrishi/encryptedfileserver/common/tea -cp ./bin com.udeyrishi.encryptedfileserver.server.Main <port> <path to TEA keys file> -root <path to server file root>

# Client:
$ java -Djava.library.path=./bin/com/udeyrishi/encryptedfileserver/common/tea -cp ./bin com.udeyrishi.encryptedfileserver.client.Main <port> <path to TEA key file> -host <server host name>
```

Additionally, the ```server.config``` and ```client.config``` configuration files can be used to configure certain runtime behaviours, such as log levels, buffer sizes, etc.

###Usage
Once the server is running, you can start having client connections. If the credentials being used by the client are not valid, the server will reject the authentication request. Else, you'll get a prompt that you can use to download files like this:

```
Filename [ENTER to quit] >> hello.txt
Download path [ENTER to quit] >> downloads/hello.txt
```

The filename should be a relative file path below the server's root directory. If the directories in the download path don't exist, they will be created. Hitting ENTER with an empty input (either at the filename or download path prompt) will close the session.

###Keys file

For the client, the keys file should contain just a single line containing the user-id and the 256-bit TEA key. For instance:

```
test_id    0x12376378 08643124 24689864 78654323 23098712 08514330 67985643 12348723
```
Smaller than 256-bit keys will be 0-padded on the left.


For the server, the keys file should be in the same format as the client keys, except there can be more than one key in the file (one key per line). For instance,

```
test_id    0x12376378 08643124 24689864 78654323 23098712 08514330 67985643 12348723
test_id2   0x12345678 09876543 08643124 24689864 78654323 67985643 23098712 12348723
```

These are all the users that have access to the file server.

###Protocol
The entire communication happens over a TEA encrypted channel. The unencrypted protocol looks something like this:

```
1. Client-to-Server: type:Auth-Request;content:<user_name>
2. If the request message is decrypted by any of the allowed keys, and the corresponding user name matches the one in the auth request's content:
   Server-to-Client: type:Auth-Response;content:Access-Granted

   Else, this response is returned unencrypted:
   Server-to-Client: type:Auth-Response;content:Access-Denied

3. Post authentication, all the file requests will be sent like this:
   Client-to-Server: type:File-Request;content:<filename>

4. To which, the server can respond in any of the following ways:
   Server-to-Client: type:File-Response-Failure;content:File-Not-Found

   Or,
   Server-to-Client: type:File-Response-Success;content:<filename>\n<64bit_filesize_in_bytes><file_bytestream>

5. To terminate a session safely (optional):
   Client-to-Server: type:Termination-Request;content:No-Content

6. If post authentication, a garbage message is sent:
   Server-to-Client: type:File-Response-Failure;content:Bad-Request
```

