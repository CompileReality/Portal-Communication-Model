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

package NetworkHAL;

import java.util.ArrayList;
import java.util.Date;

public class Server {
    BFInterface Interface;
    BFInterface portalCopy;
    Device server;
    ArrayList<Device> connected;
    ArrayList<Boolean> active;
    ArrayList<Portal> portals;
    long deltaTime;
    long Systime;

    public Server(Device serverDeviceInfo,BFInterface Interface){
        this.Interface = Interface;
        this.portalCopy = Interface;
        server = serverDeviceInfo;
        this.Interface.setDevice(serverDeviceInfo);
    }

    public void exec(){
        ArrayList<String> data = Interface.readTCP();
        if(data.get(1).equals(String.valueOf(BFInterface.PORTAL_GENERATION))){
            if (data.size() == 4){
                Device target1 = connected.get(Integer.parseInt(data.get(0)));
                Device target2 = connected.get(Integer.parseInt(data.get(2)));
                int transferType = Integer.parseInt(data.get(3));
                Portal portal = new Portal(target1,target2,transferType,portalCopy);
                portal.OPEN = true;
                String port = portal.getPORT(this.server);
                ArrayList<String> Data = new ArrayList<>();
                Data.add(this.server.ip);
                Data.add("3");
                Data.add(port);
                Interface.writeTCP(Data,target1);
                Interface.writeTCP(Data,target2);
                portal.exec();
                portals.add(portal);
            }
        }
        else if (data.get(1).equals(String.valueOf(BFInterface.PORTAL_CLOSE))){
            if (data.size()==3){
                int id = -1;
                for (int j = 0; j < portals.size(); j++) {
                    if (data.get(2) == portals.get(j).virtualDevicePortal.port){
                        id = j;
                    }
                }
                if (id != -1){
                    portals.get(id).close();
                    portals.remove(id);
                }
            }
        }
        else if (data.get(1).equals(String.valueOf(BFInterface.PORTAL_UPDATE))) {
            if (data.size() == 6){
                if (data.get(2) == "1"){
                    portals.get(Integer.parseInt(data.get(3))).addClient(connected.get(Integer.parseInt(data.get(4))));
                }else if (data.get(2) == "2"){
                    portals.get(Integer.parseInt(data.get(3))).changeTransferType(Integer.parseInt(data.get(4)));
                }
            }
        }
        else if (data.get(1).equals(String.valueOf(BFInterface.PONG))) {
            active.set(Integer.parseInt(data.get(0)),true);
        }
        else if (data.get(0).equals(String.valueOf(BFInterface.HELLO_MESSAGE))){
            connected.add(Device.fromString(data.get(1)));
            for (int j = 0;j<connected.size();j++){
                ArrayList<String> DATA = new ArrayList<>();
                DATA.add("5");
                DATA.add(String.valueOf(j));
                DATA.add(connected.get(j).name);
                Interface.writeTCP(DATA,connected.get(j));
            }
            active.add(true);
        }

        if (deltaTime >= 10000000000L){ //10 seconds
            ArrayList<Integer> index = new ArrayList<>();
            for(int j = 0;j<connected.size();j++){
                if (!active.get(j)){
                    index.add(j);
                }
            }
            for (int j:index){
                connected.remove(j);
                active.remove(j);
            }
            ArrayList<String> ping = new ArrayList<>();
            ping.add(server.ip);
            ping.add(String.valueOf(BFInterface.PING));
            for(Device client: connected){
                Interface.writeTCP(ping,client);
            }
            active.replaceAll(ignored -> false);
            deltaTime = 0;
            Systime = System.nanoTime();
        }
        else {
            deltaTime = System.nanoTime() - Systime;
        }
    }
}
