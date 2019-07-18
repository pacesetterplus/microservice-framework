package uk.gov.justice.services.jmx.api.command;

public class ShutterSystemCommand extends BaseSystemCommand {

    public static final String SHUTTER = "SHUTTER";
    private static final String DESCRIPTION = "Shutters the application to allow for maintenance.";

    public ShutterSystemCommand() {
        super(SHUTTER, DESCRIPTION);
    }
}
