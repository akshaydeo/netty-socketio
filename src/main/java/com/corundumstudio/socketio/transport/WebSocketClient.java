/**
 * Copyright 2012 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corundumstudio.socketio.transport;

import com.corundumstudio.socketio.DisconnectableHub;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.ack.AckManager;
import com.corundumstudio.socketio.messages.WebSocketPacketMessage;
import com.corundumstudio.socketio.parser.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WebSocketClient extends BaseClient {

    public WebSocketClient(Channel channel, AckManager ackManager,
                            DisconnectableHub disconnectable, UUID sessionId,
                             Transport transport, Map<String, List<String>> params) {
        super(sessionId, ackManager, disconnectable, transport,params);
        setChannel(channel);
    }

    public ChannelFuture send(Packet packet) {
        return getChannel().write(new WebSocketPacketMessage(getSessionId(), packet));
    }

}
