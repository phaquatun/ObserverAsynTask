package dynamic.proxy;

import FormatPojo.Transform.FormObserverAsynTask;
import FormatPojo.Transform.HandleConditionAppVpn;
import FormatPojo.Transform.ObserverAsynTask;
import java.util.HashMap;
import java.util.Map;

public class ScripsAppVpn {

    static class Helper {

        static final ScripsAppVpn sav = new ScripsAppVpn();
    }

    public static ScripsAppVpn getInstance() {
        return Helper.sav;
    }

    int limitCountTask, countChekLimit;
    int timeSecond;
    
    public static String ipAppVpn = "ipAppVpn";
    
    HandleConditionAppVpn handleCondAppVpn;
    HandleChangeAppVpn handleChange;
    ObserverAsynTask oat;

    Map<String, FormObserverAsynTask> mapIDTask_FormObserverTask = new HashMap<>();

    public ScripsAppVpn setOat(ObserverAsynTask oat) {
        this.oat = oat;
        this.mapIDTask_FormObserverTask = oat.getMapIDTask_FormObserverTask();
        return this;
    }

    public ScripsAppVpn setLimitTask(int limitCountTask) {
        if (!handleCondAppVpn.handle()) {
            return this;
        }
        this.limitCountTask = limitCountTask;
        return this;
    }
    
      public ScripsAppVpn setHandleConditionAppVpn(HandleConditionAppVpn handleCondAppVpn) {
        this.handleCondAppVpn = handleCondAppVpn;
        return this;
    }

    public ScripsAppVpn setHandleChangeVpn(HandleChangeAppVpn handleChange) {
        this.handleChange = handleChange;
        return this;
    }

    public ScripsAppVpn setLimitTaskReset(int totalTask) {
        this.limitCountTask = totalTask;
        return this;
    }

    public ScripsAppVpn changeVpnFirst() {
        if (!handleCondAppVpn.handle()) {
            return this;
        }
        oat.getMapIDTask_FormObserverTask().forEach((t, u) -> {
            u.getFormWebProxy().setIp("vpnApp");
        });
        handleChange.change();
        return this;
    }
    
  

    public FormObserverAsynTask changeVpnNext(FormObserverAsynTask formObserverTask) {

        if (!handleCondAppVpn.handle()) {
            return formObserverTask;
        }

        if (countChekLimit != limitCountTask) {
            ++countChekLimit;
            return formObserverTask;
        }

        if (countChekLimit == limitCountTask & !allTaskFreeUseKey(formObserverTask)) {
            formObserverTask.setSkip(true);
            return formObserverTask;
        }

        if (countChekLimit == limitCountTask & allTaskFreeUseKey(formObserverTask)) {
            countChekLimit =0;
            handleChange.change();
            formObserverTask.setSkip(false);
            var map = oat.getMapIDTask_FormObserverTask();
            map.forEach((t, u) -> u.setTaskFree().setSkip(false));

//            System.out.println("checkMap ít's time changeProxy size " + oat.getMapIDTask_FormObserverTask().size()
//                    + " " + oat.getMapIDTask_FormObserverTask().toString());
            oat.setTotalTask(oat.getMapIDTask_FormObserverTask().size());
            return formObserverTask;
        }
       
        return formObserverTask;
    }

    boolean allTaskFreeUseKey(FormObserverAsynTask formObserverTask) {
        boolean free = true;
        String keyApi = formObserverTask.getFormWebProxy().getKeyApi();

        for (Map.Entry<String, FormObserverAsynTask> entry : mapIDTask_FormObserverTask.entrySet()) {
            String keyIDTask = entry.getKey();
            FormObserverAsynTask value = entry.getValue();

            var formWebProxy = value.getFormWebProxy();
            if (value.isTaskWorking()) {
                free = false;
                break;
            }
        }

        return free;
    }
}
