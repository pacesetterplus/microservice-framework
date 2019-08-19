package uk.gov.justice.services.management.shuttering.handler;

import static java.lang.String.format;
import static uk.gov.justice.services.jmx.api.command.ShutterSystemCommand.SHUTTER;
import static uk.gov.justice.services.jmx.api.command.UnshutterSystemCommand.UNSHUTTER;
import static uk.gov.justice.services.jmx.api.state.ApplicationManagementState.SHUTTERED;
import static uk.gov.justice.services.jmx.api.state.ApplicationManagementState.SHUTTERING_IN_PROGRESS;
import static uk.gov.justice.services.jmx.api.state.ApplicationManagementState.UNSHUTTERED;
import static uk.gov.justice.services.jmx.api.state.ApplicationManagementState.UNSHUTTERING_IN_PROGRESS;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.jmx.api.command.ShutterSystemCommand;
import uk.gov.justice.services.jmx.api.command.UnshutterSystemCommand;
import uk.gov.justice.services.jmx.api.state.ApplicationManagementState;
import uk.gov.justice.services.jmx.command.ApplicationManagementStateRegistry;
import uk.gov.justice.services.jmx.command.HandlesSystemCommand;
import uk.gov.justice.services.management.shuttering.events.ShutteringRequestedEvent;
import uk.gov.justice.services.management.shuttering.events.UnshutteringRequestedEvent;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

public class ShutteringSystemCommandHandler {

    @Inject
    private UtcClock clock;

    @Inject
    private Event<ShutteringRequestedEvent> shutteringRequestedEventFirer;

    @Inject
    private Event<UnshutteringRequestedEvent> unshutteringRequestedEventFirer;

    @Inject
    private ApplicationManagementStateRegistry applicationManagementStateRegistry;

    @Inject
    private Logger logger;

    @HandlesSystemCommand(SHUTTER)
    public void onShutterRequested(final ShutterSystemCommand shutterSystemCommand) {
        final ApplicationManagementState shutteredState = applicationManagementStateRegistry.getApplicationManagementState();
        if(shutteredState == UNSHUTTERED) {
            applicationManagementStateRegistry.setApplicationManagementState(SHUTTERING_IN_PROGRESS);
            shutteringRequestedEventFirer.fire(new ShutteringRequestedEvent(shutterSystemCommand, clock.now()));
        } else {
            logger.info(format("Ignoring command '%s'. Context shuttered state is '%s'", shutterSystemCommand, shutteredState));
        }
    }

    @HandlesSystemCommand(UNSHUTTER)
    public void onUnshutterRequested(final UnshutterSystemCommand unshutterSystemCommand) {
        final ApplicationManagementState shutteredState = applicationManagementStateRegistry.getApplicationManagementState();
        if(shutteredState == SHUTTERED) {
            applicationManagementStateRegistry.setApplicationManagementState(UNSHUTTERING_IN_PROGRESS);
            unshutteringRequestedEventFirer.fire(new UnshutteringRequestedEvent(unshutterSystemCommand, clock.now()));
        } else {
            logger.info(format("Ignoring command '%s'. Context shuttered state is '%s'", unshutterSystemCommand, shutteredState));
        }
    }
}