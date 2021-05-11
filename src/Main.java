import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.server.ExportException;
import java.util.HashSet;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) throws Exception {
        int simDur = 120000;
        Network net = simulate(simDur, 1);
        //Thread.sleep(20000);
        stopSimulate(net);
        //System.out.println("Data AP got: "+ net.AP.buffer.size());
        //System.out.println("Acks AP got:" + +net.AP.ctrl_buffer.size());
        //System.out.println("Data station got: "+ (net.devices.getFirst().buffer.size()));
        //System.out.println("Acks station got:" + net.devices.getFirst().ctrl_buffer.size());
        System.out.println("For Num Retries = 1");
        System.out.println("Lost packets of AP: "+ net.AP.lostbuffer.size());
        System.out.println("Lost packets of device: "+ net.devices.getFirst().lostbuffer.size());
        double lossPer = (((net.AP.lostbuffer.size()+net.devices.getFirst().lostbuffer.size())/(2*(simDur/1000)*2)));
        System.out.println("Success Percentage:"+(1-lossPer)*100);
        System.out.println(" ");

        net = simulate(simDur, 4);
        stopSimulate(net);
        System.out.println("For Num Retries = 4");
        System.out.println("Lost packets of AP: "+ net.AP.lostbuffer.size());
        System.out.println("Lost packets of device: "+ net.devices.getFirst().lostbuffer.size());
        lossPer = (((net.AP.lostbuffer.size()+net.devices.getFirst().lostbuffer.size())/(2*(simDur/1000)*2)));
        System.out.println("Success Percentage:"+(1-lossPer)*100);
        System.out.println(" ");

        net = simulate(simDur, 7);
        stopSimulate(net);
        System.out.println("For Num Retries = 7");
        System.out.println("Lost packets of AP: "+ net.AP.lostbuffer.size());
        System.out.println("Lost packets of device: "+ net.devices.getFirst().lostbuffer.size());
        lossPer = (((net.AP.lostbuffer.size()+net.devices.getFirst().lostbuffer.size())/(2*(simDur/1000)*2)));
        System.out.println("Success Percentage:"+(1-lossPer)*100);
        System.out.println(" ");

        net = simulate(simDur, 14);
        stopSimulate(net);
        System.out.println("For Num Retries = 14");
        System.out.println("Lost packets of AP: "+ net.AP.lostbuffer.size());
        System.out.println("Lost packets of device: "+ net.devices.getFirst().lostbuffer.size());
        lossPer = ((net.AP.lostbuffer.size()+net.devices.getFirst().lostbuffer.size())/(2*(simDur/1000)*2));
        System.out.println("Success Percentage:"+(1-lossPer)*100);
        System.out.println(" ");

        //writePackets(net.devices.getFirst());
        //writePackets(net.AP);

    }

    //simulationDuration is in milliseconds
    public static Network simulate(int simulationDuration, int numRet) throws InterruptedException { //testing function, for now running communication from a station (device) and an AP
        HashSet<Standard> stans = new HashSet<>();
        LinkedList<Double> rates = new LinkedList<>();
        rates.add(1.0);
        rates.add(2.0);
        rates.add(2.0);
        stans.add(new Standard(Name.N));
        //reasonable timeout would be ~ packet_sending_duration + ack_sending_duration ~ 200,000 + 50,000
        //timeout = SIFS + ACK_DUR + SLOT_TIME
        Network net = new Network("home", "22:55:66:88:77:99", new Standard(Name.N), rates, 210000, simulationDuration);
        Device dev1 = net.createDevice("lilach_phone", "82:11:35:46:FE:19", rates, new Standard(Name.N), 210000, net.AP, simulationDuration);
        net.addDevice(dev1, 0.95);
        net.AP.setDestination(dev1);
        dev1.preparePackets();
        net.AP.preparePackets();
        dev1.setNumRetries(numRet);
        net.AP.setNumRetries(numRet);
        //Note: there seems to be some bias in the loss percentage from the AP, the device is less likely to get packets
        //that's because the probability that the probability the destination get a packet is (1-plpTo)
        //however, the probability that the source get an ACK on it is (1-plpTo)(1-plpFrom), which is smaller!
        //for this reason, setting plpFrom to 0.1 and plpTo to 0.5 make sense

        net.AP.startSending();
        dev1.startSending();

        Thread.sleep(simulationDuration);

        dev1.stopSending();
        net.AP.stopSending();



         /*
        LinkedList<Double> rates2 = new LinkedList<>();
        rates2.add(1.0);
        rates2.add(2.0);
        rates2.add(5.0);
        rates2.add(11.0);
        Device dev2 = net.createDevice("lilach_computer", "12:11:45:46:FE:87", rates2, stans);
        net.addDevice(dev2, 0.5, 0.2);
         */
        //for now:
        /*
        dev1.connected_devs.put(dev2, dev1.connected_devs.get(net.getAP()));
        dev2.connected_devs.put(dev1, dev2.connected_devs.get(net.getAP()));
        Channel ch1 = net.addConnection(dev1, dev2, 0.2, 1000, 11);
        Channel ch2 = net.addConnection(dev2, dev1, 0.4, 1000, 11);
        dev1.addConnectedDev(dev2, ch1);
        dev2.addConnectedDev(dev1, ch2);
        dev1.addListener(dev2, ch1);
        dev2.addListener(dev1, ch2);
        net.channels.add(ch1);
        net.channels.add(ch2);
        net.world.put(new Pair<>(dev1, dev2), ch1);
        net.world.put(new Pair<>(dev2, dev1), ch2);
         */

        //dev2.startSending();
/*
        dev1.startSending();
        Thread.sleep(15000);
        dev1.stopSending();
        Thread.sleep(10000);
        net.AP.startSending();
        Thread.sleep(15000);
        net.AP.stopSending();

*/


        /*net.AP.startSending();
        Thread.sleep(5000);
        net.AP.stopSending();
        dev1.startSending();
        Thread.sleep(5000);

         */

        //Thread.sleep(50000);
        //net.AP.startSending();


        //System.out.println("dev1 got: "+dev1.buffer.size());

        return net;

    }

    public static void stopSimulate(Network net)
    {
        for (Medium med : net.getMediums()){
            med.stop();
        }
        for (Device dev : net.getDevices()){
            dev.stopSending();
        }
        for (Pair<Device, Device> p : net.getWorld().keySet()){
            p.getKey().stopSending();
            p.getValue().stopSending();
        }

        net.getAP().stopSending();
    }

    public static void writePackets(Device dev) throws Exception
    {
        /*try (Writer writer = new FileWriter("Output"+dev.toString()+".json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(dev.buffer, writer);
        }

         */
        Gson gson = new Gson();
        try {
            Writer file = new FileWriter("C:\\Users\\Home\\.IntelliJIdea2019.2\\Projects\\OutputPackets\\"+dev.toString());
            while (!dev.getBuffer().isEmpty()){
                String json = gson.toJson(dev.getBuffer().remove());
                file.write(json);
            }
            while (!dev.ctrl_buffer.isEmpty()){
                String json = gson.toJson(dev.ctrl_buffer.remove());
                file.write(json);
            }
            file.flush();
            file.close();

        } catch (IOException ex) {

        }
       /* Gson gson = new Gson();

        //JSONObject packetObject = new JSONObject();
        for(Packet p : dev.getBuffer()) {
            //packetObject.put("Packet", p);
            gson.toJson(p);
        }

        Files.write(Paths.get("C:\\Users\\Home\\.IntelliJIdea2019.2\\Projects\\OutputPackets\\"+dev.toString()), packetObject.toJSONString().getBytes());

        */
    }
/*
        FileOutputStream f = null;
        try {
            f = new FileOutputStream(new File("PacketsArrivedTO"+dev.toString()+".txt"));
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write packets the input device got to file
            for(Packet p : dev.getBuffer()) {
                o.writeObject(p);
            }

            o.close();
            f.close();
        }

        catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
            e.printStackTrace();
        }
    }

 */
}
