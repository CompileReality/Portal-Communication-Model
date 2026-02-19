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

public class Portal {
    volatile int transferType;
    volatile ArrayList<Device> devices;
    volatile BFInterface interf;
    public Device virtualDevicePortal;
    public volatile boolean OPEN;
    volatile boolean TypeChange;

    public Portal(Device client1,Device client2,int transferType,BFInterface interfcopy){
        devices = new ArrayList<>();
        devices.add(client1);
        devices.add(client2);
        this.transferType = transferType;
        this.interf = interfcopy;
    }

    public String getPORT(Device server){
        Device device = new Device();
        device.port = interf.getAvailablePort();
        device.ip = server.ip;
        virtualDevicePortal = device;
        interf.setDevice(device);
        return device.port;
    }

    public void addClient(Device client){
        devices.add(client);
    }

    public void changeTransferType(int type){
        if(transferType != type){
            TypeChange = true;
        }
    }

    public void exec(){
        new Thread(){
            @Override
            public void run() {
                while(OPEN) {
                    if (TypeChange){
                        ArrayList<String> data = new ArrayList<>();
                        data.add(virtualDevicePortal.ip);
                        data.add("1");
                        if (transferType == 1){
                            data.add("2");
                            for (Device client:devices) {
                                interf.writeTCP(data,client);
                            }
                            transferType = 2;
                        }else if (transferType == 2){
                            data.add("1");
                            for (Device client:devices) {
                                interf.writeUDP(data,client);
                            }
                            transferType = 1;
                        }
                    }
                    if (transferType == 1) {
                        TCP();
                    } else {
                        UDP();
                    }
                }
            }
        }.start();
    }

    public void UDP(){
        ArrayList<String> DATA = interf.readUDP();
        for (Device client:devices) {
            if (client.ip == DATA.get(0)){
                continue;
            }
            interf.writeUDP(DATA,client);
        }
    }

    public void TCP(){
        ArrayList<String> DATA = interf.readTCP();
        for (Device client:devices) {
            if (client.ip == DATA.get(0)){
                continue;
            }
            interf.writeTCP(DATA,client);
        }
    }

    public void close(){
        ArrayList<String> DATA = new ArrayList<>();
        DATA.add(virtualDevicePortal.ip);
        DATA.add("6");
        if (transferType == 1){
            for (Device client:devices) {
                interf.writeTCP(DATA,client);
            }
        }else{
            for (Device client:devices) {
                interf.writeUDP(DATA,client);
            }
        }
        interf.releasePort(virtualDevicePortal.port);
    }
}
