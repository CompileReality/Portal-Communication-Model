# Portal Communication Model
Safe and fast model for server-client network model which uses "Portal" for client-client communication without interruption by server , reducing load on both server and "Portal" 

This Model is independent of Platform on which you test, and Framework you use. So you can use it anywhere, with any device connected to each other. This is acheived due to BFInterface (Basic function interface).

## Basic Function Interface
BFInterface helps in adapting to different Platform. This interface provides necessary function to write this whole model.This model can be used by making correct custom class which implements this interface and overwrites all functions based on Platform to be used on.

## Device
Device class acts as identity for all real devices and virtual devices (here virtual device means a client/portal/server having same IP but different ports, they can be seperated by assuming they all are different devices even if they have same IP).
For example, If server is on one device and it created a portal with same IP but different port then that portal has to create completely new instance of Device class.


## Portal
Portal handles client to client communication without exposing each other completely. Portal is just like a server which handles only there communication without taking responsibility of watching whether all client is active.

Portal listens on same IP as that of server but on different port. So, every portal on server has different port but same IP. This allows access of portal to only clients which know its port, and thus this makes it safe and fast.

One Portal can have multiple clients but atleast 2 clients is required.If one client sent a message then, portal will send it all clients connected to this portal except one which sent it.

One client can join/connect to multiple portal at once, but requires custom made client code file because the client code provided is just an example for how to use it.

Portal class inside NetworkHAL (i.e. Network Hardware Abstraction Layer) controls all working of portal.

Data/Message can be transfered using two allowed method:
- TCP
- UDP

One Portal can only transfer message in one method at a time. Transfer type is set by client which is sending data/message and portal automatically notifes all clients to switch transfer method. This notification message is sent in previously set transfer type.
For example, there's client C1 which wants send a message "Hello" but want to use UDP instead of TCP. So, C1 will send message to server to change the transefer type from TCP to UDP of portal on port XXXX. Then server will notify portal on port XXXX to change it's transfer type.Now, portal will first send message to change their receive type to all client(including C1, reason behind this is as C1 gets conformation of change), then portal changes it's transfer type. Then, C1 can send message "Hello" using UDP.  

Portal's message handling part (i.e. "exec" method in Portal.java inside NetworkHAL package) is ran on different thread as server can do it's task parallel to portal.

An instance of Portal can be created by server only. because if client created an instance of Portal then IP of portal and client will be same and that makes IP to leak to all clients connected to portal.

When portal is closed it releases it's port after notifying all it's client that portal is closed now.

## Server Design
Server is designed to handle active clients and their portals only. Server uses PING-PONG method to check if client is active or not, every 10 seconds. If it doesn't respond then it is disconnected directly by server.

Every 10 seconds, Server sends PING messages to all clients even if it is not connected to any portal. Then every client has to respond with PONG message if it is active. If it doesn't then either client has shutdown or is not in range, so server disconnects that client before resending the PING message. Client has 10 seconds of interval to respond with PONG message.

If new client is joined in between that interval, then temporarly it's active flag is set 'true'. When timer is refreshed and PING is to be send then new client's active flag is set to 'false' even if it's time passed after connecting to server is less than 10 seconds, and PING message is send to all client.

Server also creates new portal, update it or close it whenever needed. Client can send message to server to create,update,close a portal, and server responds with output if needed.

When Client asks to create a Portal and add another client in portal then the client which wants to create portal has to provide some information about another client to identify correct one. So, when any client joins the server, the server sends all device's name and id (Including this client also. So that, client can get their id also).Before sending, this client has to send a Hello message with it's name. Using this id it can identify correct one. And that all communication with server is done via TCP only.

## Advantages of using this model
- Lightweight
- Safe
- Fast
- Platform and Framework Independent
- Multiple types of transport protocol

## Server Client Layer Protocol
While communicating with server all client must follow this portocol else server won't identify that as client and just ignore that message. This protocol contains three parts in following order:
- ID
- Command id
- Payload

The Payload is again has parts depending on message type. ID is index of client itself in list sent by server when it joined the server. Command id is id of command to perform any action by server, like opening,closing,updating a portal or Hello message, etc.

Here's all type of Command:
- `PORTAL_GENERATION` : Payload has in total 2 size

    - ID of client 2
    - transfer type

    After sending this 
- `PORTAL_CLOSE` : Payload has in total 
- `PORTAL_UPDATE`
- `PING`
- `PONG`
- `HELLO_MESSAGE`

