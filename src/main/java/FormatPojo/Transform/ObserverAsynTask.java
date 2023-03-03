package FormatPojo.Transform;

import dynamic.proxy.FormatWebProxy;
import dynamic.proxy.ScripsAppVpn;
import dynamic.proxy.ScripsWebProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import tungphamdev.oraclesun.RestclientVer2.ClientConfig;

public class ObserverAsynTask {

    int countTest, sizeList = -1;
    private int totalTask, limitTask, countTaskSkip, countTaskErr;
    private long timeDelay = 600;
    String idTask;
    boolean endSetIdTask, breakLoop, condiWebProxy;
    ExecutorService executorSingle = Executors.newSingleThreadExecutor();

    private HandleTaskStart handleTS;
    private HandleConditionLoop handleconditionLoop = () -> {
        return false;
    };

    public ObserverAsynTask() {
    }

    private Map<String, FormObserverAsynTask> mapIDTask_FormObserverTask = new HashMap<>();

    public ObserverAsynTask setSizeList(int sizeList) {
        this.sizeList = sizeList;
        return this;
    }

    public Map<String, FormObserverAsynTask> getMapIDTask_FormObserverTask() {
        return this.mapIDTask_FormObserverTask;
    }

    public ObserverAsynTask setTotalTask(int val) {
        this.totalTask = val;
        return this;
    }

    public ObserverAsynTask setConditionWebProxy(boolean val) {
        this.condiWebProxy = val;
        return this;
    }

    public ScripsAppVpn scripsAppVpn() {
        return ScripsAppVpn.getInstance().setOat(this);
    }

    public ScripsWebProxy scripsWebProxy() {
        return ScripsWebProxy.getInstance().setOat(this);
    }

    public ObserverAsynTask setHandleConditionLoop(HandleConditionLoop handleconditionLoop) {
        this.handleconditionLoop = handleconditionLoop;
        return this;
    }

    public ObserverAsynTask setHandleTaskStart(HandleTaskStart handleTS) {
        this.handleTS = handleTS;
        return this;
    }

    public ObserverAsynTask setTimeDelay(long timeDelay) {
        this.timeDelay = timeDelay;
        return this;
    }

    public ObserverAsynTask addCountTaskErr() {
        ++this.countTaskErr;
        return this;
    }

    public ObserverAsynTask createTaskId(int totalTask) {
        this.totalTask = this.limitTask = totalTask;
        for (int i = 0; i < totalTask; i++) {
            String uuid = UUID.randomUUID().toString();
            mapIDTask_FormObserverTask.put(
                    uuid,
                    new FormObserverAsynTask().setIndexTask(i).setFormWebProxy(new FormatWebProxy()).setIdTask(uuid).setTaskFree()
            );
        }

//        System.out.println(">>> map create " + mapIDTask_FormObserverTask.toString());
        return this;
    }

    public ObserverAsynTask runEventLoop(HandleLoopAsyn handleAsyn) {

        for (;;) {
            ++countTest;
            for (int i = 0; i < (totalTask > sizeList | sizeList != -1 ? sizeList : totalTask); i++) {

                CompletableFuture.supplyAsync(this::getTask, executorSingle)
                        .thenApplyAsync((t) -> {            // handle taskStart 
//                            --totalTask;
                            if (t == null) {
                                return t;
                            }
                            if (handleconditionLoop.cond()) {
                                t.setSkip(true);
                            }
                            return handleTS.taskStart(t);
                        }, executorSingle)
                        .thenApplyAsync((t) -> {            // handle working 
                            if (!t.isSkip() & t != null) {
                                t.setTaskWorking();
                                return handleAsyn.handle(t);
                            }
                            return t;
                        })
                        .whenCompleteAsync((t, u) -> {
                            if (t != null) {
                                this.continueLoop(t).setTaskFree();
                            }
                        }, executorSingle);   // handle end 
            }

            synchronized (this) {
                if (handleconditionLoop.cond() | isBreakLoop()) {
                    break;
                }
            }

            awaitLoop();
        }

        System.out.println("-------------------------end eventLoop----------------------------------");
        return this;
    }

    private synchronized FormObserverAsynTask getTask() {
        FormObserverAsynTask formObserverTask = null;

        for (Map.Entry<String, FormObserverAsynTask> entry : mapIDTask_FormObserverTask.entrySet()) {
            String idTask = entry.getKey();
            FormObserverAsynTask valueObserver = entry.getValue();

            if (valueObserver.isTaskFree() & !valueObserver.isSkip()) {
                formObserverTask = valueObserver;
                mapIDTask_FormObserverTask.replace(idTask, formObserverTask.setTaskStart());

                break;
            }
        }
//        System.out.println(">> map in getTask countTest " + countTest + " " + formObserverTask.getIdTask() + " - " + mapIDTask_FormObserverTask.toString());
        return formObserverTask;
    }

    /*
    *** 
     */
    public synchronized FormObserverAsynTask continueLoop(FormObserverAsynTask formObserver) {

        if (formObserver.isSkip()) {
            List<FormObserverAsynTask> list = new ArrayList<>(mapIDTask_FormObserverTask.values());

            int countTaskSkipLocal = (int) list.stream().filter((t) -> t.isSkip()).count();
            System.out.println(">>>>>> check countTaskSkipLocal " + countTaskSkipLocal + " limitTask " + limitTask);

            if (countTaskSkipLocal == totalTask - 1) {
                formObserver.setSkip(false);
                CompletableFuture.delayedExecutor(timeDelay, TimeUnit.MILLISECONDS, executorSingle).execute(() -> {
                    totalTask = limitTask;
                    wakeUp();

                });
            }

        } else {
//            ++totalTask;
            wakeUp();
        }

        return formObserver;
    }

    private boolean isBreakLoop() {
        System.out.println("totalTaskErr " + countTaskErr + " limTask " + limitTask);
        if (countTaskErr == limitTask) {
            return true;
        }
        return false;
    }

    private synchronized void wakeUp() {
        notify();
    }

    private synchronized ObserverAsynTask awaitLoop() {

        try {
            wait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

}
