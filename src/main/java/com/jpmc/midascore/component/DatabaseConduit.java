package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(UserRepository userRepository,
                           TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }
    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-core-group"
    )
    @Transactional
    public void listen(Transaction transaction) {

        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // Validation
        if (sender == null || recipient == null) {
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            return;
        }
        
        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // Save transaction record
        TransactionRecord record =
                new TransactionRecord(transaction.getAmount(), sender, recipient);

        transactionRecordRepository.save(record);

        userRepository.save(sender);
        userRepository.save(recipient);
        if (sender.getName().equals("waldorf")) {
            System.out.println("WALDORF (sender): " + sender.getBalance());
        }

        if (recipient.getName().equals("waldorf")) {
            System.out.println("WALDORF (recipient): " + recipient.getBalance());
        }
    }

}