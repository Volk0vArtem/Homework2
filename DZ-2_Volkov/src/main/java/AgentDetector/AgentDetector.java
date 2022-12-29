package AgentDetector;

import AdditionalClasses.PcapHelper;
import Packet.Listener;
import Packet.Listn;
import Packet.PacketBuilder;
import Json.JsonMessage;
import jade.core.AID;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AgentDetector {

    private ScheduledFuture<?> discoveringTask;
    private ScheduledFuture<?> sendingTask;
    private String interfaceName;
    private int T;
    private byte[] packetData;
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
    private PcapHelper pcapHelper;
    private PacketBuilder packetBuilder;
    private boolean isDiscovering, isSending;
    private AID cAID;
    private final String data;
    private Map<AID, Long> hashMap = new HashMap<>();
    private Map<AID, Long> newHashMap;
    private List<AID> aidList;
    private final List<Listn> subscribers = new ArrayList<>();

    public AgentDetector(String interfaceName, int t, String data) {
        this.interfaceName = interfaceName;
        this.data = data;
        T = t;
        pcapHelper = new PcapHelper(interfaceName, t);
        packetBuilder = new PacketBuilder("\\Device\\NPF_Loopback", data, 2500);
        packetData = packetBuilder.build();
    }

    public synchronized void startDiscovering() {
        Listener l = new Listener(hashMap, cAID);
        if (!isDiscovering) {
            discoveringTask = pcapHelper.startPacketsCapturing(2500, l , ses);
            isDiscovering = true;
        }
    }

    public void startSending() {
        if (!isSending) {
            sendingTask = ses.scheduleAtFixedRate( () -> pcapHelper.sendPacket(packetData), T, T, TimeUnit.MILLISECONDS);
            isSending = true;
        }
    }

    public synchronized void deadAgentRemoving() {
        JsonMessage jsonMessage = new JsonMessage();
        String agentsName = jsonMessage.fromJson(data).getAgentName();
        newHashMap = new HashMap<>();
        for (Map.Entry<AID, Long> entry: hashMap.entrySet()) {
            if ((System.currentTimeMillis() - entry.getValue() < 1000) && !(entry.getKey().toString().contains(agentsName))) {
                newHashMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public synchronized List<AID> getActiveAgents() {
        deadAgentRemoving();
        aidList = new ArrayList<>(newHashMap.keySet());
        return aidList;
    }

    public void subscribeOnChange(Listn subscriber) {
        subscribers.add(subscriber);
    }

    public void stopSending() {
        if (isSending) {
            sendingTask.cancel(true);
            isSending = false;
        }
    }
}

