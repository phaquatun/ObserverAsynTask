package dynamic.proxy;

import FormatPojo.Transform.FormObserverAsynTask;
import FormatPojo.Transform.HandleConditionUseWebProxy;
import FormatPojo.Transform.ObserverAsynTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class ScripsWebProxy {

    int countKeyApi;
    boolean getAllKeyApiFaile;

    private Map<String, FormatWebProxy> mapKey_FormWebProxy = new HashMap<>();
    private Map<String, FormObserverAsynTask> mapIDTask_FormObserverTask = new HashMap<>();

    private HandleChangeProxy handleChangeProxy;
    private HandleConditionUseWebProxy handleCondWebProxy;
    private ObserverAsynTask oat;

    static class Helper {

        final static ScripsWebProxy instance = new ScripsWebProxy();
    }

    public static ScripsWebProxy getInstance() {
        return Helper.instance;
    }

    private ScripsWebProxy() {

    }

    public ScripsWebProxy setOat(ObserverAsynTask oat) {
        this.oat = oat;
        this.mapIDTask_FormObserverTask = oat.getMapIDTask_FormObserverTask();
        return this;
    }

    public ScripsWebProxy setHandleConditionWebProxy(HandleConditionUseWebProxy handleCondWebProxy) {
        this.handleCondWebProxy = handleCondWebProxy;
        return this;
    }

    public Map<String, FormatWebProxy> getMapKey_FormWebProxy() {
        return this.mapKey_FormWebProxy;
    }

    public ScripsWebProxy setHandleGetProxy(HandleChangeProxy handle) {
        this.handleChangeProxy = handle;
        return this;
    }

    public ScripsWebProxy getFirstProxy() {
        if (!handleCondWebProxy.handle()) {
            return this;
        }

        List<String> list = new ArrayList<>(mapKey_FormWebProxy.keySet());
        List<CompletableFuture<FormatWebProxy>> listFutWebproxy = list.stream().map(this::futFormWebProxy).collect(Collectors.toList());

        CompletableFuture<Void> futAll = CompletableFuture.allOf(listFutWebproxy.toArray(new CompletableFuture[listFutWebproxy.size()]));

        CompletableFuture<List<FormatWebProxy>> futListFormWebProxy = futAll.thenApply((unuse) -> {
            return listFutWebproxy.stream().map((f) -> {
                return f.join();
            }).collect(Collectors.toList());
        });

        mapKey_FormWebProxy = futListFormWebProxy.join().stream().collect(Collectors.toMap(FormatWebProxy::getKeyApi, Function.identity()));

//        System.out.println(">>getFirstProxy check new map " + mapKey_FormWebProxy.toString());

        setFormWebProxyTask();

//        System.out.println(">>getFirstProxy check new Map key forObserverTask " + mapIDTask_FormObserverTask.toString());
//        System.out.println(">>getFirstProxy check value new Map key forObserverTask " + mapIDTask_FormObserverTask.values().toString());
        return this;
    }

    public FormObserverAsynTask getNextProxy(FormObserverAsynTask formObserverTask) {
        if (!handleCondWebProxy.handle()) {
            return formObserverTask;
        }

        if (!formObserverTask.getFormWebProxy().isChangeNextIp()) {
            return formObserverTask;
        }

        if (formObserverTask.getFormWebProxy().isChangeNextIp() & !allTaskFreeUseKey(formObserverTask)) {
            formObserverTask.setSkip(true);
            return formObserverTask;
        }

        if (formObserverTask.getFormWebProxy().isChangeNextIp() & allTaskFreeUseKey(formObserverTask)) {
//            System.out.println("ít's time key " + formObserverTask.getFormWebProxy().getKeyApi() + " - " + formObserverTask.getFormWebProxy().toString());

            var formWebProxyNew = handleChangeProxy.handleChange(formObserverTask.getFormWebProxy().getKeyApi());
            formObserverTask.setFormWebProxy(formWebProxyNew).setSkip(false);

            var map = oat.getMapIDTask_FormObserverTask();
            map.forEach((t, u) -> {
                if (formWebProxyNew.getKeyApi().equals(u.getFormWebProxy().getKeyApi())) {
                    u.setFormWebProxy(formWebProxyNew).setSkip(false);
                }
            });

////            System.out.println("checkMap ít's time changeProxy size " + oat.getMapIDTask_FormObserverTask().size()
////                    + " " + oat.getMapIDTask_FormObserverTask().toString());
            oat.setTotalTask(oat.getMapIDTask_FormObserverTask().size());
            return formObserverTask;
        }

        return formObserverTask;
    }

    /*
    *** getNextProxy
     */
    boolean allTaskFreeUseKey(FormObserverAsynTask formObserverTask) {
        boolean free = true;
        String keyApi = formObserverTask.getFormWebProxy().getKeyApi();

        for (Map.Entry<String, FormObserverAsynTask> entry : mapIDTask_FormObserverTask.entrySet()) {
            String keyIDTask = entry.getKey();
            FormObserverAsynTask value = entry.getValue();

            var formWebProxy = value.getFormWebProxy();
            if (keyApi.equals(value.getFormWebProxy().getKeyApi()) & value.isTaskWorking()) {
                free = false;
                break;
            }
        }

        return free;
    }

    /*
    *** getFirstProxy
     */
    private CompletableFuture<FormatWebProxy> futFormWebProxy(String keyApi) {
        return CompletableFuture.supplyAsync(() -> {
            return handleChangeProxy.handleChange(keyApi);
        });
    }

    void setFormWebProxyTask() {

        var listFormWebProxy = mapKey_FormWebProxy.entrySet().stream()
                .filter(t -> t.getValue().isGetProxyErr() == false)
                .map(Map.Entry::getValue).collect(Collectors.toList());

        if (listFormWebProxy.size() == 0) {
            getAllKeyApiFaile = true;
        }

        int countKeyApi = 0;
        for (Map.Entry<String, FormObserverAsynTask> entry : mapIDTask_FormObserverTask.entrySet()) {
            String keyIdTask = entry.getKey();
            FormObserverAsynTask value = entry.getValue();

            if (countKeyApi == listFormWebProxy.size()) {
                countKeyApi = 0;
            }

            var newValue = value.setFormWebProxy(listFormWebProxy.get(countKeyApi++));
            mapIDTask_FormObserverTask.replace(keyIdTask, newValue);
        }

    }

}
