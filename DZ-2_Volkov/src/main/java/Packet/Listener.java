package Packet;

import Json.JsonMessage;
import jade.core.AID;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapPacket;
import java.util.Map;

public class Listener implements PacketListener {
    private Map<AID, Long> hashMap;
    private AID cAID;
    private PacketBuilder packetBuilder;
    private JsonMessage jsonMessage;

    public Listener(Map<AID, Long> hashMap, AID cAID) {
        this.hashMap = hashMap;
        this.cAID = cAID;
        packetBuilder = new PacketBuilder();
        jsonMessage = new JsonMessage();
    }

    @Override
    public synchronized void gotPacket(PcapPacket packet) {
        String receivedData = packetBuilder.parse(packet.getRawData());
        String agentName = jsonMessage.fromJson(receivedData).getAgentName();
        Long time = System.currentTimeMillis();
        cAID = new AID(agentName, true);
        hashMap.put(cAID, time);
    }
}
