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
import com.corundumstudio.socketio.messages.XHRNewChannelMessage;
import com.corundumstudio.socketio.messages.XHRPacketMessage;
import com.corundumstudio.socketio.parser.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class XHRPollingClient extends BaseClient {

    private static final Logger log = LoggerFactory.getLogger(XHRPollingClient.class);

    private String origin;

    public XHRPollingClient(AckManager ackManager, DisconnectableHub disconnectable, UUID sessionId, Transport transport) {
        super(sessionId, ackManager, disconnectable, transport);
    }

    public void bindChannel(Channel channel, String origin) {
        log.trace("Binding new channel");
        this.origin = origin;
        setChannel(channel);
        log.trace("Sending new channel message");
        channel.write(new XHRNewChannelMessage(origin, getSessionId()));
    }

    public String getOrigin() {
        return origin;
    }

    public ChannelFuture send(Packet packet) {
        log.trace("Sending data");
        return getChannel().write(new XHRPacketMessage(getSessionId(), origin, packet));
    }

}
