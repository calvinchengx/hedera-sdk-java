package com.hedera.sdk.examples;

import com.hedera.sdk.*;
import com.hedera.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.sdk.file.FileAppendTransaction;
import com.hedera.sdk.file.FileCreateTransaction;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("Duplicates")
public final class AppendToFile {
    public static void main(String[] args) throws InterruptedException {
        var env = Dotenv.load();

        var operatorKey = Ed25519PrivateKey.fromString(Objects.requireNonNull(env.get("OPERATOR_SECRET")));

        var network = Objects.requireNonNull(env.get("NETWORK"));
        var node = AccountId.fromString(Objects.requireNonNull(env.get("NODE")));

        var client = new Client(Map.of(node, network));

        // First we create a file
        var fileContents = "Hedera hashgraph is".getBytes();

        var txId = new TransactionId(new AccountId(2));

        var fileTx = new FileCreateTransaction(client).setTransactionId(txId)
            .setNodeAccount(node)
            .setExpirationTime(
                Instant.now()
                    .plus(Duration.ofSeconds(2592000))
            )
            .addKey(operatorKey.getPublicKey())
            .setContents(fileContents)
            .sign(operatorKey)
            .sign(operatorKey);

        var res = fileTx.execute();

        System.out.println("File create transaction: " + res.toString());

        // Sleep for 4 seconds
        Thread.sleep(4000);

        // Next we append to the file
        var additionalFileContents = " great!".getBytes();

        var appendFileTxId = new TransactionId(new AccountId(2));

        var appendFileTx = new FileAppendTransaction(client).setTransactionId(appendFileTxId)
            .setNodeAccount(node)
            .setContents(additionalFileContents)
            // first signature is the owner of the file
            .sign(operatorKey)
            // second signature is the transaction payer
            .sign(operatorKey);

        res = appendFileTx.execute();

        System.out.println("File append transaction: " + res.toString());

        // Sleep for 4 seconds
        Thread.sleep(4000);

        var query = new TransactionReceiptQuery(client).setTransaction(txId);

        var receipt = query.execute();
        var receiptStatus = receipt.getStatus();

        System.out.println("status: " + receiptStatus.toString());
    }
}
