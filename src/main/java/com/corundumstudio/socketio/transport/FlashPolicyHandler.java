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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class FlashPolicyHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(FlashPolicyHandler.class);

    private final String requestBuffer = "<policy-file-request/>";

    private final String responseBuffer =
            "<?xml version=\"1.0\"?>"
                    + "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">"
                    + "<cross-domain-policy> "
                    + "   <site-control permitted-cross-domain-policies=\"master-only\"/>"
                    + "   <allow-access-from domain=\"*\" to-ports=\"*\" />"
                    + "</cross-domain-policy>";

    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
        log.trace("inside flash policy handler");
        if (msg instanceof ByteBuf) {
            ByteBuf message = (ByteBuf) msg;
            ByteBuf data = message.slice(0, requestBuffer.getBytes().length);
            if (data.equals(requestBuffer)) {
                message.release();
                ChannelFuture f = ctx.writeAndFlush(responseBuffer.getBytes());
                f.addListener(ChannelFutureListener.CLOSE);
                return;
            }
            ctx.pipeline().remove(this);
        }
        ctx.fireChannelRead(msg);
    }

}
