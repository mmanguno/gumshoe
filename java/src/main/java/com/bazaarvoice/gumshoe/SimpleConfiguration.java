package com.bazaarvoice.gumshoe;

/**
 * Configuration POJO to allow specification of following extension points:
 *
 * - Filter used to filter out events emitted
 * - Decorator used to decorate all events with global data
 * - Publisher used to publish events in some fashion.
 *
 * @author lance.woodson
 *
 */
public class SimpleConfiguration implements Configuration {
    private String applicationName;
    private Filter filter;
    private Decorator decorator;
    private Publisher publisher;
    private int maxContextStackDepth;

    /**
     * Create a new SimpleConfiguration for the application using a
     * NoOpFilter, SimpleDecorator and PrintStreamPublisher to publish
     * events to standard out.  A maximum context stack depth of 50
     * will be enforced.
     *
     * @param applicationName
     */
    public SimpleConfiguration(String applicationName) {
        this.applicationName = applicationName;
        this.filter = new NoOpFilter();
        this.decorator = new SimpleDecorator(applicationName);
        this.publisher = new PrintStreamPublisher();
        this.maxContextStackDepth = 50;
    }

    /**
     * Create a new SimpleConfiguration for the application using
     * a NoOpFilter, SimpleDecorator and EventLogPublisher that publishes
     * events to the file at the specified path.  A max context stack depth
     * of 50 will be enforced.
     *
     * @param applicationName
     * @param logPath
     */
    public SimpleConfiguration(String applicationName, String logPath) {
        this.applicationName = applicationName;
        this.filter = new NoOpFilter();
        this.decorator = new SimpleDecorator(applicationName);
        this.publisher = new EventLogPublisher(logPath);
        this.maxContextStackDepth = 50;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Decorator getDecorator() {
        return decorator;
    }

    public void setDecorator(Decorator decorator) {
        this.decorator = decorator;
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public int getMaxContextStackDepth() {
        return maxContextStackDepth;
    }

    public void setMaxContextStackDepth(int maxContentStackDepth) {
        this.maxContextStackDepth = maxContentStackDepth;
    }
}
