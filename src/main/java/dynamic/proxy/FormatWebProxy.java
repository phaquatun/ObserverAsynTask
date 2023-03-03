package dynamic.proxy;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class FormatWebProxy {

    String ip = "none", keyApi, idTaskAsyn, response;
    int nextRequest;
    long timeGetIp;
    private boolean getProxyErr;

    List<String> listTaskUseKeyApi = new ArrayList<>();

    public FormatWebProxy() {

    }

    public FormatWebProxy setKeyApi(String keyApi) {
        this.keyApi = keyApi;
        return this;
    }

    public FormatWebProxy setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public FormatWebProxy setNextRequest(int nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    public FormatWebProxy setTimeGetIp(long timeGetIp) {
        this.timeGetIp = timeGetIp;
        return this;
    }

    public FormatWebProxy addTaskUseKeyApi(String idTask) {
        listTaskUseKeyApi.add(idTask);
        return this;
    }

    public List<String> getListTaskUseKeyApi() {
        return this.listTaskUseKeyApi;
    }

    public int getCountTaskUseKeyApi() {

        return listTaskUseKeyApi.size();
    }

    public FormatWebProxy setIdTask(String idTaskAsyn) {
        this.idTaskAsyn = idTaskAsyn;
        return this;
    }

    public FormatWebProxy setResponse(String response) {
        this.response = response;
        return this;
    }

    public FormatWebProxy setGetProxyErr(boolean getProxyErr) {
        this.getProxyErr = getProxyErr;
        return this;
    }

    public boolean isChangeNextIp() {
        long time = System.currentTimeMillis();
        boolean val = (time - timeGetIp) / 1000 >= nextRequest + 25;
        System.out.println(">> check time end to change ip " + new JSONObject().put("keyApi", keyApi).put("nextRequest", val).put("ip", ip));

        return val;
    }

    @Override
    public String toString() {
        return new JSONObject().put("ip", ip)
                .put("nextRequest", nextRequest)
                .put("keyApi", keyApi).put("idTaskAsyn", idTaskAsyn)
                .put("timeGetIp", timeGetIp)
                .put("countTask", listTaskUseKeyApi.size())
                .put("listTaskUseKeyApi", listTaskUseKeyApi.toString())
                .put("response", response).put("getProxyErr", getProxyErr)
                .toString();
    }

}
