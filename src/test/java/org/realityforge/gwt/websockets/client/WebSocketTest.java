package org.realityforge.gwt.websockets.client;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.realityforge.gwt.websockets.client.event.CloseEvent;
import org.realityforge.gwt.websockets.client.event.ErrorEvent;
import org.realityforge.gwt.websockets.client.event.MessageEvent;
import org.realityforge.gwt.websockets.client.event.OpenEvent;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class WebSocketTest
{
  @Test
  public void registryTest()
  {
    assertNull( WebSocket.newWebSocketIfSupported() );
    assertFalse( WebSocket.isSupported() );
    final TestWebSocket.Factory factory = new TestWebSocket.Factory();
    WebSocket.register( factory );
    assertNotNull( WebSocket.newWebSocketIfSupported() );
    assertTrue( WebSocket.isSupported() );
    assertTrue( WebSocket.deregister( factory ) );
    assertNull( WebSocket.newWebSocketIfSupported() );
    assertFalse( WebSocket.isSupported() );
    assertFalse( WebSocket.deregister( factory ) );
  }

  @Test
  public void handlerInteractions()
  {
    final TestWebSocket webSocket = new TestWebSocket( new SimpleEventBus() );

    {
      final OpenEvent.Handler handler = mock( OpenEvent.Handler.class );
      final HandlerRegistration registration = webSocket.addOpenHandler( handler );
      webSocket.onOpen();
      verify( handler, only() ).onOpenEvent( any( OpenEvent.class ) );
      registration.removeHandler();
      webSocket.onOpen();
      verify( handler, atMost( 1 ) ).onOpenEvent( any( OpenEvent.class ) );
    }

    {
      final CloseEvent.Handler handler = mock( CloseEvent.Handler.class );
      final HandlerRegistration registration = webSocket.addCloseHandler( handler );
      webSocket.onClose( true, CloseEvent.CLOSE_NORMAL, null );
      final CloseEvent expected = new CloseEvent( webSocket, true, CloseEvent.CLOSE_NORMAL, null );
      verify( handler, only() ).onCloseEvent( refEq( expected, "source" ) );
      registration.removeHandler();
      webSocket.onClose( true, CloseEvent.CLOSE_NORMAL, null );
      verify( handler, atMost( 1 ) ).onCloseEvent( any( CloseEvent.class ) );
    }

    {
      final MessageEvent.Handler handler = mock( MessageEvent.Handler.class );
      final HandlerRegistration registration = webSocket.addMessageHandler( handler );
      webSocket.onMessage( "Blah" );
      final MessageEvent expected = new MessageEvent( webSocket, "Blah" );
      verify( handler, only() ).onMessageEvent( refEq( expected, "source" ) );
      registration.removeHandler();
      webSocket.onMessage( "Blah" );
      verify( handler, atMost( 1 ) ).onMessageEvent( any( MessageEvent.class ) );
    }

    {
      final MessageEvent.Handler handler = mock( MessageEvent.Handler.class );
      final HandlerRegistration registration = webSocket.addMessageHandler( handler );
      final ArrayBuffer arrayBuffer = mock( ArrayBuffer.class );
      webSocket.onMessage( arrayBuffer );
      final MessageEvent expected = new MessageEvent( webSocket, arrayBuffer );
      verify( handler, only() ).onMessageEvent( refEq( expected, "source" ) );
      registration.removeHandler();
      webSocket.onMessage( arrayBuffer );
      verify( handler, atMost( 1 ) ).onMessageEvent( any( MessageEvent.class ) );
    }

    {
      final ErrorEvent.Handler handler = mock( ErrorEvent.Handler.class );
      final HandlerRegistration registration = webSocket.addErrorHandler( handler );
      webSocket.onError();
      verify( handler, only() ).onErrorEvent( any( ErrorEvent.class ) );
      registration.removeHandler();
      webSocket.onError();
      verify( handler, atMost( 1 ) ).onErrorEvent( any( ErrorEvent.class ) );
    }
  }
}