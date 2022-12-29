package Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class JsonMessage {

    public String toJson(String agentName){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        CfgClass obj_cfg = new CfgClass(agentName);
        String message = gson.toJson(obj_cfg, CfgClass.class);
        return message;
    }

    public CfgClass fromJson(String message){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        CfgClass obj_cfg = gson.fromJson(message, CfgClass.class);
        return obj_cfg;
    }
}