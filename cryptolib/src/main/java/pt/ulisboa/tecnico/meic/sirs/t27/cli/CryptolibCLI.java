package main.java.pt.ulisboa.tecnico.meic.sirs.t27.cli;

import main.java.pt.ulisboa.tecnico.meic.sirs.t27.cli.commands.*;

public class CryptolibCLI {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            HelpCommand.main();
            return;
        }

        final String command = args[0];
        final String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);

        switch (command) {
            case "help":
                HelpCommand.main();
                break;
            case "protect":
                ProtectCommand.main(newArgs);
                break;
            case "unprotect":
                UnprotectCommand.main(newArgs);
                break;
            case "check":
                CheckCommand.main(newArgs);
                break;
            case "sign":
                SignCommand.main(newArgs);
                break;
            default:
                break;
        }
    }
}
