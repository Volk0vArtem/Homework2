import Json.JsonMessage;
import AgentDetector.AgentDetector;
import jade.core.AID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AgentDetectorTest {

    @Test
    @SneakyThrows
    void twoActiveDetectors(){
        AID aid1 = new AID("Agent1",true);
        AID aid2 = new AID("Agent2",true);

        JsonMessage jsonMessage = new JsonMessage();
        String data1 = jsonMessage.toJson(aid1.getLocalName());
        String data2 = jsonMessage.toJson(aid2.getLocalName());

        AgentDetector detector1 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data1);
        AgentDetector detector2 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data2);

        detector1.startDiscovering();
        detector2.startDiscovering();

        detector1.startSending();
        detector2.startSending();

        Thread.sleep(2000);
        detector1.stopSending();
        detector2.stopSending();

        Assertions.assertEquals(1, detector1.getActiveAgents().size());
        Assertions.assertEquals(1, detector2.getActiveAgents().size());
    }

    @Test
    @SneakyThrows
    void stoppingOneDetector(){
        AID aid1 = new AID("Agent1",true);
        AID aid2 = new AID("Agent2",true);

        JsonMessage jsonMessage = new JsonMessage();
        String data1 = jsonMessage.toJson(aid1.getLocalName());
        String data2 = jsonMessage.toJson(aid2.getLocalName());

        AgentDetector detector1 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data1);
        AgentDetector detector2 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data2);

        detector1.startDiscovering();
        detector2.startDiscovering();

        detector1.startSending();
        detector2.startSending();

        Thread.sleep(2000);
        detector1.stopSending();
        Thread.sleep(2000);
        detector2.stopSending();

        Assertions.assertEquals(1, detector1.getActiveAgents().size());
        Assertions.assertEquals(0, detector2.getActiveAgents().size());
    }

    @Test
    @SneakyThrows
    void fourActiveDetectors() {
        AID aid1 = new AID("Agent1",true);
        AID aid2 = new AID("Agent2",true);
        AID aid3 = new AID("Agent3",true);
        AID aid4 = new AID("Agent4",true);

        JsonMessage jm = new JsonMessage();
        String data1 = jm.toJson(aid1.getLocalName());
        String data2 = jm.toJson(aid2.getLocalName());
        String data3 = jm.toJson(aid3.getLocalName());
        String data4 = jm.toJson(aid4.getLocalName());

        AgentDetector detector1 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data1);
        AgentDetector detector2 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data2);
        AgentDetector detector3 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data3);
        AgentDetector detector4 = new AgentDetector("\\Device\\NPF_Loopback", 1000, data4);

        detector1.startDiscovering();
        detector2.startDiscovering();
        detector3.startDiscovering();
        detector4.startDiscovering();

        detector1.startSending();
        detector2.startSending();
        detector3.startSending();
        detector4.startSending();

        Thread.sleep(2000);
        detector1.stopSending();
        detector2.stopSending();
        detector3.stopSending();
        detector4.stopSending();

        Assertions.assertEquals(3, detector1.getActiveAgents().size());
        Assertions.assertEquals(3, detector2.getActiveAgents().size());
        Assertions.assertEquals(3, detector3.getActiveAgents().size());
        Assertions.assertEquals(3, detector4.getActiveAgents().size());

    }
}
