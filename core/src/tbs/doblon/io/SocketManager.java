package tbs.doblon.io;


import org.json.JSONObject;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by mike on 5/29/16.
 */
public class SocketManager {
    //    JSONObject obj = new JSONObject();
//    obj.put("hello", "server");
//    obj.put("binary", new byte[42]);
//    socket.emit("foo", obj);
    static Socket socket;

    public static void init() {
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        opts.query = "op=" + "sda";
        try {
            socket = IO.socket("http://10.0.0.38:5000", opts);
        } catch (Exception e) {

        }

        socket.on("mds", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Utility.log("connected");
                String a = "";
                for (Object arg : args) {
                    a+= String.valueOf(arg) + " , ";
                }
                Utility.log(String.valueOf(a));
            }

        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.emit("foo", "hi");
                socket.disconnect();
            }

        }).on("event", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
            }

        });
        socket.connect();
    }

    public void send() {
        // Sending an object
        JSONObject obj = new JSONObject();
        obj.put("hello", "server");
        obj.put("binary", new byte[42]);
        socket.emit("foo", obj);


    }

    public void receive() {
        // Receiving an object
        socket.on("foo", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
            }
        });
    }
}
