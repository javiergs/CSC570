import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class EmotivDelegate {

    public static final String CLIENT_ID = "dYvuSFLyPj87WKphZZEZrE7rAwiZv5r390bMPCKK";
    public static final String CLIENT_SECRET = "cRjefI0seoDCs9G2gPGwC8ctIKy19DbGHMUm9GbYqEpycOhYG7echQFf1K89mFdE8W61cZ4jvcU6So6rSBWisjFtBZi6AYfYIWTn8vibgcllDlLcYgwWOs0E9sz5LKS6";

    private boolean subscribed = false;
    private String cortexToken = null;
    private String firstName, lastName, userName;
    private String headset = null;
    private String session = null;

    public void getCortexInfo(WebSocketClient ws) {
        System.out.println("getCortexInfo: done!");
        JSONObject message = new JSONObject();
        message.put("id", 1);
        message.put("jsonrpc", "2.0");
        message.put("method", "getCortexInfo");
        ws.send(message.toString());
    }

    public void requestAccess(WebSocketClient ws) {
        System.out.println("requestAccess: done!");
        JSONObject message = new JSONObject();
        message.put("id", 2);
        message.put("jsonrpc", "2.0");
        message.put("method", "requestAccess");
        JSONObject params = new JSONObject();
        params.put("clientId", CLIENT_ID);
        params.put("clientSecret", CLIENT_SECRET);
        message.put("params", params);
        ws.send(message.toString());
    }

    public void authorize(WebSocketClient ws) {
        System.out.println("authorize: done!");
        JSONObject message = new JSONObject();
        message.put("id", 3);
        message.put("jsonrpc", "2.0");
        message.put("method", "authorize");
        JSONObject params = new JSONObject();
        params.put("clientId", "dYvuSFLyPj87WKphZZEZrE7rAwiZv5r390bMPCKK");
        params.put("clientSecret", "cRjefI0seoDCs9G2gPGwC8ctIKy19DbGHMUm9GbYqEpycOhYG7echQFf1K89mFdE8W61cZ4jvcU6So6rSBWisjFtBZi6AYfYIWTn8vibgcllDlLcYgwWOs0E9sz5LKS6");
        params.put("debit", 1);
        message.put("params", params);
        ws.send(message.toString());
    }

    public void getUserInformation(WebSocketClient ws) {
        System.out.println("getUserInformation");
        if (cortexToken != null) {
            JSONObject message = new JSONObject();
            message.put("id", 4);
            message.put("jsonrpc", "2.0");
            message.put("method", "getUserInformation");
            message.put("params", new JSONObject().put("cortexToken", cortexToken));
            ws.send(message.toString());
        }
    }

    public void queryHeadsets(WebSocketClient ws) {
        System.out.println("queryHeadsets");
        JSONObject message = new JSONObject();
        message.put("id", 5);
        message.put("jsonrpc", "2.0");
        message.put("method", "queryHeadsets");
        ws.send(message.toString());
    }

    public void createSession(WebSocketClient ws) {
        if (cortexToken != null && headset != null) {
            System.out.println("createSession");
            JSONObject message = new JSONObject();
            message.put("id", 6);
            message.put("jsonrpc", "2.0");
            message.put("method", "createSession");
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("headset", headset);
            params.put("status", "active");
            message.put("params", params);
            ws.send(message.toString());
        }
    }

    public void subscribe(WebSocketClient ws) {
        System.out.println("subscribe");
        if (cortexToken != null && session != null) {
            JSONObject message = new JSONObject();
            message.put("id", 7);
            message.put("jsonrpc", "2.0");
            message.put("method", "subscribe");
            JSONObject params = new JSONObject();
            params.put("cortexToken", cortexToken);
            params.put("session", session);
            params.put("streams", new JSONArray().put("met"));
            // "met" is for emotion, "dev" is for dev mode, "fac" is for facial gestures
            message.put("params", params);
            ws.send(message.toString());
        }
    }

    public void handle(int id, Object result, WebSocketClient ws) {
        System.out.println("Handle: " + id);
        JSONObject myJsonObj;
        switch (id) {
            case 0:
                getCortexInfo(ws);
                break;
            case 1:
                requestAccess(ws);
                break;
            case 2:
                authorize(ws);
                break;
            case 3:
                myJsonObj = new JSONObject(result.toString());
                cortexToken = myJsonObj.getString("cortexToken");
                getUserInformation(ws);
                break;
            case 4:
                myJsonObj = new JSONObject(result.toString());
                System.out.println(">>>> getUserInformation: " + myJsonObj.toString());
                firstName = myJsonObj.getString("firstName");
                lastName = myJsonObj.getString("lastName");
                userName = myJsonObj.getString("username");
                queryHeadsets(ws);
                break;
            case 5:
                JSONArray myJsonArr = new JSONArray(result.toString());
                headset = myJsonArr.getJSONObject(0).getString("id");
                createSession(ws);
                break;
            case 6:
                myJsonObj = new JSONObject(result.toString());
                session = myJsonObj.getString("id");
                subscribe(ws);
                break;
            case 7:
                subscribed = true;
        }
    }


    public boolean isSubscribed() {
        return subscribed;
    }
}
