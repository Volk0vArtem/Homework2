package Behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PongBehaviour extends AchieveREResponder {
    public PongBehaviour(Agent a) {
        super(a, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchProtocol("ping")));
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) {
        ACLMessage reply = request.createReply();
        log.info("Agent {} got {} from {}", getAgent().getLocalName(), request.getContent(), request.getSender().getLocalName());
        reply.setPerformative(ACLMessage.INFORM);
        reply.setProtocol("pong");
        reply.setContent("Pong");
        return reply;
    }
}