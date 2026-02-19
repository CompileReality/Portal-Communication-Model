/*
 * Copyright 2025 Prathmesh Sanjay Kumbhar
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import NetworkHAL.BFInterface;
import NetworkHAL.Device;

import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    volatile BFInterface interf;
    volatile int transferType;
    volatile boolean isPortalActive = false;
    Device current,server,portal;
    volatile HashMap<Integer,String> availableDevices = new HashMap<>();
    volatile int CurrentID;
    volatile boolean isClientActive;

    public Client(BFInterface interf,Device current,Device server){
        this.interf = interf;
        this.transferType = 1;
        this.current = current;
        this.server = server;
        interf.setDevice(current);
        ArrayList<String> data = new ArrayList<>();
        data.add(String.valueOf(BFInterface.HELLO_MESSAGE));
        data.add(current.toString());
        isClientActive = true;
        while(isClientActive){
            exec();
        }
    }

    public void exec(){
        ArrayList<String> data = getData();
        switch (Integer.parseInt(data.get(1))){
            case 3:
                portal = new Device();
                portal.ip = server.ip;
                portal.port = data.get(2);
                isPortalActive = true;
                break;
            case 5:
                if (data.get(3) == current.name){
                    CurrentID = Integer.parseInt(data.get(2));
                    break;
                }
                availableDevices.put(Integer.parseInt(data.get(2)),data.get(3));
                break;
            case BFInterface.PING:
                ArrayList<String> pong = new ArrayList<>();
                pong.add(String.valueOf(CurrentID));
                pong.add(String.valueOf(BFInterface.PONG));
                sendData(pong,server);
                break;
            default:
                processData(data);
        }

        if (!isPortalActive){
            portal = null;
            ArrayList<String> Data = new ArrayList<>();
            Data.add(String.valueOf(CurrentID));
            Data.add(String.valueOf(BFInterface.PORTAL_CLOSE));
            Data.add(portal.port);
            sendData(Data,server);
        }
    }

    public void processData(ArrayList<String> data){}

    public ArrayList<String> getData(){
        if (transferType == 1){
            return interf.readTCP();
        }else if (transferType == 2){
            return interf.readUDP();
        }
        return null;
    }

    public void sendData(ArrayList<String> data,Device target){
        if (transferType == 1){
            interf.writeTCP(data,target);
        }else if (transferType == 2){
            interf.writeUDP(data,target);
        }
    }
}
