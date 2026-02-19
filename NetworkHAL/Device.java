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

public class Device {
    public String ip;
    public String port;
    public String name;

    @Override
    public String toString() {
        return ip + "&" + port + "&" + name;
    }

    public static Device fromString(String data){
        Device device = new Device();
        device.ip = data.substring(0,data.indexOf("&"));
        data = data.substring(data.indexOf("&"));
        device.port  = data.substring(0,data.indexOf("&"));
        data = data.substring(data.indexOf("&"));
        device.name = data;
        return device;
    }
}
