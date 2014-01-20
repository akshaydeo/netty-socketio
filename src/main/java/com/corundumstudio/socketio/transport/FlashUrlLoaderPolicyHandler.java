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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Flash policy handler for UrlLoaders
 * Author: akshay
 * Date  : 1/20/14
 * Time  : 6:11 AM
 */
@ChannelHandler.Sharable
public final class FlashUrlLoaderPolicyHandler extends ChannelInboundHandlerAdapter {
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(FlashUrlLoaderPolicyHandler.class);
    /**
     * Crossdomain
     */
    private final ByteBuf crossdomain = Unpooled.copiedBuffer(""
            + "<?xml version=\"1.0\" ?>\n"
            + "<!DOCTYPE cross-domain-policy SYSTEM \"http://www.adobe.com/xml/dtds/cross-domain-policy" +
            ".dtd\">\n"
            + "<cross-domain-policy>\n"
            + "    <allow-access-from domain=\"*\" to-ports=\"*\" secure=\"false\"/>"
            + "    <allow-access-from domain=\"d2xclp3ege6hxd.cloudfront.net\" to-ports=\"*\" />\n"
            + "    <allow-access-from domain=\"da1e79qj82tlx.cloudfront.net\" to-ports=\"*\" />\n"
            + "</cross-domain-policy>", CharsetUtil.UTF_8);


    /**
     * Channel read callback
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead (final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            final HttpRequest request = (HttpRequest) msg;
            if (request.getUri().contains("crossdomain.xml")) {
                writeCrossdomainDotXml(ctx, request);
                log.trace("policy request");
            }
            ctx.pipeline().remove(this);
            return;
        }
        ctx.fireChannelRead(msg);
    }

    /**
     * Method to write back crossdomain xml
     *
     * @param request
     */
    private void writeCrossdomainDotXml (final ChannelHandlerContext ctx, final HttpRequest request) {
        boolean keepAlive = isKeepAlive(request);

        HttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
                Unpooled.copiedBuffer(crossdomain));
        response.headers().set(CONTENT_TYPE, "text/xml");
        response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        if (keepAlive) {
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            response.headers().set(HttpHeaders.Values.KEEP_ALIVE, "timeout=2,max=5");
        }
        // Write the response.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
