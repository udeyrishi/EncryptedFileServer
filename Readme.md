Encrypted File Server
--------------------------------
A file server and client that uses a [Feistel cipher](https://en.wikipedia.org/wiki/Feistel_cipher) (based on [Tiny Encryption Algorithm](https://en.wikipedia.org/wiki/Tiny_Encryption_Algorithm)) to communicate and transfer files over encrypted channels. The application is written in Java, and the server can handle multiple client connections in parallel. The TEA encryption routines are in C, and are interfaced via [JNI](http://docs.oracle.com/javase/7/docs/technotes/guides/jni/).

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

Additionally, the ```server.config``` and ```client.config``` configuration files can be used to configure certain kinds of runtime behaviours of the servers. The different options are either self-explanatory, or have inline descriptions.

###Keys file

For the client, the keys file should contain just a single line containing the user-id and the 256-bit TEA key. See ```test_data/client.key``` for instance.

For the server, the keys file should be in the same format as the client keys, except there can be more than one key in the file (one key per line). These are all the users that have access to the server. See ```test_data/keys.txt``` for instance.
