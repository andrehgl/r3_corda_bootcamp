package java_bootcamp;

import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract{
    public static String ID = "java_bootcamp.TokenContract";

    public interface Commands extends CommandData {
        class Issue implements Commands { }
        class Transfer implements Commands { }
        class Exit implements Commands { }
    }

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {

        CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);

        if (command.getValue() instanceof Commands.Issue) {
            if (tx.getInputStates().size() != 0) throw new IllegalArgumentException("Token transfer should have no inputs.");
            if (tx.getOutputStates().size() != 1) throw new IllegalArgumentException("Token transfer should have one output.");

            if (tx.outputsOfType(TokenState.class).size() != 1) throw new IllegalArgumentException("Token transfer output should be an TokenState.");

            final TokenState tokenStateOutput = tx.outputsOfType(TokenState.class).get(0);
            if (tokenStateOutput.getAmount() <= 0) throw new IllegalArgumentException("Token tranfer output should have a positive amount.");

            if (tokenStateOutput.getIssuer().equals(tx.getOutputStates())) throw new IllegalArgumentException("Não posso criar Issue para o mesmo output.");

            // Checking the transaction's required signers.
            final List<PublicKey> requiredSigners = command.getSigners();
            if (!(requiredSigners.contains(tokenStateOutput.getIssuer().getOwningKey()))) {
                throw new IllegalArgumentException("Token transfer should have output's owner as a required signer.");
            }

        } else if (command.getValue() instanceof Commands.Transfer) {
            throw new IllegalArgumentException("Transfer not implemented yet ");
        } else if (command.getValue() instanceof Commands.Exit) {
            throw new IllegalArgumentException("Exit not implemented yet ");
        } else {
            throw new IllegalArgumentException("Unrecognised command.");
        }
    }
}