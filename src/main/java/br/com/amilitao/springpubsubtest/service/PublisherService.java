package br.com.amilitao.springpubsubtest.service;

import br.com.amilitao.springpubsubtest.controller.dto.LoteMensagemDto;
import br.com.amilitao.springpubsubtest.controller.dto.MensagemDto;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.batching.BatchingSettings;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.threeten.bp.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class PublisherService {

    @Autowired
    private PubSubTemplate pubSubTemplate;

    @Value("${spring.pubsub.test.project-id}")
    private String projectId;

    @Value("${spring.pubsub.test.topic-id}")
    private String topicId;

    public void publish(MensagemDto mensagemDto){
        pubSubTemplate.publish(topicId, mensagemDto);
    }


    public void publishWithBatchSettings(LoteMensagemDto lote)
            throws IOException, ExecutionException, InterruptedException {


        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;
        List<ApiFuture<String>> messageIdFutures = new ArrayList<>();

        try {
            // Batch settings control how the publisher batches messages
            long requestBytesThreshold = 5000L; // default : 1000 bytes
            long messageCountBatchSize = 10L; // default : 100 message

            Duration publishDelayThreshold = Duration.ofMillis(100); // default : 1 ms

            // Publish request get triggered based on request size, messages count & time since last
            // publish, whichever condition is met first.
            BatchingSettings batchingSettings =
                    BatchingSettings.newBuilder()
                            .setIsEnabled(true)
                            .setElementCountThreshold(messageCountBatchSize)
                            .setRequestByteThreshold(requestBytesThreshold)
                            .setDelayThreshold(publishDelayThreshold)
                            .build();

            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).setBatchingSettings(batchingSettings).build();

            // schedule publishing one message at a time : messages get automatically batched

            for (MensagemDto mensagemDto : lote.getMensagens()) {

                Gson gson = new Gson();
                String json = gson.toJson(mensagemDto);

                var data = ByteString.copyFromUtf8(json);
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

                // Once published, returns a server-assigned message id (unique within the topic)
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
                messageIdFutures.add(messageIdFuture);
            }
        } finally {
            // Wait on any pending publish requests.
            List<String> messageIds = ApiFutures.allAsList(messageIdFutures).get();

            System.out.println("Published " + messageIds.size() + " messages with batch settings.");

            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }
}
