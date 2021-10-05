/* This is not a piece of code that will compile, it is an overview of how the
 * final API might look/work when it is implemented and a general goal to work
 * towards
 */

public class Plan {
    public static void main(String[] args) {

        int port = 1234;
        // Start a server which listens for connections over port 1234
        ServerSocket server = new ServerSocket(port);

        // Attempt to open a connection to the server
        Socket clientSocket = new Socket("localhost", port);

        // Accept the client connection
        Socket serverSocket = server.accept();

        // Declare the methods that will be made accessible over the RPC connection
        RPCMethod1<String, Integer> m1 = this::methodOne;
        RPCMethod0<Boolean> m2 = this::isTrue;

        // Initialize instances of classes implementing RPCChannel
        SocketRPCChannel serverChannel = new SocketRPCChannel(serverSocket);
        SocketRPCChannel clientChannel = new SocketRPCChannel(clientSocket);

        RPCManager serverRPCManager = new RPCManager(serverChannel, {m1, m2}, {m2, m1});
        RPCManager clientRPCManager = new RPCManager(clientChannel, {m2, m1}, {m1, m2});

        clientRPCManager.call(m1, 5);
        RPC sIncomingRPC = serverRPCManager.receive();
        serverRPCManager.handle(sIncomingRPC);
        // "methodOne was called!" is printed after call to handle

        serverRPCManager.call(m2);
        RPC cIncomingRPC = clientRPCManager.receive();
        clientRPCManager.handle(cIncomingRPC);
        // "isTrue was called!" is printed after call to handle
    }

    public String methodOne(int a) {
        System.out.println("methodOne was called!");
        return ((Integer) a).toString();
    }

    public boolean isTrue() {
        System.out.println("isTrue was called!");
        return true;
    }
}
