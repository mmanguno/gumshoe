package com.bazaarvoice.gumshoe;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ContextTest extends Assert {
    private EventFactory eventFactory;
    private EventHandler eventHandler;
    private Context context;
    private Context secondContext;

    @BeforeMethod
    public void setUp() {
        eventFactory = new EventFactory();
        eventHandler = mock(EventHandler.class);
        context = new Context("context one", eventFactory, eventHandler);
        secondContext = new Context("context two", context);
    }

    @Test
    public void ensureContextCreationAssignsStreamId() {
        assertNotNull(context.getStreamId());
    }

    @Test
    public void ensureContextCreationSetsContext() {
        assertEquals(context.data.get("context"), Arrays.asList("context one"));
    }

    @Test
    public void ensureContextCreatedWithOtherContextPropagatesStreamId() {
        assertEquals(secondContext.getStreamId(), context.getStreamId());
    }

    @Test
    public void ensureContextCreatedWithOtherContextAppendsNameToContext() {
        assertEquals(secondContext.data.get("context"), Arrays.asList("context one", "context two"));
    }

    @Test
    public void ensureNewContextIsInCreatedStatus() {
        assertEquals(context.getStatus(), Context.Status.CREATED);
    }

    @Test
    public void ensureContextThatHasHadStartCalledIsInStartedStatus() {
        context.start();
        assertEquals(context.getStatus(), Context.Status.STARTED);
    }

    @Test
    public void ensureContextThatHasHadFinishCalledIsInFinishedStatus() {
        context.start();
        context.finish();
        assertEquals(context.getStatus(), Context.Status.FINISHED);
    }

    @Test
    public void ensureContextThatHasHadFailCalledIsInFinishedStatus() {
        context.start();
        context.fail();
        assertEquals(context.getStatus(), Context.Status.FINISHED);
    }

    @Test
    public void ensurePutStoresDataForContext() {
        context.put("foo", "bar");
        assertEquals(context.data.get("foo"), "bar");
    }

    @Test(expectedExceptions={IllegalStateException.class})
    public void ensurePutWhenInFinishedStatusFails() {
        context.start();
        context.finish();
        context.put("foo", "bar");
    }

    @Test
    public void ensureStartSendsStartedEventToDispatcher() {
        context.start();

        verify(eventHandler).handle(buildExpectedEvent(context, "started"));
    }

    @Test
    public void ensureStartLoadsEventFactorysDataStack() {
        context.put("foo", "bar").start();

        assertEquals(eventFactory.getDataStack().get("foo"), "bar");
    }

    @Test
    public void ensureFinishSendsFinishedEventToDispatcher() {
        context.start();
        context.finish();

        verify(eventHandler).handle(buildExpectedEvent(context, "started"));
        verify(eventHandler).handle(buildExpectedEvent(context, "finished"));
    }

    @Test
    public void ensureFinishUnloadsEventFactoryDataStack() {
        context.put("foo", "bar").start().finish();

        assertEquals(eventFactory.getDataStack().get("foo"), null);
    }

    @Test
    public void ensureFailSendsFinishedEventToDispatcher() {
        context.start();
        context.fail();

        verify(eventHandler).handle(buildExpectedEvent(context, "started"));
        verify(eventHandler).handle(buildExpectedEvent(context, "failed"));
    }

    @Test
    public void ensureFailUnloadsEventFactoryDataStack() {
        context.put("foo", "bar").start().fail();

        assertEquals(eventFactory.getDataStack().get("foo"), null);
    }

    private Map<String, Object> buildExpectedEvent(Context context, String type) {
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("stream_id", context.getStreamId().toString());
        event.put("context", Arrays.asList(context.getName()));
        event.put("type", type);

        if (type.equals("finished") || type.equals("failed")) {
            event.put("execution_time", context.getFinishedTime() - context.getStartTime());
        }

        return event;
    }
}
