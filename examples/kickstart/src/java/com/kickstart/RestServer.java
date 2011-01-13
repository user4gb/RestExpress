package com.kickstart;

import static com.strategicgains.restexpress.RestExpress.JSON_FORMAT;
import static com.strategicgains.restexpress.RestExpress.TXT_FORMAT;
import static com.strategicgains.restexpress.RestExpress.XML_FORMAT;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.bootstrap.ServerBootstrap;

import com.strategicgains.restexpress.console.Console;
import com.strategicgains.restexpress.pipeline.DefaultRequestHandler;
import com.strategicgains.restexpress.pipeline.PipelineBuilder;
import com.strategicgains.restexpress.pipeline.SimpleMessageObserver;
import com.strategicgains.restexpress.route.RouteMapping;
import com.strategicgains.restexpress.route.RouteResolver;
import com.strategicgains.restexpress.route.RoutesDeclaration;
import com.strategicgains.restexpress.serialization.DefaultSerializationResolver;
import com.strategicgains.restexpress.serialization.SerializationProcessor;
import com.strategicgains.restexpress.serialization.json.DefaultJsonProcessor;
import com.strategicgains.restexpress.serialization.text.DefaultTxtProcessor;
import com.strategicgains.restexpress.serialization.xml.DefaultXmlProcessor;
import com.strategicgains.restexpress.util.Bootstraps;
import com.strategicgains.restexpress.util.Resolver;

/**
 * The main entry-point into RestExpress for the example services.
 * 
 * @author toddf
 * @since Aug 31, 2009
 */
public class RestServer
{
	private static final int DEFAULT_PORT = 3330;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		int port = DEFAULT_PORT;

		if (args.length > 0)
		{
			port = Integer.parseInt(args[0]);
		}

		// Configure the server.
		ServerBootstrap bootstrap = Bootstraps.createServerNioBootstrap();

		// Set up the event pipeline factory.
	    DefaultRequestHandler requestHandler = new DefaultRequestHandler(
	    	createRouteResolver(new Routes()),
	    	createSerializationResolver());
	    
	    // Add MessageObservers to the request handler here, if desired...
	    requestHandler.addMessageObserver(new SimpleMessageObserver());

	    // Add pre/post processors to the request handler here...
//	    requestHandler.addPreprocessor(preprocessorHandler);
//	    requestHandler.addPostprocessor(postprocessorHandler);

	    PipelineBuilder pf = new PipelineBuilder()
			.setRequestHandler(requestHandler);
		bootstrap.setPipelineFactory(pf);

		// Bind and start to accept incoming connections.
		System.out.println("Starting KickStart Example Server on port " + port);
		bootstrap.bind(new InetSocketAddress(port));
	}
	
	private static RouteResolver createRouteResolver(RoutesDeclaration routeDeclaration)
	{
		RouteMapping mapping = routeDeclaration.createRouteMapping();
		Console.initialize(routeDeclaration, mapping);
		return new RouteResolver(mapping);
	}

	private static Resolver<SerializationProcessor> createSerializationResolver()
	{
		Map<String, SerializationProcessor> serializationProcessors = new HashMap<String, SerializationProcessor>();
		serializationProcessors.put(JSON_FORMAT, new DefaultJsonProcessor());
		serializationProcessors.put(TXT_FORMAT, new DefaultTxtProcessor());
		serializationProcessors.put(XML_FORMAT, createXmlProcessor());

		return new DefaultSerializationResolver(serializationProcessors, JSON_FORMAT);
	}

	private static SerializationProcessor createXmlProcessor()
	{
		DefaultXmlProcessor xmlProcessor = new DefaultXmlProcessor();
//		xmlProcessor.alias("element_name", Element.class);
//		...
		return xmlProcessor;
	}
}
