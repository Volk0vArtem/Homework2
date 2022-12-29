package Packet;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.nio.ByteBuffer;

@NoArgsConstructor
public class PacketBuilder{
    private String packetData;
    private String iface;
    private int portToSend;
    private byte[] packet;
    private byte[] platformDescBytes;
    private int dataLength, totalLength;

    public PacketBuilder(String iface, String data, int portToSend) {
        this.packetData = data;
        this.portToSend = portToSend;
        this.iface = iface;
        platformDescBytes = data.getBytes();
        dataLength = platformDescBytes.length;
        totalLength = dataLength + 28;
        packet = new byte[totalLength + (iface.equals("\\Device\\NPF_Loopback") ? 4 : 14)];
    }

    @SneakyThrows
    public byte[] addHeader() {
        /* set NPF_Loopback as iface to use. WORKS ONLY FOR WINDOWS*/
        for (int i = 0, j = 7; i < 1; i++, j++) packet[i] = longToBytes(0x02)[j];
        /* send to all*/
        byte[] ipDestinationBytes = InetAddress.getByName("255.255.255.255").getAddress();
        byte[] ipSourceBytes = InetAddress.getByName("127.0.0.1").getAddress();
        //Header Length = 20 bytes
        for (int i = 4, j = 7; i < 5; i++, j++) packet[i] = longToBytes(69)[j];
        //Differentiated Services Field
        for (int i = 5, j = 7; i < 6; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Total Length
        for (int i = 6, j = 6; i < 8; i++, j++) packet[i] = longToBytes(totalLength)[j];
        //Identification - for fragmented packages
        for (int i = 8, j = 6; i < 10; i++, j++) packet[i] = longToBytes(33500)[j];
        //Flag, Fragment Offset - for fragmented packages
        for (int i = 10, j = 6; i < 12; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Time to Live - max limit for moving through the network
        for (int i = 12, j = 7; i < 13; i++, j++) packet[i] = longToBytes(128)[j];
        //Protocol - UDP
        for (int i = 13, j = 7; i < 14; i++, j++) packet[i] = longToBytes(17)[j];
        //Header Checksum, can be 0x00 if it is not calculated
        for (int i = 14, j = 6; i < 16; i++, j++) packet[i] = longToBytes(0x00)[j];

        for (int i = 16, j = 0; i < 20; i++, j++) packet[i] = ipSourceBytes[j];
        for (int i = 20, j = 0; i < 24; i++, j++) packet[i] = ipDestinationBytes[j];
        return packet;
    }

    public byte[] addUdpPart() {
        //Source port
        for (int i = 24, j = 6; i < 26 ; i++, j++) packet[i] = longToBytes(portToSend)[j];
        //Destination port
        for (int i = 26 , j = 6; i < 28; i++, j++) packet[i] = longToBytes(portToSend)[j];
        return packet;
    }

    public byte[] addPayload() {
        int length = totalLength - 20;
        for (int i = 28, j = 6; i < 30; i++, j++) packet[i] = longToBytes(length)[j];
        //Checksum, can be 0x00 if it is not calculated
        for (int i = 30, j = 6; i < 32; i++, j++) packet[i] = longToBytes(0x0000)[j];
        return packet;
    }

    public byte[] build(){
        addHeader();
        addUdpPart();
        addPayload();
        System.arraycopy(platformDescBytes, 0, packet, 32, platformDescBytes.length);
        return packet;
    }

    public String parse(byte[] packetData){
        if (packetData.length < 14) return null;
        int offset = 4 /*local*/ + 20 /*ipv4*/ + 8 /* udp */;

        byte[]dataByte = new byte[packetData.length-offset];
        System.arraycopy(packetData,offset,dataByte,0,dataByte.length);
        return new String(dataByte);
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}
