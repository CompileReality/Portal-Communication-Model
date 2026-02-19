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

public interface BFInterface {
    int PORTAL_GENERATION = 1;
    int PORTAL_CLOSE = 2;
    int PORTAL_UPDATE = 3;
    int PING = 4;
    int PONG = 5;
    int HELLO_MESSAGE = 6;

    void setDevice(Device current);
    void writeTCP(ArrayList<String> data,Device target);
    ArrayList<String> readTCP();
    void writeUDP(ArrayList<String> data,Device target);
    ArrayList<String> readUDP();
    String getAvailablePort();
    void releasePort(String Port);
}
