package FormatPojo.Transform;

import dynamic.proxy.FormatWebProxy;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.json.JSONObject;

@Getter
public class FormObserverAsynTask {

    String idTask, statusTask;
    int indexTask;
    boolean taskStart, taskWorking, taskFree, taskErr, skip;
    private FormatWebProxy formWebProxy;
    private Map<String, Object> mapTransForm = new HashMap<>();

    public FormObserverAsynTask setIndexTask(int indexTask) {
        this.indexTask = indexTask;
        return this;
    }
    

    public FormObserverAsynTask setIdTask(String idTask) {
        this.idTask = idTask;
        return this;
    }

    public FormObserverAsynTask setTaskErr(boolean taskErr) {
        this.taskErr = taskErr;
        return this;
    }

    public FormObserverAsynTask setSkip(boolean skip) {
        this.skip = skip;
        return this;
    }

    public FormObserverAsynTask setTaskStart() {
        this.taskStart = true;
        taskWorking = taskFree = false;
        return this;
    }

    public FormObserverAsynTask setTaskWorking() {
        this.taskWorking = true;
        taskStart = taskFree = false;
        return this;
    }

    public FormObserverAsynTask setTaskFree() {
        this.taskFree = true;
        taskStart = taskWorking = false;
        return this;
    }

    public FormObserverAsynTask setFormWebProxy(FormatWebProxy formWebProxy) {
        this.formWebProxy = formWebProxy;
        return this;
    }

    public Map<String, Object> getMapTransForm() {
        return this.mapTransForm;
    }

    public FormObserverAsynTask putObjTransform(String key, Object obj) {
        mapTransForm.put(key, obj);
        return this;
    }

    @Override
    public String toString() {
        return new JSONObject().put("idTask", idTask).put("statusTask", statusTask)
                .put("indexTask", indexTask).put("skip", skip).put("formWebProxy", formWebProxy.toString())
                .put("taskWorking", taskWorking).put("taskStart", taskStart)
                .put("taskFree", taskFree).put("taskErr", taskErr)
                .toString();
    }
}
