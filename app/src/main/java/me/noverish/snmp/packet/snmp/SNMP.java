package me.noverish.snmp.packet.snmp;

import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.asn1.BERSerializable;

import java.io.IOException;
import java.io.OutputStream;

import me.noverish.snmp.packet.pdu.PDU;
import me.noverish.snmp.packet.pdu.PDUVariable;

public class SNMP implements BERSerializable {
    public int version;
    public SNMPCommunity community;
    public PDU pdu;

    public SNMP() {

    }

    public SNMP(int version, SNMPCommunity community, PDU pdu) {
        this.version = version;
        this.community = community;
        this.pdu = pdu;
    }

    // BERSerializable
    @Override
    public void encodeBER(OutputStream os) throws IOException {
        int payloadLength = getBERPayloadLength();

        BER.encodeHeader(os, BER.SEQUENCE, payloadLength);
        BER.encodeInteger(os, BER.INTEGER, version);
        community.encodeBER(os);
        pdu.encodeBER(os);
    }

    @Override
    public void decodeBER(BERInputStream is) throws IOException {
        BER.decodeHeader(is, new BER.MutableByte());

        version = BER.decodeInteger(is, new BER.MutableByte());

        community = new SNMPCommunity();
        community.decodeBER(is);

        pdu = new PDU();
        pdu.decodeBER(is);
    }

    @Override
    public int getBERLength() {
        int payloadLength = getBERPayloadLength();
        return payloadLength + BER.getBERLengthOfLength(payloadLength) + 1;
    }

    @Override
    public int getBERPayloadLength() {
        int length = pdu.getBERLength();
        length += community.getBERLength();
        length += 3;
        return length;
    }


    // toString
    @Override
    public String toString() {
        return "{\n" +
                "  \"version\": " + version + "\n" +
                "  \"community\": " + community.value + "\n" +
                pdu.toString() +
                "}";
    }

    public String toSimpleString() {
        PDUVariable variable = pdu.variables.get(0);
        String oid = variable.oid.toString();
        String value = variable.value.toString();

        return oid + " = " + value;
    }
}
