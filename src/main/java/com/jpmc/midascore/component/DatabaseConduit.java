package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
public class DatabaseConduit {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    // 🔥 Configurable API URL
    @Value("${incentive.api.url:http://localhost:8080/incentive}")
    private String incentiveUrl;

    public DatabaseConduit(UserRepository userRepository,
                           TransactionRecordRepository transactionRecordRepository,
                           RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
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

        // 🔍 Fetch users
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // ❌ Validation
        if (sender == null || recipient == null) {
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            return;
        }

        // 🌐 Call Incentive API
        Incentive incentive = restTemplate.postForObject(
                incentiveUrl,
                transaction,
                Incentive.class
        );

        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0;

        // 💰 Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());

        recipient.setBalance(
                recipient.getBalance() + transaction.getAmount() + incentiveAmount
        );

        // 📝 Save transaction
        TransactionRecord record =
                new TransactionRecord(transaction.getAmount(), incentiveAmount, sender, recipient);

        transactionRecordRepository.save(record);

        // 💾 Save updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        // 🧪 Debug (Task 4 - wilbur)
        if (sender.getName().equals("wilbur")) {
            System.out.println("WILBUR (sender): " + sender.getBalance());
        }

        if (recipient.getName().equals("wilbur")) {
            System.out.println("WILBUR (recipient): " + recipient.getBalance());
        }
    }
}